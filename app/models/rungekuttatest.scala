package models

import scalation.{VectorD, RungeKutta}
import scalation.Derivatives.DerivativeV

case class Rungekuttatest (){
  
	val t0 = 0.0                         // initial time
    val tf = 5.0                         // final time
    val n  = 1001                        // number of time steps

    val kf = (1.0,  1.0,  0.5)     // forward reaction rates
    val kb = (0.02, 0.02, 0.01)    // backward reaction rates

    // define the system of Ordinary Differential Equations (ODEs)

    // d[H2]/dt                         = - kf1 [H2] [O] + kb1 [H] [OH] - kf3 [H2] [OH] + kb3 [H2O] [H]
    def dh2_dt(t: Double, c: VectorD)  = -kf._1*c(0)*c(2) + kb._1*c(3)*c(4) - kf._3*c(0)*c(4) + kb._3*c(5)*c(3) 

    // d[O2]/dt                         = - kf2 [H] [O2] + kb2 [O] [OH] (2)
    def do2_dt(t: Double, c: VectorD)  = -kf._2*c(3)*c(1) + kb._2*c(2)*c(4)

    // d[O]/dt                          = - kf1 [H2] [O] + kb1 [H] [OH] + kf2 [H] [O2] - kb2 [O] [OH]
    def do_dt(t: Double, c: VectorD)   = -kf._1*c(0)*c(2) + kb._1*c(3)*c(4) + kf._2*c(3)*c(1) - kb._2*c(2)*c(4)

    // d[H]/dt                          = + kf1 [H2] [O] - kb1 [H] [OH] - kf2 [H] [O2] + kb2 [O] [OH] + kf3 [H2] [OH] - kb3 [H2O] [H]
    def dh_dt(t: Double, c: VectorD)   =  kf._1*c(0)*c(2) - kb._1*c(3)*c(4) - kf._2*c(3)*c(1) + kb._2*c(2)*c(4) + kf._3*c(0)*c(4) - kb._3*c(5)*c(3)

    // d[OH]/dt                         = + kf1 [H2] [O] - kb1 [H] [OH] + kf2 [H] [O2] - kb2 [O] [OH] - kf3 [H2] [OH] + kb3 [H2O] [H]
    def doh_dt(t: Double, c: VectorD)  =  kf._1*c(0)*c(2) - kb._1*c(3)*c(4) + kf._2*c(3)*c(1) - kb._2*c(1)*c(4) - kf._3*c(0)*c(4) + kb._3*c(5)*c(3)

    // d[H2O]/dt                        = + kf3 [H2] [OH] - kb3 [H2O] [H]
    def dh2o_dt(t: Double, c: VectorD) =  kf._3*c(0)*c(4) - kb._3*c(5)*c(3)

    val odes: Array [DerivativeV] = Array (dh2_dt, do2_dt, do_dt, dh_dt, doh_dt, dh2o_dt)

    val dt = tf / n                                 // time step
    
    def test(): List[String] = {
        // concentrations    H2, O2, O,   H,  OH, H2O
        //                   0   1   2    3   4   5
        val c = new VectorD (Array(4.0, 6.0, 0.0, .02, 0.1, 0.8))

        //val results = Rungekuttatest.solveFolding(t0, dt, odes, c)
       System.out.println(tf / dt)
        val results = c :: Rungekuttatest.solveRecursive(tf, dt, odes, c.clone())
        Rungekuttatest.printCVec(results, t0, dt)
    }
}

object Rungekuttatest {

  def printCVec(vecs: List[VectorD], t0: Double, dt: Double): List[String] = {
    vecs match {
      case h::t => "> at t = " + "%6.3f".format (t0) + " c = " + h :: printCVec(t, t0 + dt, dt)
      case Nil => List()
      case _ => throw new IllegalArgumentException
    }
  }

  private def solveSingle(odes: Array [DerivativeV], cVec: VectorD, dt: Double): VectorD = {
    RungeKutta.integrateVV (odes, cVec, dt)
  }

  /** Solve a list of ODEs, given start concentrations and time span and return a list of
   * vectors with the concentrations at each point in time; time is sliced according to the
   * step size
   * @param t The final time (time span will be [0.0, t])
   * @param dt The step size
   * @param odes The list of ODE functions
   * @param cVec The initial concentrations
   * @return The list of concentration at each time increment 0.0+dt*i
   */
  def solveFolding(t: Double, dt: Double, odes: Array [DerivativeV], cVec: VectorD): List[VectorD] = {
    (0.0 to t by dt).toList.foldLeft(List[VectorD]())((l: List[VectorD], step: Double) => l match {
      case h::t => RungeKutta.integrateVV(odes, cVec.clone(), step, 0.0, dt) :: h :: t
      case Nil => cVec :: Nil
    }).reverse
  }

  /** Solves the list of ODE's recursively and creates a List of VectorD elements containing the concentrations on specific
  * points in time. ODE's are solved using the RungeKutta method.
  * @param time Time over which the ODE's must be solved.
  * @param dt Time differences for which the ODE's are to be solved.
  * @param odes The Array of pointers to the methods to be differentiated
  * @param cVec The concentration vector used in the ODE's
  * @return The list of concentrations.
  */
  def solveRecursive(time: Double, dt: Double, odes: Array [DerivativeV], cVec: VectorD): List[VectorD] = {
    require(math.abs(time / dt) > 1000.0,"Resolution too high, provide smaller step size.")
    if (time <= 0.0)
      List()
    else
      solveSingle(odes, cVec, dt) :: solveRecursive(time - dt, dt, odes, res.clone())
  }
}
