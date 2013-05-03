package models

import scala.collection.immutable.List
import anorm._

abstract class Protein(parents: List[Protein], ks: List[Double], ds: List[Double], km: Double, n: Int) {
    var children:List[Protein] = List()
    def addProtein(p: Protein) { children = p::children }
}

case class ProteinActivator(val name:String, val id: Int, parents: List[Protein], ks: List[Double], ds: List[Double], km: Double = 2.0, n: Int = 4)
    extends Protein(parents, ks, ds, km, n){
  parents.foreach(p => p.addProtein(this))
}

case class ProteinRepressor(val name:String, val id: Int, parents: List[Protein], ks: List[Double], ds: List[Double], km: Double = 2.0, n: Int = 4)
    extends Protein(parents, ks, ds, km, n){
  parents.foreach(p => p.addProtein(this))
}
