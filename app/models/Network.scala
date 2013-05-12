package models
import scalation.{VectorD, RungeKutta}
import factories.ODEFactory._
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.collection.mutable.Map

/**
 *  Class to represent a network of coding sequences and transcription factors.
 *  It has functionality to simulate how the concentrations of the components of
 *  the network change over time, and outputs the corresponding concentrations if
 *  desired.
 */
class Network(val inputs: List[CodingSeq], val user: String, val networkname: String) {

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

    private var steps = 0

    /**
     *  Perform the simulation that will show how the concentrations of the checmicals
     *  in the system will evolve and return these concentrations as follows:
     *  Each time step a list of pairs is generated, with mRNA and protein concentration,
     *  and these time steps form a list as well.
     *  @param finish The finish time; simulation will run from 0.0 to this time.
     */
    def simulate(finish: Double): List[List[(Double,Double)]] = {
        // function to get all the concentrations out of the network as a list of pairs
    	def getConcs(l: List[CodingSeq] = inputs): List[(Double,Double)] = l.flatMap(seq => seq match {
            case CodingSeq(_,_) => seq.concentration :: (seq.linksTo match {
                case Some(NotGate(_,next)) => getConcs(List(next))
                case Some(AndGate(_,next)) => getConcs(List(next))
                case _ => Nil
            })
        })

        currentTime = 0.0
        val times = currentTime to finish by stepSize
        // do the required steps and save the concentrations each round
        times.foldRight(List(getConcs()))((time,li)=>{
            step() // this is very poor actually: functional method fold has side effects now
            getConcs() :: li
        }).reverse
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

        // function to verify that a given CS is not from a different level
        // maybe adding pointers to the previous element in CSs leads to a more efficient design
        def reachable(seq: CodingSeq): Boolean = inputs.foldLeft(false)((soFar: Boolean, cs: CodingSeq) => soFar || (seq == cs || (cs match {
            case CodingSeq(_,_) => cs.linksTo match {
                case None => false
                case Some(NotGate(_,nextSeq)) => reachable(nextSeq)
                case Some(AndGate((seq,seq1),_)) if seq != seq1 => false
                case Some(AndGate((seq1,seq),_)) if seq != seq1 => false
                case Some(AndGate((_,_),nextSeq)) => reachable(nextSeq)
                case _ => false
            }
            case _ => false
        })))

        // function to filter out the CodingSeqs we're going to mess with on some level
        def partition(css: List[CodingSeq]): (List[CodingSeq],List[CodingSeq]) = css.partition(x => x match {
            case CodingSeq(_,_) => x.linksTo match {
                case Some(cx: NotGate) => true
                case Some(AndGate((seq1,seq2),_)) if reachable(seq1) && reachable(seq2) => true
                case _ => false
            }
            case _ => false
        })

        inputs.foreach( _ match {
            case x: CodingSeq => val newConcs = solve(mkODEs(List(x)))(0); x.concentration=(newConcs(0),newConcs(1))
        })
        do_the_math((inputs,List[CodingSeq]()))

        // the function that will do the actual work
        def do_the_math(css: (List[CodingSeq],List[CodingSeq])) {
            // first figure out which CSs can be updated on this level
            val newPartition = partition(css._1)
            val oldPartition = partition(css._2)
            val goodCSs = newPartition._1 ::: oldPartition._1
            val badCSs = newPartition._2 ::: oldPartition._2
            // if there's nothing left to do, quit
            if(goodCSs.length == 0)
                return
            // then generate the appropriate ODEPairs and update the concentrations
            val parts = goodCSs.collect( { case x:CodingSeq => x.linksTo } ).collect({case Some(x) => x})
            val odePairs = mkODEs(parts)
            val results = solve(odePairs)
            results.zip(parts).foreach({
                case (a,b:NotGate) => b.output.concentration=(a(0),a(1))
                case (a,b:AndGate) => b.output.concentration=(a(0),a(1))
                })
            // finally, recursively update the rest of the network using the next
            // CSs after the gates we updated, passing along the ones we didn't touch yet
            do_the_math((parts.collect( {
                case x:NotGate => x.output
                case x:AndGate => x.output } ), badCSs))
        }

        // the function that calls the solver; the solver expects each element of the
        // result vector to be generated by a separate function, hence the awkward Array.
        // the different cases are needed because the integrator expects that there are
        // as many functions as there are elements in the vector of initial concentrations
        def solve(odePairs: List[ODEPair]): List[VectorD] = {
            odePairs.map( {case (ode, concs) if concs.length == 3 => RungeKutta.integrateVV(Array((d, v)=>ode(d, v)(0), (d, v)=>ode(d, v)(1), (d, v)=>0.0), concs, currentTime, 0.0, stepSize)
                           case (ode, concs) if concs.length == 4 => RungeKutta.integrateVV(Array((d, v)=>ode(d, v)(0), (d, v)=>ode(d, v)(1), (d, v)=>0.0, (d, v) => 0.0), concs, currentTime, 0.0, stepSize)
                           case (ode, concs) => RungeKutta.integrateVV(Array((d, v)=>ode(d, v)(0), (d, v)=>ode(d, v)(1)), concs, currentTime, 0.0, stepSize)} )
        }

    }
    
