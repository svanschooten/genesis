package scalation

import math.{abs, E, pow, round}

/**  Given an unknown, time-dependent function y(t) governed by an Ordinary
 *  Differential Equation (ODE) of the form y(t)' = f(t, y) where ' is d/dt,
 *  compute y(t) using a 4th-order Runge-Kutta Integrator (RK4).  Note: the
 *  integrateV method for a system of separable ODEs is mixed in from the
 *  Integrator trait.
 */
object RungeKutta extends Integrator {

    import Derivatives.{Derivative, DerivativeV}

    /** Compute y(t) governed by a differential equation using numerical integration
     *  of the derivative function f(t, y) using a 4th-order Runge-Kutta method to
     *  return the value of y(t) at time t.
     *  @param f     the derivative function f(t, y) where y is a scalar
     *  @param y0    the value of the y-function at time t0, y0 = y(t0)
     *  @param t     the time value at which to compute y(t)
     *  @param t0    the initial time
     *  @param step  the step size
     */
    def integrate (f: Derivative, y0: Double, t: Double,
                   t0: Double = 0.0, step: Double = defaultStepSize): Double = {
        // time interval
        val t_t0 = t - t0
        // number of steps
        val steps = round(t_t0 / step).asInstanceOf[Int]
        // adjusted step size
        var h = t_t0 / steps.asInstanceOf[Double]
        // initialize ith time ti to t0
        var ti = t0
        // initialize y = f(t) to y0
        var y = y0

        var a,b,c,d = 0.0

        for (i <- 1 to steps) {
            // take the next step
            ti += h
            // don't go past t
            if(ti > t) {
                h -= ti - t
                ti = t
            }

            a = f(ti, y)
            b = f(ti + h/2.0, y + a/2.0)
            c = f(ti + h/2.0, y + b/2.0)
            d = f(ti + h, y + c)
            y += h/6.0 * (a + 2*b + 2*c + d)

            if(abs (y) > Double.MaxValue / 10.0)
                throw new RuntimeException("Probable overflow since y = "+y)
        }
        y
    }

    /** Compute y(t), a vector, governed by a system of differential equations using
     *  numerical integration of the derivative function f(t, y) using a 4th-order
     *  Runge-Kutta method to return the value of y(t) at time t.
     *  @param f     the array of derivative functions [f(t, y)] where y is a vector
     *  @param y0    the value of the y-function at time t0, y0 = y(t0)
     *  @param t     the time value at which to compute y(t)
     *  @param t0    the initial time
     *  @param step  the step size
     */
    def integrateVV (f: Array[DerivativeV], y0: VectorD, t: Double,
                     t0: Double = 0.0, step: Double = defaultStepSize): VectorD = {
        // time interval
        val t_t0 = t - t0
        // number of steps
        val steps: Int = (round (t_t0 / step)).asInstanceOf[Int]
        // adjusted step size
        var h = t_t0 / steps.asInstanceOf[Double]
        // initialize ith time ti to t0
        var ti = t0
        // initialize y = f(t) to y0
        val y = y0

        for(i <- 1 to steps) {
            // take the next step
            ti += h
            // don't go past t
            if(ti > t) {
                h -= ti - t
                ti = t
            }

            val a = new VectorD(f.map(fun=>fun(ti,y)))
            val b = new VectorD(f.map(fun=>fun(ti + h/2.0, y + a * h/2.0)))
            val c = new VectorD(f.map(fun=>fun(ti + h/2.0, y + b * h/2.0)))
            val d = new VectorD(f.map(fun=>fun(ti + h, y + c * h)))

            for(j <- 0 until y.dim)
                y(j) += h/6.0 * (a(j) + 2.0*b(j) + 2.0*c(j) + d(j))

            if(abs (y(0)) > Double.MaxValue / 10.0)
                throw new RuntimeException("Probable overflow because y = "+y)
        }
        y
    }

}
