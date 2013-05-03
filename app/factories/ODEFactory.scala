package factories

import scalation.{VectorD, RungeKutta}
import scalation.DoubleWithExp._
import models._

/**
 *  The factory where all the stuff with ODEs happens :D
 *  It has functionality to take a network of parts (CSs and TFs) and use ODEs to
 *  update their concentrations, thereby simulating how the "signals" propagate
 *  through the network
 */
object ODEFactory {

    /**
     *  ODE: dy/dt = f(t,y)
     *  t is the double, y is the VectorD with the resulting concentrations:
     *  [mRNA] at index 0, [Protein] at index 1, with [mRNA] and [Protein] the
     *  concentrations of the "output" of the gate (i.e. the CS after it)
     */
    type ODE = (Double, VectorD) => VectorD

    /**
     * ODEPair: a pair of an ode and a vector of initial concentrations
     */
    type ODEPair = (ODE, VectorD)

    /**
     *  We're going to make a list of ODE pairs:
     *  each pair will have a function for the ODE that takes a vector for the
     *  input concentrations ([TF], [mRNA] and [Protein])
     *  and a vector for the initial concentrations of the input protein and mRNA
     *  The functions will differ in the number of elements they expect their vector to have.
     */
    def mkODEs(parts: List[Part]): List[ODEPair] = parts.map(mkTuple).flatMap(item => item match { case Some(x) => List(x); case None => Nil})

    /**
     *  This function builds an ODEPair from given parts.
     *  The ODEs will require three or four inputs depending on which
     *  kind of promotor we have; both take as final elements the (current) mRNA concentration and
     *  the (current) output protein concentration, but:
     *  NotGates take one TF (input) concentration and AndGates take two TF (input) concentrations
     *  If a CodingSeq is encountered, no ODE needs to be generated and None is returned
     */    
    def mkTuple(part: Part): Option[ODEPair] = part match {
        case cs:CodingSeq => Some(
            ((time: Double, concs: VectorD) => new VectorD(Array( -cs.d1*concs(0), cs.k2*concs(0) - cs.d2*concs(1))),
            new VectorD(Array(cs.concentration._1, cs.concentration._2))))
        case ng:NotGate => { Some(
            //concs(0):[TF]; concs(1): [mRNA]; concs(2): [Protein]
            (   (time: Double, concs: VectorD) => new VectorD(Array(
                    (ng.k1 *  ng.km ~^ ng.n) / (ng.km ~^ ng.n + concs(0) ~^ ng.n) - ng.output.d1 * concs(1),
                    ng.output.k2 * concs(1) - ng.output.d2 * concs(2)
                )),
                new VectorD(Array(ng.input.concentration._2, ng.output.concentration._1, ng.output.concentration._2))
            )
        )}
        case ag:AndGate => Some(
            // concs(0): [TF1]; concs(1): [TF2]; concs(2): [mRNA]; concs(3): [Protein]
            (   (time: Double, concs: VectorD) => new VectorD(Array(
                    (ag.k1 * (concs(0) * concs(1)) ~^ ag.n) / (ag.km ~^ ag.n + (concs(0) * concs(1)) ~^ ag.n) - ag.output.d1 * concs(2),
                    ag.output.k2 * concs(2) - ag.output.d2 * concs(3)
                )),
                new VectorD(Array(ag.input._1.concentration._2, ag.input._2.concentration._2, ag.output.concentration._1, ag.output.concentration._2))
            )
        )
        case _ => None
    }

}
