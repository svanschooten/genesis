package models

case class Gene(val k2:Double, val d: (Double,Double))

/**
 *  superclass for gates
 */ 
sealed abstract class Part

/**
 *  class for NOT gates
 *  only include output gene; input is not relevant: only input concentrations will be fed to the ode function
 *  the other parameters determine the specific type of not gate (i.e. the specific TF)
 */
case class NotGate(val output: Gene, val k1: Double, val Km: Double, val n: Int) extends Part

/**
 *  class for AND gates
 *  the only difference with NOT gates is what kind of ODE function will be generated for this class
 */
case class AndGate(val output: Gene, val k1: Double, val Km: Double, val n: Int) extends Part