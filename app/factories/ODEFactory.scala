package factories

import scala.collection.immutable.List

import scalation.Derivatives.DerivativeV
import scalation.VectorD
import scalation.DoubleWithExp._
import models._

object ODEFactory {
    var c = Array[Double]()

    def init(conc: List[Double]) {
        c = new Array(conc.length*2)
        for(i <- 0 until conc.length)
            c(i*2) = conc(i)
    }

    //TODO disgusting; figure out how to give a proper return type
    def mkODEs(proteins: ProteinChain):List[Any] = {
        for(protein <- proteins.startNodes) yield protein match {
            case ProteinActivator(_,id,pids,ks,ds,km,n) =>
                List(  (t: Double, c: VectorD) => ks(0) * foldConcentrations(pids) ~^ n/(km ~^ n + foldConcentrations(pids) ~^ n) - ds(0) * c(id+1),
                       (t: Double, c: VectorD) => ks(1)*c(id+1) -ds(1)*c(id) ) ::: mkODEs(new ProteinChain(protein.children, List[List[Double]]()))
            case ProteinRepressor(_,id,pids,ks,ds,km,n) => (t: Double, c: VectorD) =>
                List( (t: Double, c: VectorD) => ks(0) * km ~^ n / ( km ~^ n + foldConcentrations(pids) ~^ n) - ds(0) * c(id+1),
                      (t: Double, c: VectorD) => ks(1) * c(id+1) - ds(1) * c(id) )
            case _ => List()//throw new IllegalArgumentException("Unsupported protein type.")
        }
    }

    def foldConcentrations(indexes: List[Int]): Double = indexes.foldLeft(1.0)( (x,y) => x*c(y))
}