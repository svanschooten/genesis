package factories

import scala.collection.immutable.List

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
     *  The amount of time between steps; this also determines how many steps the
     *  simulation will take (given a fixed ending time)
     *  Alexey (in meeting on 4/26): may need to decide this dynamically based on the end time
     */
    private val stepSize = 0.01

    /**
     *  The current time; needed by the RungeKutta integrator
     */
    private var currentTime = 0.0

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
        case NotGate(CodingSeq(_, _, (_,c_in), _), CodingSeq(k2, (d1, d2), (c_out_r,c_out_p), _), k1, km, n) => Some(
            //concs(0):[TF]; concs(1): [mRNA]; concs(2): [Protein]
            (   (time: Double, concs: VectorD) => new VectorD(Array(
                    (k1 * km ~^ n) / (km ~^ n + concs(0) ~^ n) - d1 * concs(1),
                    k2 * concs(1) - d2 * concs(2)
                )),
                new VectorD(Array(c_in, c_out_r, c_out_p))
            )
        )
        case AndGate((CodingSeq(_, _, (_,c_in_1), _), CodingSeq(_, _, (_,c_in_2), _)), CodingSeq(k2, (d1, d2), (c_out_r,c_out_p), _), k1, km, n) => Some(
            // concs(0): [TF1]; concs(1): [TF2]; concs(2): [mRNA]; concs(3): [Protein]
            (   (time: Double, concs: VectorD) => new VectorD(Array(
                    (k1 * (concs(0) * concs(1)) ~^ n) / (km ~^ n + (concs(0) * concs(1)) ~^ n) - d1 * concs(2),
                    k2 * concs(2) - d2 * concs(3)
                )),
                new VectorD(Array(c_in_1, c_in_2, c_out_r, c_out_p))
            )
        )
        case _: CodingSeq => None
    }

    /**
     *  Do a step in the simulation. That is, calculate the new concentrations
     *  of all the proteins in the system, using a level-based approach to solving
     *  the ODEs. A level is defined as all the CodingSeqs and the first gates they
     *  link to, with the condition that any gate that is linked to by any CodingSeq
     *  in the current list, must have both of its inputs in the list. If it doesn't,
     *  the gate belongs to a different, deeper level and will be handled later,
     *  when both of its inputs have been updated.
     *  @param firstCSs The CSs that form the inputs to the network
     */
    def step(firstCSs: List[CodingSeq]) {
        // TODO the concentrations of the CodingSeqs at the beginning of the network may need to be updated separately; ask Alexey
        // TODO whatever code loops to call step a number of times must reset the ready booleans on the CSs before each step
        // TODO also, when the simulation is started, the currentTime (and possibly step size) must be reset
        currentTime += stepSize

        // function filter out the CodingSeqs we're going to mess with on some level
        def partition(css: List[CodingSeq]): (List[CodingSeq],List[CodingSeq]) = css.partition( _ match {
            case CodingSeq(_,_,_,link) => link match {
                case Some(cx: NotGate) => true
                case Some(AndGate((seq1,seq2),_,_,_,_)) if firstCSs.contains(seq1) && firstCSs.contains(seq2) => true
                case _ => false
            }
            case _ => false
        })

        do_the_math((firstCSs,List[CodingSeq]()))

        // the function that will do the actual work
        def do_the_math(css: (List[CodingSeq],List[CodingSeq])) {
            // first figure out which CSs can be updated on this level
            val newPartition = partition(css._1)
            val oldPartition = partition(css._2)
            val goodCSs = newPartition._1 ::: oldPartition._1
            val badCSs = newPartition._2 ::: oldPartition._2
            
            // then generate the appropriate ODEPairs and update the concentrations
            val parts = goodCSs.collect( { case CodingSeq(_,_,_,Some(link)) => link } )
            val odePairs = mkODEs(parts)
            val results = solve(odePairs)
            results.zip(parts).foreach({
                case (a,b:NotGate) => b.output.concentration=(a(0),a(1))
                case (a,b:AndGate) => b.output.concentration=(a(0),a(1))
                })
            // finally, recursively update the rest of the network using the next
            // CSs after the gates we updated, passing along the ones we didn't touch yet
            do_the_math((parts.collect( {
                case NotGate(_, out, _, _, _) => out
                case AndGate(_, out, _, _, _) => out } ), badCSs))
        }

    }

    /**
     *  Solve a list of ODEPairs using the supplied initial concentrations
     *  integrateVV assumes only one step of a simulation to be needed, but since
     *  the steps are determined external to it, it only needs to take one step each
     *  time, so it is called such that it will calculate only the next step
     *  @param odePairs The list of ODEPairs containing ODEs to solve
     */
    def solve(odePairs: List[ODEPair]): List[VectorD] = {
        odePairs.map( {case (ode, concs) => RungeKutta.integrateVV(Array((d:Double, v:VectorD)=>ode(d,v)(0), (d:Double, v:VectorD)=>ode(d,v)(1)), concs, currentTime, 0.0, stepSize)} )
    }

}
