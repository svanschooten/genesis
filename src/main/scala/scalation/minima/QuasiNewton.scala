
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.0
 *  @date    Fri Sep 30 13:37:32 EDT 2011
 *  @see     LICENSE (MIT style license file).
 *------------------------------------------------------------------------------
 *  @see The Superlinear Convergence of a Modified BFGS-Type Method for Unconstrained Optimization 
 *  @see On the Robustness of Conjugate-Gradient Methods and Quasi-Newton Methods
 *  @see Limited Memory BFGS for Nonsmooth Optimization
 *  @see http://en.wikipedia.org/wiki/BFGS_method
 */

package scalation.minima

import math.{abs, max, pow}

import scalation.math.Calculus.{gradient, gradientD}
import scalation.math.Matrices.MatrixD
import scalation.math.Vectors.{FunctionV2S, VectorD}
import scalation.util.Error

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** Broyden–Fletcher–Goldfarb–Shanno (BFGS) Quasi-Newton Algorithm for solving
 *  Non-Linear Programming (NLP) problems.  BFGS determines a search direction by
 *  deflecting the steepest descent direction vector (opposite the gradient) by
 *  multiplying it by a matrix that approximates the inverse Hessian.  Note, this
 *  implementation may be step up to work with the matrix 'b' (approximate Hessian)
 *  or directly with the 'binv' matrix (the inverse of b).
 *
 *  minimize    f(x)
 *  subject to  g(x) <= 0
 *
 *  @param f  the objective function to be minimized
 *  @param g  the constraint function to be satisfied, if any
 */