    def save = {
	    DB.withConnection { implicit connection =>
	      SQL(
	        """
	         merge into ownedby(username,networkname) key(username,networkname) values({user},{networkname})
	        """
	      ).on(
	        'user -> user,
	        'networkname -> networkname
	      ).executeUpdate()
	      
	      val idResult = SQL(
	          """
	          select id from ownedby
	          where username={user} AND networkname={networkname}
	          """
	          ).on(
		        'user -> user,
		        'networkname -> networkname
		      ).apply().head
		  val id = idResult[Long]("id")
		  for(cs:CodingSeq <- inputs) {
		    cs.save(id,None)
		  }
	    }
	    
	  }

}

object Network {
    import play.api.libs.json._

    /**
     *  Function designed to format the simulation data into JSON that can then
     *  be used by the Rickshaw library to make a pretty graph. More concretely,
     *  the function generates the value for the series property of the graph object.
     *  Data needs to be transposed because it contains concentrations for each time
     *  step, while we need time steps for each concentration (i.e. chemical)
     *  @param data A list of simulation data as generated by Network.simulate
     */
    def simToJson(data: List[List[(Double,Double)]]): JsArray = Json.arr(data.transpose.map( frame => {
        Json.obj( "name" -> "to be filled in", "data" -> Json.arr(
            frame.map(pair => Json.obj("x" -> pair._1, "y" -> pair._2))
        )
    )}))
    
    /**
     * Return the Network object with name 'networkname' that belongs to 'user'
     */
    def loadNetwork(user: String, networkname: String): Network = {
      DB.withConnection{ implicit connection =>
      	val idResult = SQL(
	          """
	          select id from ownedby
	          where username={user} AND networkname={networkname}
	          """
	          ).on(
		        'user -> user,
		        'networkname -> networkname
		      ).apply().head
		  val id = idResult[Long]("id")
		  var inputs1:Map[String,String] = Map()
	      var inputs2:Map[String,String] = Map()
	      var seqs:Map[String,CodingSeq] = Map()
	      var startCDS:List[CodingSeq] = List()
	      val allCDS = SQL(
			      """
			      select * from cds
	    		  where id = {id}
			      """
			      ).on('id -> id)
			      .as {
	      	  		get[String]("prev")~get[String]("name")~get[String]("next")~get[Double]("conc1")~get[Double]("conc2") map{
	      	  		  case prev~name~next~c1~c2 => (prev,name,next,c1,c2)
	      	  		} *
	      	}
      	  for(cs <- allCDS){
      	    if(inputs1 contains cs._3) inputs2 += (cs._3 -> cs._2)
      	    else if(cs._3!="NONE") inputs1 += (cs._3 -> cs._2)
      	    seqs += (cs._2 -> new CodingSeq(cs._2,(cs._4,cs._5)))
      	    if(cs._1=="NONE") startCDS ::= seqs(cs._2)
      	  }
	      
	      for(str: String <- inputs1.keys){
	        if(inputs2 contains str){
	        	val g = new AndGate((seqs(inputs1(str)),seqs(inputs2(str))),seqs(str))
	        }
	        else{
	        	val g = new NotGate(seqs(inputs1(str)),seqs(str))
	        }
	      }
      	new Network(startCDS,user,networkname)
      }
    }
}