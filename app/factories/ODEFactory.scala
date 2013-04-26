package factories

import scala.collection.immutable.List

import scalation.VectorD
import scalation.DoubleWithExp._
import models._

object ODEFactory {

    /**
     *  ODE: dy/dt = f(t,y)
     *  t is the double, y is the VectorD
     */
    type ODE = (Double, VectorD) => Double

    /**
     *  We're going to make a list of triplets:
     *  each triplet will have one function for the mRNA concentration
     *  and one function for the Protein concentration
     *  and one double for the initial protein concentration of the input protein
     *  The functions will differ in the number of elements they expect their vector to have.
     */
    def mkODEs(parts: List[Part]): List[(ODE, Double, ODE, Double)] = parts.filter((p:Part) => p match { case Gene(_,_,_) => false; case _ => true}).map( mkTuple )

    /**
     *  This function builds the pairs from given parts.
     *  The second element of each pair is the protein concentration,
     *  which is straightforward.
     *  The first element will require three or four inputs depending on which
     *  kind of promotor we have; both take as final elements the (current) mRNA concentration and
     *  the (current) output protein concentration, but:
     *  NotPromotors take one TF (input) concentration and AndPromotors take two TF (input) concentrations
     */
    def mkTuple(part: Part): (ODE, ODE, Double) = part match {
        case NotGate(Gene(_, _, c), Gene(k2, (d1, d2), _), k1, km, n) => (
            //concs(0):[TF]; concs(1): [mRNA]; concs(2): [Protein]
            (time: Double, concs: VectorD) => (k1 * km ~^ n) / (km ~^ n + concs(0) ~^ n) - d1 * concs(1),
            (time: Double, concs: VectorD) => k2 * concs(1) - d2 * concs(2),c)
        case AndGate((Gene(_, _, c1), Gene(_, _, c2)), Gene(k2, (d1, d2), _), k1, km, n) => (
            // concs(0): [TF1]; concs(1): [TF2]; concs(2): [mRNA]; concs(3): [Protein]
            (time: Double, concs: VectorD) => (k1 * (concs(0) * concs(1)) ~^ n) / (km ~^ n + (concs(0) * concs(1)) ~^ n) - d1 * concs(2),
            (time: Double, concs: VectorD) => k2 * concs(2) - d2 * concs(3),c1*c2)
    }

}
