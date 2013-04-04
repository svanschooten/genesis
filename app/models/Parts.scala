package models

case class Gene(val k2:Double, val d: (Double,Double))

sealed abstract class Part

case class NotPromotor(val tf: Gene, val k1:Double, val Km: Double, val n: Int) extends Part
case class AndPromotor(val tf1: Gene, val tf2: Gene, val k1: Double, val Km: Double, val n: Int) extends Part {
    require(!tf1.equals(tf2))
}