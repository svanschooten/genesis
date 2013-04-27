package factories

import scala.collection.immutable.List

import scalation.VectorD
import scalation.DoubleWithExp._
import models._

object ODEFactory {

    /**
     *  ODE: dy/dt = f(t,y)
     *  t is the double, y is the VectorD with the resulting concentrations:
     *  [mRNA] at index 0, [Protein] at index 1, with [mRNA] and [Protein] the
     *  concentrations of the "output" of the gate (i.e. the CS after it)
     */
    type ODE = (Double, VectorD) => VectorD

    /**
     * ODEPair: a pair of an ode and a vector of initial concentrations to be fed to it
     */
    type ODEPair = (ODE, VectorD)

    /**
     *  We're going to make a list of triplets:
     *  each triplet will have one function for the mRNA concentration
     *  and one function for the Protein concentration
     *  and one double for the initial protein concentration of the input protein
     *  The functions will differ in the number of elements they expect their vector to have.
     */
    def mkODEs(parts: List[Part]): List[ODEPair] = parts.map(mkTuple).flatMap(item => item match { case Some(x) => List(x); case None => Nil})

    /**
     *  This function builds a tuple from given parts.
     *  Each tuple consists of two ODEPairs; the first one
     *  contains the ODE for the mRNA concentration and the initial mRNA concentration,
     *  the second one contains the same information for the protein concentration.
     *  The initial mRNA concentrations are assumed to be zero.
     *  The ODEs will require three or four inputs depending on which
     *  kind of promotor we have; both take as final elements the (current) mRNA concentration and
     *  the (current) output protein concentration, but:
     *  NotPromotors take one TF (input) concentration and AndPromotors take two TF (input) concentrations
     */
    def mkTuple(part: Part): Option[ODEPair] = part match {
        case NotGate(CodingSeq(_, _, c_in, _), CodingSeq(k2, (d1, d2), c_out, _), k1, km, n) => Some(
            //concs(0):[TF]; concs(1): [mRNA]; concs(2): [Protein]
            (   (time: Double, concs: VectorD) => new VectorD(Array(
                    (k1 * km ~^ n) / (km ~^ n + concs(0) ~^ n) - d1 * concs(1),
                    k2 * concs(1) - d2 * concs(2)
                )),
                new VectorD(Array(c_in, 0.0, c_out))
            )
        )
        case AndGate((CodingSeq(_, _, c_in_1, _), CodingSeq(_, _, c_in_2, _)), CodingSeq(k2, (d1, d2), c_out, _), k1, km, n) => Some(
            // concs(0): [TF1]; concs(1): [TF2]; concs(2): [mRNA]; concs(3): [Protein]
            (   (time: Double, concs: VectorD) => new VectorD(Array(
                    (k1 * (concs(0) * concs(1)) ~^ n) / (km ~^ n + (concs(0) * concs(1)) ~^ n) - d1 * concs(2),
                    k2 * concs(2) - d2 * concs(3)
                )),
                new VectorD(Array(c_in_1, c_in_2, 0.0, c_out))
            )
        )
        case _: CodingSeq => None

    }

}