class QuasiNewton (f: FunctionV2S [Double], g: FunctionV2S [Double] = null)
      extends Error
{
    private val DEBUG    = true              // the debug flag
    private val EPSILON  = 1.E-10            // a value that is close to zero
    private val MAX_ITER = 100               // maximum number of iterations
    private val WEIGHT   = 1000.             // weight on penalty for constraint violation

    private var df: Array [FunctionV2S [Double]] = null   // array of partials
    private var b: MatrixD    = null         // approx. Hessian matrix (use b or binv)
    private var binv: MatrixD = null         // inverse of approx. Hessian matrix
    private var bfgs          = true         // use BFGS (true) or Steepest-Descent (false)

    type Pair = Tuple2 [VectorD, VectorD]    // pair of vectors

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Use the Steepest-Descent algorithm rather than the default BFGS algorithm.
     */
    def setSteepest () { bfgs = false }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Update the 'b' matrix, whose inverse is used to deflect -gradient to a
     *  better direction than steepest descent (-gradient).
     *  @param s  the step vector (next point - current point)
     *  @param y  the difference in the gradients (next - current)
     *
    def updateB (s: VectorD, y: VectorD)
    {
        var sy = s dot y                     // dot product of s and y
        if (abs (sy) < EPSILON) sy = EPSILON
        val sb = s * b
        b += (y outer y) / sy - (sb outer sb) / (sb dot s)
    } // updateB
     */

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Update the 'binv' matrix, which is used to deflect -gradient to a better
     *  search direction than steepest descent (-gradient).
     *  Compute the 'binv' matrix directly using the Sherman–Morrison formula.
     *  @see http://en.wikipedia.org/wiki/BFGS_method
     *  @param s  the step vector (next point - current point)
     *  @param y  the difference in the gradients (next - current)
     */
    def updateBinv (s: VectorD, y: VectorD)
    {
        var sy = s dot y                     // dot product of s and y
        if (abs (sy) < EPSILON) sy = EPSILON
        val binvy = binv * y
        binv +=  ((s outer s) * (sy + (binvy dot y))) / (sy * sy) -
                 ((binvy outer s) + (s outer binvy)) / sy
    } // updateBinv

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Set the partial derivative functions.  If these functions are available,
     *  they are more efficient and more accurate than estimating the values
     *  using difference quotients (the default approach).
     *  @param partials  the array of partial derivative functions
     */
    def setDerivatives (partials: Array [FunctionV2S [Double]])
    {
        if (g != null) flaw ("setDerivatives", "only works for unconstrained problems")
        df = partials       // use given functions for partial derivatives
    } // setDerivatives

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** The objective function f re-scaled by a weighted penalty, if constrained.
     *  @param x  the coordinate values of the currrent point
     */
    def fg (x: VectorD): Double =
    {
        if (g == null) {                  // unconstrained
            f(x)
        } else {                          // constrained, g(x) <= 0
            val penalty = max (g(x), 0.)
            f(x) * (1. + WEIGHT * penalty * penalty)
        } // if
    } // fg

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Perform an inexact line search in the dir direction, returning the
     *  distance (z) to move in that direction.
     *  @param x    the current point
     *  @param dir  the direction to move in
     */
    def lineSearch (x: VectorD, dir: VectorD): Double =
    {
        def f_1D (z: Double): Double = fg(x + dir * z)  // create a 1D function
        val ls = new WolfeLS (f_1D)                     // Wolfe line search
        ls.search ()                                    // perform a line search
    } // lineSearch

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Solve the following Non-Linear Programming (NLP) problem using BFGS:
     *  min { f(x) | g(x) <= 0 }.  To use explicit functions for gradient,
     *  replace 'gradient (fg, x._1 + s)' with 'gradientD (df,  x._1 + s)'.
     *  @param x0  the starting point 
     */
    def solve (x0: VectorD): VectorD =
    {
        var x:  Pair = (x0, gradient (fg, x0))        // current (point, gradient)
        var xx: Pair = (null, null)                   // next (point, gradient)
        var s:  VectorD = null                        // step vector
        var dir = -x._2                               // initial direction is -gradient

        binv = new MatrixD (x0.dim, 1., 0.)           // inverse of approx. Hessian matrix
//      b    = new MatrixD (x0.dim, 1., 0.)           // approx. Hessian matrix (either use b or binv)

        for (k <- 1 to MAX_ITER if x._2.normSq > EPSILON) {
            s  = dir * lineSearch (x._1, dir)         // update step vector
            xx = (x._1 + s, gradient (fg, x._1 + s))  // compute the next point
            if (bfgs) {
                dir = -(binv * xx._2)                 // next search direction using BFGS and 'binv'
                updateBinv (s, xx._2 - x._2)          // update the deflection matrix 'binv'
//              dir = -(b.inverse * xx._2)            // next search direction using BFGS and 'b'
//              updateB (s, xx._2 - x._2)             // update the deflection matrix 'b'
            } else {
                dir = -xx._2                          // next search direction using Steepest-Descent
            } // if
            if (DEBUG) println ("solve: (k = " + k + ") move from " + x._1 + " to " + xx._1
                              + " where fg(xx._1) = " + fg(xx._1))
            x = xx                                    // make the next point, the current point
        } // for
        x._1                                          // return the current point
    } // solve

} // QuasiNewton class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** This object is used to test the QuasiNewton class.
 */
object QuasiNewtonTest extends App
{
    def x0 = new VectorD (2)

    println ("\nMinimize: (x_0 - 3)^2 + (x_1 - 4)^2 + 1")
    def f (x: VectorD): Double = (x(0) - 3.) * (x(0) - 3.) + (x(1) - 4.) * (x(1) - 4.) + 1.
    var solver = new QuasiNewton (f)
    var x = solver.solve (x0)
    println ("optimal solution x = " + x + " with an objective value f(x) = " + f(x))

    println ("\nMinimize: x_0^4 + (x_0 - 3)^2 + (x_1 - 4)^2 + 1")
    def g (x: VectorD): Double = pow (x(0), 4.) + (x(0) - 3.) * (x(0) - 3.) + (x(1) - 4.) * (x(1) - 4.) + 1.
    solver = new QuasiNewton (g)
    x = solver.solve (x0)
    println ("optimal solution x = " + x + " with an objective value g(x) = " + g(x))

} // QuasiNewtonTest object

