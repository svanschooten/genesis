package models
import scalation.{VectorD, RungeKutta}
import factories.ODEFactory._
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.collection.mutable.Map
import play.api.libs.json._

/**
 *  Class to represent a network of coding sequences and transcription factors.
 *  It has functionality to simulate how the concentrations of the components of
 *  the network change over time, and outputs the corresponding concentrations if
 *  desired. It can also be saved to an external database, and its companion enables
 *  loading back from such a database, as well as removal from it.
 */
class Network(val inputs: List[CodingSeq], userid: Int, val networkname: String, stepSize: Double = 1) {

    // resets all the ready flags
    private def reset_readies(cs: CodingSeq) {
        cs.ready=false
        cs.linksTo.foreach(_ match {
            case x:Gate if(x.output.ready) => reset_readies(x.output)
            case _ => return
        })
    }

    /**
     *  Perform the simulation that will show how the concentrations of the checmicals
     *  in the system will evolve and return these concentrations as follows:
     *  Each time step a list of triplets is generated, with name, mRNA and protein concentration,
     *  and these time steps form a list as well.
     *  @param finish The finish time; simulation will run from 0.0 to this time.
     */
    def simulate(finish: Double): List[List[(String,Double,Double)]] = {
        // function to get all the concentrations out of the network as a list of pairs
    	def getConcs(l: List[CodingSeq] = inputs): Set[(String,Double,Double)] = l.flatMap(seq => seq match {
            case CodingSeq(name,_,_,_) if(!seq.ready) => {seq.ready = true;
              Set((name, seq.concentration.head._1, seq.concentration.head._2)) ++ (seq.linksTo.collect( {
                case x:Gate => getConcs(List(x.output))
            })).flatten}
            case _ => Nil
        }).toSet

        val times = 0.0 to finish by stepSize
        inputs.foreach(reset_readies _)
        // do the required steps and save the concentrations each round
        times.foldRight(List(getConcs().toList))((time,li)=>{
            step() // this is very poor actually: functional method fold has side effects now
            inputs.foreach(reset_readies _)
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
        // reset the ready flags
        inputs.foreach(reset_readies _)
        // but not those of the inputs that have no output connected to them
        // these inputs also handle their concentration a bit differently;
        //instead of calculating new ones each time, existing concentration is popped so head is always the current concentration
        inputs.foreach(x=> {if(x.linkedBy.isEmpty) {x.ready=true; x.concentration = x.concentration.tail}})
        // figure out the new concentrations
        inputs.foreach(do_the_math _)

        // the function that will do the actual work
        def do_the_math(cs: CodingSeq) {
            // generate the appropriate ODEPairs and update the concentrations
            val parts = cs.linksTo.collect( {
                case y@NotGate(_,out,_) if(!out.ready) => y
                case y@AndGate((in1,in2),out,_) if(((in1.ready && in2==cs) || (in1==cs && in2.ready) || (in1.linkedBy.isEmpty ^ in2.linkedBy.isEmpty)) && !out.ready) => y
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

    /**
     * Save this network to the database.
     */
    def save(libraryid : Int) = {
    	Network.delete(userid,networkname)
	    DB.withConnection { implicit connection =>
	      SQL(
	        """
	         insert into networkownedby(userid,networkname,libraryid) values({user},{networkname},{libraryid})
	        """
	      ).on(
	        'user -> userid,
	        'networkname -> networkname,
	        'libraryid -> libraryid
	      ).executeUpdate()
	      val id = getID
		  for(cs:CodingSeq <- inputs) {
		    cs.save(id,true,true)
		  }
	    }

	  }

    /**
     *  Generate the input list for the proteins that form the input to the network
     *  using a file that dictates when the concentration is zero or one
     *  @param input The array of input lines of the file
     *  @param startProteinConc The concentration to use when a protein is 1 (on)
     *  @param startMRNAConc The concentration to use when an mRNA is 1 (on)
     *  @param limit The maximum time (to figure out how long the final 0 or 1 lasts)
     */
    def setStartParameters(input: Array[String], startProteinConc: Double, startMRNAConc: Double, limit: Double){
      // these are the values that the proteins on their own would stabilize to (generated using MATLAB 2012b)
      // [k2,d1,d2: cds; k1: not]
      val defaultConcs = Map(
        "A"->(30.36,175.11),
        "B"->(66.80,297.16),
        "C"->(33.43,175.19),
        "D"->(111.67,737.69),
        "E"->(24.65,134.10),
        "F"->(31.61,166.65),
        "G"->(27.61,150.06),
        "H"->(30.83,83.62),
        "I"->(43.08,232.11),
        "J"->(46.26,280.51),
        "K"->(52.73,273.37),
        "L"->(58.41,367.73),
        "M"->(37.55,258.82)
      )

      val results:Map[String,List[(Double,Double)]] = Map()
      val firstLine = input(0).split(",")
      val TFInd = new Array[String](firstLine.length+1)
      val curConcs = new Array[(Double,Double)](firstLine.length+1)
      for(i <- 1 to firstLine.length-1){
        TFInd(i) = firstLine(i)
        results += (TFInd(i)->List())
      }
      val secLine = input(1).split(",")
      for(j <- 1 to secLine.length-1){
	      if(secLine(j).toInt==1) curConcs(j) = defaultConcs(TFInd(j))
	      else curConcs(j) = (0.0,0.0)
	    }
      var t = 0.0
      for(i <- 2 to input.length-1){
        val curLine = input(i).split(",")
        while(t<curLine(0).toDouble){
          for(j <- 1 to curLine.length-1){
            results(TFInd(j)) ::= (curConcs(j)._1,curConcs(j)._2)
          }
          t += stepSize
        }
        for(j <- 1 to curLine.length-1){
	      if(curLine(j).toInt==1) curConcs(j) = defaultConcs(TFInd(j))
	      else curConcs(j) = (0.0,0.0)
	    }
      }
      while(t<=limit){
        for(i <- 1 to firstLine.length-1){
            results(TFInd(i)) ::= (curConcs(i)._1,curConcs(i)._2)
        }
        t += stepSize
      }
      for(cs: CodingSeq <- inputs){
        if(results contains cs.name) cs.concentration = results(cs.name).reverse
      }
    }

    /**
     * Delete this network from the database.
     */
    def delete {
      Network.delete(userid,networkname)
    }

    /**
     * Return the networkID that belongs to this network in the database.
     */
    def getID: Int = {
      Network.getID(userid,networkname)
    }
}

object Network {

    /**
     * Return the Network object with name 'networkname' that belongs to 'user'
     */
    def load(userid: Int, networkname: String) = {
      DB.withConnection{ implicit connection =>
      	  val id = getID(userid, networkname)
      	  val libraryid = SQL("select libraryid from networkownedby where networkid={id})")
      	  				.on('id -> id)().head[Int]("libraryid")
      	  /*val tempconcs = SQL(
			      """
			      select * from concentrations
	    		  where networkid = {id}
			      """
			      ).on('id -> id)
			      .as {
	      	  		get[String]("name")~get[Int]("time")~get[Double]("conc1")~get[Double]("conc2") map{
	      	  		  case name~time~conc1~conc2 => (name,time,conc1,conc2)
	      	  		} *
	      	}
      	  var tempconcs2:Map[String,List[(Double,Double,Double)]] = Map()
      	  for(c <- tempconcs){
      	    if(tempconcs2 contains c._1){
      	      tempconcs2(c._1) ::= (c._2,c._3,c._4)
      	    }
      	    else{
      	      tempconcs2 += (c._1 -> List((c._2,c._3,c._4)))
      	    }
      	  }
      	  var concentrations:Map[String,List[(Double,Double)]] = Map()
      	  for(c <- tempconcs2.keys){
      	    val list = tempconcs2(c).sortBy(_._1).map(x => (x._2,x._3))
      	    concentrations += c -> list
      	  }

		  var inputs1:Map[String,String] = Map()
	      var inputs2:Map[String,String] = Map()
	      var seqs:Map[String,CodingSeq] = Map()*/
	      val allCDS = SQL(
			      """
			      select * from cds
	    		  where networkid = {id}
			      """
			      ).on('id -> id)
			      .as {
	      	  		get[String]("name")~get[String]("next")~get[Boolean]("isInput") map{
	      	  		  case name~next~isInput => (name,next,isInput)
	      	  		} *
	      	}
		  val gates : List[(String,Double,Double)] = SQL("select * from gates where networkid = {id}")
				  .on('id -> id)
			      .as {
	      	  		get[String]("name")~get[Double]("x")~get[Double]("y") map{
	      	  		  case name~x~y => (name,x,y)
	      	  		} *
	      	}
		  val CDSJson = Json.toJson(allCDS.map(data => {
			  Json.obj("name"->data._1,"next"->data._2,"isInput"->data._3)
			}))
		  val gatesJson = Json.toJson(gates.map(data => {
			  Json.obj("name"->data._1,"x"->data._2,"y"->data._3)
			}))
		  /*val positions = gates.map(x => x._1 -> (x._2,x._3))
		  var startCDS: List[CodingSeq] = Nil
		  for(cs <- allCDS){
		    val newCDS = new CodingSeq(cs._1,libraryID,concentrations(cs._2),cs._3)
		    seqs += (cs._1 -> newCDS)
		    if(cs._3) startCDS ::= newCDS
		  }
      	  for(cs <- allCDS){
      	    if(!(seqs contains cs._2)) seqs += (cs._2 -> new CodingSeq(cs._2,libraryID,concentrations(cs._2),false))
      	    if(inputs1 contains cs._2) inputs2 += (cs._2 -> cs._1)
      	    else inputs1 += (cs._2 -> cs._1)
      	  }

	      for(str: String <- inputs1.keys){
	        if(inputs2 contains str){
	        	val g = new AndGate((seqs(inputs1(str)),seqs(inputs2(str))),seqs(str),libraryID)
				g.x = positions.get(str)._1
				g.y = positions.get(str)._2
	        }
	        else{
	        	val g = new NotGate(seqs(inputs1(str)),seqs(str),libraryID)
				g.x = positions.get(str)._1
				g.y = positions.get(str)._2
	        }
	      }
      	new Network(startCDS,userid,networkname)*/
		println(CDSJson)
		println(gatesJson)
		(libraryid,CDSJson,gatesJson)
      }
    }

    /**
     * Delete the network that corresponds with userid and networkname from the database
     */
    def delete(userid: Int, networkname: String){
      DB.withConnection { implicit connection =>
        val idResults = SQL(
	          """
	          select networkid from networkownedby
	          where userid={userid} AND networkname={networkname}
	          """
	          ).on(
		        'userid -> userid,
		        'networkname -> networkname
		      ).apply()
		  if(idResults.isEmpty) return
		  val id = idResults.head[Int]("networkid")
	      SQL(
	        """
	         DELETE FROM networkownedby WHERE networkid={id};
	         DELETE FROM concentrations WHERE networkid={id};
	         DELETE FROM cds WHERE networkid={id};
	         DELETE FROM gates WHERE networkID={id};
	        """
	      ).on(
	        'id -> id
	      ).executeUpdate()
      	}
    }

    /**
     * Return the networkID that belongs to the corresponding network in the database.
     */
    def getID(userid: Int, networkname: String): Int = {
      DB.withConnection { implicit connection =>
      	  val idResult = SQL(
	          """
	          select networkid from networkownedby
	          where userid={userid} AND networkname={networkname}
	          """
	          ).on(
		        'userid -> userid,
		        'networkname -> networkname
		      ).apply()
		  idResult.head[Int]("networkid")
	    }
    }

    /**
     *  Generate a new Network based on JSON input.
     */
    def fromJSON(json: JsValue) = {
        val net_name = (json \ "name").as[String]
        val libraryID = (json \ "library").as[String].toInt

        // parse the network
        val jsVertices = (json \ "circuit" \ "vertices").as[List[JsValue]]
        val jsEdges = (json \ "circuit" \ "edges").as[List[JsValue]]
        // map from protein name to actual CS
        val csMap = scala.collection.mutable.Map[String,CodingSeq]()
        // map from source of an edge to protein name for that edge
        val srcToCSMap = jsEdges.foldLeft(Map[String,String]())((m,e) => {
            val src = (e \ "source").as[String]
            val csName = (e \ "protein").as[String]
            if(!csMap.contains(csName)) {
                val cs = CodingSeq(csName, libraryID, List((0,0)), false)
                // startsWith can be replaced by == once the setup only generates
                // the source and sink "gates" once
                if(src.startsWith("Input"))
                    cs.isInput=true
                csMap+= csName -> cs
            }
            m + (src -> csName)
        })
        // map from destination of an edge to protein name for that edge
        val destToCSMap = jsEdges.foldLeft(Map[String,String]())((m,e) => {
            val dest = (e \ "target").as[String]
            val csName = (e \ "protein").as[String]
            if(dest.startsWith("and")) {
                if(m.contains(dest+"1"))
                    m + (dest+"2" -> csName)
                else
                    m + (dest+"1" -> csName)
            }
            else
                m + (dest -> csName)
        })
        // list of inputs for the network
        val inputs = (json \ "inputs").as[String].split("\n")

        jsVertices.foreach(v => {
            val id = (v \ "id").as[String]
            val gateType = (v \ "type").as[String]
            if(gateType == "not") {
                val inCS = csMap(destToCSMap(id))
                val outCS = csMap(srcToCSMap(id))
                NotGate(inCS,outCS,libraryID)
            }
            if(gateType == "and") {
                val inCS1 = csMap(destToCSMap(id+"1"))
                val inCS2 = csMap(destToCSMap(id+"2"))
                val outCS = csMap(srcToCSMap(id))
                AndGate((inCS1,inCS2),outCS,libraryID)
            }
        })
        new Network(csMap.values.filter(_.isInput).toList,-1,net_name)
    }

    def simulate(json: JsValue): JsValue = {
      val network = fromJSON(json)
      val inputs = (json \ "inputs").as[String].split("\n")
      val time = (json \ "time").as[String].toDouble
      val steps = (json \ "steps").as[String].toInt
      network.setStartParameters(inputs, 100.0, 10.0, time)
      network.simJson(time)
    }
	
	def saveFromJson(json: JsValue) = {
    	val libraryID = (json \ "library").as[String].toInt
		fromJSON(json).save(libraryID)
	}
	
    def getNetworks(userId: Int) = {
      DB.withConnection { implicit connection =>
        val networks = SQL(
          """
	          select networkname from networkownedby
	          where userid={userid} or userid=-1
          """
        ).on(
          'userid -> userId
        ).as { get[String]("networkname")* }
        val resMap = Map(networks map {s => (s, Network.load(userId,s))} : _*)
        println(resMap)
        Json.toJson(networks.map(x => {
	        Json.obj(x -> Json.obj("libraryid"->resMap(x)._1, "CDS"->resMap(x)._2, "gates"->resMap(x)._3) )
	    }))
      }
    }
}