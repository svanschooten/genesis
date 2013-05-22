package models
import scalation.{VectorD, RungeKutta}
import factories.ODEFactory._
import play.api.libs.json._

/*
TODO list of inputs:
    list using (Double, List[List[Double]]) as (stepSize assumed in List, list of concentrations for each input in alphabetical order)
*/

/**
 *  Class to represent a network of coding sequences and transcription factors.
 *  It has functionality to simulate how the concentrations of the components of
 *  the network change over time, and outputs the corresponding concentrations if
 *  desired.
 */
class Network(inputs: List[CodingSeq]) {

    /**
     *  The amount of time between steps; this also determines how many steps the
     *  simulation will take (given a fixed ending time)
     *  Alexey (in meeting on 4/26): may need to decide this dynamically based on the end time
     */
    private val stepSize = 1
    /**
     *  The current time; needed by the RungeKutta integrator
     */
    private var currentTime = 0.0

    // resets all the ready flags
    private def reset_readies(cs: CodingSeq) {
        cs.ready=false
        cs.linksTo.foreach(_ match {
            case NotGate(_,y) if(y.ready) => reset_readies(y)
            case AndGate((_,_),y) if(y.ready) => reset_readies(y)
            case _ => return
        })
    }

    /**
     *  Perform the simulation that will show how the concentrations of the checmicals
     *  in the system will evolve and return these concentrations as follows:
     *  Each time step a list of triplets is generated, with name, mRNA and protein concentration,
     *  and these time steps form a list as well.
     *  This function first resets all the current concentrations to guarantee idempotence.
     *  @param finish The finish time; simulation will run from 0.0 to this time.
     */
    def simulate(finish: Double): List[List[(String,Double,Double)]] = {
        // function to reset all concentrations in this network
        def resetConcs(cs: CodingSeq) {
            if(cs.ready)
                return
            if(!cs.isInput)
                cs.concentration = List((0,0))
            cs.ready = true
            cs.currentStep = 1
            cs.linksTo.foreach(x => resetConcs(x.output))
        }
        // ready inputs if necessary and reset all concentrations
        inputs.foreach(x=> {if(x.linkedBy.isEmpty) x.ready=true; resetConcs(x)})

        // function to get all the concentrations out of the network as a list of pairs
    	def getConcs(l: List[CodingSeq] = inputs): Set[(String,Double,Double)] = l.flatMap(seq => seq match {
            case CodingSeq(name,_,_) if(!seq.ready) => {seq.ready = true;
              Set((name, seq.curConc._1, seq.curConc._2)) ++ (seq.linksTo.collect( {
                case NotGate(_,next) => getConcs(List(next))
                case AndGate(_,next) => getConcs(List(next))
            })).flatten}
            case _ => Nil
        }).toSet

        currentTime = 0.0
        val times = currentTime to finish by stepSize
        inputs.foreach(reset_readies _)
        // do the required steps and save the concentrations each round
        times.foldRight(List(getConcs().toList))((time,li)=>{
            step() // this is very poor actually: functional method fold has side effects now
            inputs.foreach(x => {reset_readies(x); x.currentStep += 1})
            getConcs().toList :: li
        }).reverse
    }

    /**
     *  Function that performs the simulation just like simulate(), except this returns
     *  a JSON value that can be used to plot a graph
     */
    def simJson(finish: Double) = {
        val results = simulate(finish)
        val flipped = new scala.collection.mutable.ListMap[String, scala.collection.mutable.ListBuffer[(String, Double, Double)]]()
        results(0).foreach( triple => flipped += triple._1 -> new scala.collection.mutable.ListBuffer[(String,Double,Double)]())
        results.foreach( li => {
            li.foreach( triple => flipped(triple._1) += triple)
        })
        Json.toJson(flipped.values.flatMap( dataset => {
            var x = 0.0-stepSize; var y = 0.0-stepSize
            List(Json.obj( "name" -> Json.toJson("mRNA_"+dataset(0)._1), "data" ->
                dataset.map(_._2).map(conc => {x+=stepSize; Json.obj("x" -> x, "y" -> conc)})),
                Json.obj("name" -> Json.toJson("protein_"+dataset(0)._1), "data" ->
                dataset.map(_._3).map(conc => {y+=stepSize; Json.obj("x" -> y, "y" -> conc)}))
                )}))
    }

    /**
     *  Do a step in the simulation. That is, calculate the new concentrations
     *  of all the proteins in the system, using a level-based approach to solving
     *  the ODEs. A level is defined as all the CodingSeqs and the first gates they
     *  link to, with the condition that any gate that is linked to by any CodingSeq
     *  in the current list, must have both of its inputs in the list. If it doesn't,
     *  the gate belongs to a different, deeper level and will be handled later,
     *  when both of its inputs have been updated.
     */
    def step() {
        currentTime += stepSize

        // reset the ready flags
        inputs.foreach(reset_readies _)
        // but not those of the inputs that have no output connected to them
        inputs.foreach(x=> if(x.linkedBy.isEmpty) x.ready=true)
        // figure out the new concentrations
        inputs.foreach(do_the_math _)

        // the function that will do the actual work
        def do_the_math(cs: CodingSeq) {
            // generate the appropriate ODEPairs and update the concentrations
            val parts = cs.linksTo.collect( {
                case y@NotGate(_,out) if(!out.ready) => y
                case y@AndGate((in1,in2),out) if(((in1.ready && in2==cs) || (in1==cs && in2.ready)) && !out.ready) => y
            })

            val odePairs = mkODEs(parts)
            val results = solve(odePairs)
            results.zip(parts).foreach(_ match {
                case (a,b:NotGate) => b.output.ready=true; b.output.concentration ::= (a(1),a(2))
                case (a,b:AndGate) => b.output.ready=true; b.output.concentration ::= (a(2),a(3))
            })
            // finally, recursively update the rest of the network
            // foreach won't do anything if parts was empty, that is the base case of the recursion
            parts.foreach((x:Gate) => do_the_math(x.output))
        }

        // the function that calls the solver; the solver expects each element of the
        // result vector to be generated by a separate function, hence the awkward Array.
        // the different cases are needed because the integrator expects that there are
        // as many functions as there are elements in the vector of initial concentrations
        def solve(odePairs: List[ODEPair]): List[VectorD] = {
            odePairs.map( {case (ode, concs) if concs.length == 3 => RungeKutta.integrateVV(Array((d, v)=>0.0, (d, v)=>ode(d, v)(0), (d, v)=>ode(d, v)(1)), concs, stepSize, 0.0, stepSize)
                           case (ode, concs) if concs.length == 4 => RungeKutta.integrateVV(Array((d, v)=>0.0, (d, v)=>0.0, (d, v)=>ode(d, v)(0), (d, v)=>ode(d, v)(1)), concs, stepSize, 0.0, stepSize)
                           } )
        }
    }

}