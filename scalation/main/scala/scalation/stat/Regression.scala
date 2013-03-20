
/**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * @author  John Miller
 * @version 1.0
 * @date    Wed Aug 26 18:41:26 EDT 2009
 * @see     LICENSE (MIT style license file).
 */

package scalation.stat

import math.pow

import scalation.math.Matrices.MatrixD
import scalation.math.Vectors.VectorD

/**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * The Regression object supports multiple linear regression.
 */
object Regression
{
    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Evaluate the formula y = x * b.
     * @param x  the design matrix augmented with a first column of ones
     * @param b  the parameter vector
     */
    def eval (x: MatrixD, b: VectorD): VectorD =
    {
        x * b
    } // eval

    /**:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * Fit the parameter vector (b-vector) in the regression equation y = x * b + e
     * using the least squares method.
     * @param x  the design matrix augmented with a first column of ones
     * @param y  the response vector
     */
    def fit (x: MatrixD, y: VectorD): Tuple2 [VectorD, Double] =
    {
        val b    = (x.t * x).inverse * x.t * y   // parameter vector
        val e    = y - x * b                     // error vector
        val sse  = e.normSq                      // sum of squared errors
        val ssto = y.normSq - (pow (y.sum, 2)) / y.dim
        val r2   = (ssto - sse) / ssto           // coefficient of determination
        (b, r2)
    } // fit

} // Regression object


/**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * Object to test Regression object (y = x * b = b0 + b1*x1 + b2*x2).
 */
object RegressionTest extends App
{
    // five data points: constant term, x1 coordinate, x2 coordinate
    val x = new MatrixD ((5, 3), 1., 36.,  66.,               // 5-by-3 matrix
                                 1., 37.,  68.,
                                 1., 47.,  64.,
                                 1., 32.,  53.,
                                 1.,  1., 101.)
    // five data points: y coordinate
    val y = new VectorD (745., 895., 442., 440., 1598.)

    val tp = Regression.fit (x, y)
    val yp = Regression.eval (x, tp._1)
    println ("b  = " + tp._1)
    println ("r2 = " + tp._2)
    println ("y  = " + y)
    println ("yp = " + yp)

} // RegressionTest object

/**::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * Object to test Regression object (y = x * b = b0 + b1*x1 + b2*x2).
 */
object RegressionTest2 extends App
{
    // four data points: constant term, x1 coordinate, x2 coordinate
    val x = new MatrixD ((4, 3), 1., 1., 1.,                  // 4-by-3 matrix
                                 1., 1., 2.,
                                 1., 2., 1.,
                                 1., 2., 2.)
    // four data points: y coordinate
    val y = new VectorD (6., 8., 7., 9.)

    val tp = Regression.fit (x, y)
    val yp = Regression.eval (x, tp._1)
    println ("b  = " + tp._1)
    println ("r2 = " + tp._2)
    println ("y  = " + y)
    println ("yp = " + yp)

} // RegressionTest2 object

