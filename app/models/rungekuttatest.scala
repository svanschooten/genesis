package models

import play.api.libs.json._

import scalation.{VectorD, RungeKutta}
import scalation.Derivatives.DerivativeV

/**
 * ODE solving class containing some testing values but also contains an actual ODE solving method using the RungeKutta method.
 * Also contains an method to generate the JSON object containing the results and a method to return the results as a List[Sting] object.
 */
case class Rungekuttatest (){
  
  val t0 = 0.0                         // initial time
  val tf = 4.0                         // final time
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

  /**
   * Testing method for the standard contained test values.
   * @return A list of Strings that contain the results.
   */
  def test(): List[String] = {
      // concentrations    H2, O2, O,   H,  OH, H2O
      //                   0   1   2    3   4   5
      val results = testResults()
      Rungekuttatest.printCVec(results, t0, dt)
  }

  /**
   * Generalized test method to get the results of the ODE in this test object.
   * @return The list of tuples containing the results.
   */
  def testResults(): List[VectorD] = {
      val c = new VectorD (Array(4.0, 6.0, 0.0, .02, 0.1, 0.8))
      Rungekuttatest.solveFolding(t0, tf, dt, odes, c.clone())
  }

  def genJson = {
    import models._
    val A = CodingSeq("A",(1,1))
    val B = CodingSeq("B",(1,1))
    val C = CodingSeq("C",(1,1))
    val D = CodingSeq("D",(1,1))
    val E = CodingSeq("E",(1,1))
    val F = CodingSeq("F",(1,1))
    val notA = NotGate(A,C)
    val notAandB = AndGate((C,B),D)
    val notAandBandE = AndGate((D,E),F)
    val net = new Network(List(A,B,E))
    net.simJson(1.0)
    //Rungekuttatest.resultsToJson(t0, tf, dt, testResults())
  }

}

object Rungekuttatest {

  /**
   * A helper method for the mapping of results to JSON.
   * @param l The list to be mapped.
   * @return the mapped list of lists.
   */
  private def convert(l: List[VectorD]): List[List[Double]] = l.map(_.getConts)

  /**
   * This method maps all the results in the given list to a JSON object.
   * @param t0 Starting time.
   * @param tf Ending time.
   * @param dt Time increments.
   * @param results The list with result vectors.
   * @return A JSON object containing the results.
   */
  protected def resultsToJson(t0: Double, tf: Double, dt: Double, results: List[VectorD]): JsValue = Json.toJson(
    Map("t" -> Json.toJson((t0 to tf by dt).toList),
      "vectors" -> Json.toJson(convert(results)),
      "names" -> Json.toJson((0 to results.head.length).toList)
    )
  )

  /**
   * Generalised method for solving the ODE's given and returns a JSON value.
   * @param t0 The starting time.
   * @param tf The end time for the simulations.
   * @param dt The time increments.
   * @param odes The Array of methods the be solved.
   * @param cVec The initial concentration vector.
   * @return A JSON object containing all the results.
   */
  def solve(t0: Double, tf: Double, dt: Double, odes: Array [DerivativeV], cVec: VectorD): JsValue = {
    val results = solveFolding(t0, tf, dt, odes, cVec)
    resultsToJson(t0, tf, dt, results)
  }

  /**
   * Used to return a formatted List[String] from the result list.
   * The format is:
   * > at t = 'specific time in 3 decimals' c = ['all elements in the vector for that specific time']
   * @param vecs The list of results.
   * @param t0 Starting time.
   * @param dt The time increments.
   * @return The List[String] as the format is described.
   */
  def printCVec(vecs: List[VectorD], t0: Double, dt: Double): List[String] = {
    vecs match {
      case h::t => "> at t = " + "%6.3f".format (t0) + " c = " + h.toStringBare :: printCVec(t, t0 + dt, dt)
      case Nil => Nil
    }
  }

  /** Solve a list of ODEs, given start concentrations and time span and return a list of
   * vectors with the concentrations at each point in time; time is sliced according to the
   * step size
   * @param t0 The final time (time span will be [0.0, t])
   * @param dt The step size
   * @param odes The list of ODE functions
   * @param cVec The initial concentrations
   * @return The list of concentration at each time increment 0.0+dt*i
   */
  private def solveFolding(t0: Double, tf: Double, dt: Double, odes: Array [DerivativeV], cVec: VectorD): List[VectorD] = {
    (t0 to tf by dt).toList.foldLeft(List[VectorD]())((l, step) => l match {
      case h::t => RungeKutta.integrateVV(odes, cVec.clone(), step, 0.0, dt) :: h :: t
      case Nil => cVec :: Nil
    }).reverse
  }

}
