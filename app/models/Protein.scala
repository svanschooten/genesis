package models

import scala.collection.immutable.List

abstract class Protein() {
    var children:List[Protein] = List()
    def addProtein(p: Protein) { children = p::children }
}

case class ProteinActivator(val name:String, val id: Int, val parentIds: List[Int],
    val ks: List[Double], val ds: List[Double], val km: Double = 2.0, val n: Int = 4) extends Protein
case class ProteinRepressor(val name:String, val id: Int, val parentIds: List[Int],
    val ks: List[Double], val ds: List[Double], val km: Double = 2.0, val n: Int = 4) extends Protein
