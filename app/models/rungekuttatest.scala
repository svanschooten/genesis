package models

import play.api.libs.json._

import scalation.{VectorD, RungeKutta}
import scalation.Derivatives.DerivativeV

case class Rungekuttatest (){
  
	val t0 = 0.0                         // initial time
    val tf = 5.0                         // final time
    val n  = 200                        // number of time steps

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
        val results = testResults()
        printTestBare()
        Rungekuttatest.printCVec(results, t0, dt)
    }

    def testResults(): List[(VectorD,VectorD)] = {
        val c = new VectorD (Array(4.0, 6.0, 0.0, .02, 0.1, 0.8))
        val b = Rungekuttatest.zeros(c)
        (b,c) :: Rungekuttatest.solveRecursive(t0, tf, dt, odes.zip(odes), b.clone(), c.clone())
        //c :: Rungekuttatest.solveFolding(t0, dt, odes, c)
    }

    def printTestBare() = {
      val c = new VectorD (Array(4.0, 6.0, 0.0, .02, 0.1, 0.8))
      val b = Rungekuttatest.zeros(c)
      val results = (b,c) :: Rungekuttatest.solveRecursive(t0, tf, dt, odes.zip(odes), b.clone(), c.clone())
      val printRes = Rungekuttatest.printBareCVec(results, t0, dt)
      Rungekuttatest.print(printRes)
    }


    def convert(l: List[VectorD]): List[List[Double]] = l.map(_.getConts)

    def genJson(): JsValue = Json.toJson(
        Map("t" -> Json.toJson((t0 to tf by dt).toList),
            "vectors" -> Json.toJson(convert(testResults().map(_._2)))
        )
    )
}

object Rungekuttatest {

  private def zeros(v: VectorD): VectorD = new VectorD(Array.fill(v.length)(0.0))

  def getJsonTest: JsValue = {
    Rungekuttatest().genJson()
  }

  def print(results: List[String]) = {
    results.foreach((s: String) => System.out.println(s))
  }

  def printCVec(vecs: List[(VectorD,VectorD)], t0: Double, dt: Double): List[String] = {
    vecs match {
      case h::t => "> at t = " + "%6.3f".format (t0) + " c = " + h._2 :: printCVec(t, t0 + dt, dt)
      case Nil => List()
      case _ => throw new IllegalArgumentException
    }
  }

  def printBareCVec(vecs: List[(VectorD,VectorD)], t0: Double, dt: Double): List[String] = {
    vecs match {
      case h::t => ">t:" + "%6.3f".format (t0) + " c:" + h._2.toStringBare + "" :: printBareCVec(t, t0 + dt, dt )
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
  def solveFolding(t: Double, dt: Double, odes: Array [(DerivativeV,DerivativeV)], cVec: VectorD): List[(VectorD,VectorD)] = {
    (0.0 to t by dt).toList.foldLeft(List[(VectorD,VectorD)]())((l: List[(VectorD,VectorD)], step: Double) => l match {
      case h::t => (RungeKutta.integrateVV(odes.map(_._1), zeros(cVec), step, 0.0, dt),
                    RungeKutta.integrateVV(odes.map(_._2), cVec.clone(), step, 0.0, dt)) :: h :: t
      case Nil => (new VectorD(Array.fill(cVec.length)(0.0)),cVec) :: Nil
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
  def solveRecursive(t0: Double, time: Double, dt: Double, odes: Array [(DerivativeV,DerivativeV)], bVec: VectorD, cVec: VectorD): List[(VectorD,VectorD)] = {
    require(math.abs(time / dt) <= 1000.0,"Resolution too high, provide smaller step size.")
    if (time <= t0) {
      List()
    } else {
      val res = (solveSingle(odes.map(_._1), bVec, dt),solveSingle(odes.map(_._2), cVec, dt))
      res :: solveRecursive(t0, time - dt, dt, odes, res._1.clone(), res._2.clone())
    }
  }
}
