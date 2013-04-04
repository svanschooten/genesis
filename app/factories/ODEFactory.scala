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
     *  We're going to make a list of pairs of ODEs:
     *  each pair will have one function for the mRNA concentration (d[mRNA]/dt)
     *  and one function for the Protein concentration (d[Protein]/dt).
     *  The functions will differ in the number of elements they expect their vector to have.
     */
    def mkODEs(parts: List[Part]): List[(ODE, ODE)] = parts.map( mkTuple )

    /**
     *  This function builds the pairs from given parts.
     *  The second element of each pair is the protein concentration,
     *  which is straightforward.
     *  The first element will require three or four inputs depending on which
     *  kind of gate we have; both take as final elements the (current) mRNA concentration and
     *  the (current) output protein concentration, but:
     *  NotGates take one TF (input) concentration and AndGates take two TF (input) concentrations
     */
    def mkTuple(part: Part): (ODE, ODE) = part match {
        case NotGate(Gene(k2, (d1, d2)), k1, km, n) => (
            //concs(0):[TF]; concs(1): [mRNA]; concs(2): [Protein]
            (time: Double, concs: VectorD) => (k1 * km ~^ n) / (km ~^ n + concs(0) ~^ n) - d1 * concs(1),
            (time: Double, concs: VectorD) => k2 * concs(1) - d2 * concs(2))
        case AndGate(Gene(k2, (d1, d2)), k1, km, n) => (
            // concs(0): [TF1]; concs(1): [TF2]; concs(2): [mRNA]; concs(3): [Protein]
            (time: Double, concs: VectorD) => (k1 * (concs(0) * concs(1)) ~^ n) / (km ~^ n + (concs(0) * concs(1)) ~^ n) - d1 * concs(2),
            (time: Double, concs: VectorD) => k2 * concs(2) - d2 * concs(3))
    }

}