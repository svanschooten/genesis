package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsArray
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import factories.ODEFactory



class NetworkSpec extends Specification {
 sequential
    "Network simulation" should {
        "return correct results" in {
          running(FakeApplication()) {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs),true)
            val B = CodingSeq("B",concs.zip(concs),true)
            val C = CodingSeq("C",0,false)
            val D = CodingSeq("D")
            val E = CodingSeq("E",concs.zip(concs),true)
            val F = CodingSeq("F",0)
            val notA = NotGate(A,C)
            val notAandB = AndGate((C,B),D)
            val notAandBandE = AndGate((D,E),F)
            val net = new Network(List(A,B,E),-1,"")
            val results = net.simulate(1500.0)
            // expected results generated using MATLAB R2012b
            //._2 = mRna, ._3 = protein
            results(results.length-1).foreach(t => t._1 match {
                case "A" => t._2 must beCloseTo(1.0,0.1); t._3 must beCloseTo(1.0,0.1)
                case "B" => t._2 must beCloseTo(1.0,0.1); t._3 must beCloseTo(1.0,0.1)
                case "C" => t._2 must beCloseTo(200.44,0.1); t._3 must beCloseTo(999.66,0.1)
                case "D" => t._2 must beCloseTo(334.73,0.1); t._3 must beCloseTo(1278.35,0.1)
                case "E" => t._2 must beCloseTo(1.0,0.1); t._3 must beCloseTo(1.0,0.1)
                case "F" => t._2 must beCloseTo(270.67,0.1); t._3 must beCloseTo(939.21,0.1)
            })
          }
        }
    }
    
    "NotGate simulation" should {
        "return correct results" in {
          running(FakeApplication()) {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs),isInput=true)
            val C = CodingSeq("C",isInput=false)
            val AtoB = NotGate(A,C)
            val net = new Network(List(A),-1,"")
            val results = net.simulate(1500.0)
            results(results.length-1)(0)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(0)._3 must beCloseTo(1.0, 0.1)
            results(results.length-1)(1)._2 must beCloseTo(200.44, 0.1)
            results(results.length-1)(1)._3 must beCloseTo(999.69, 0.1)
          }
       }
    }
    
     "AndGate simulation" should {
        "return correct results" in {
          running(FakeApplication()) {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs),isInput=true)
            val B = CodingSeq("B",concs.zip(concs),isInput=true)
            val C = CodingSeq("C",isInput=false)
            val ABtoC = AndGate((A,B),C)
            val net = new Network(List(A,B),-1,"")
            val results = net.simulate(1500.0)
            results(results.length-1)(0)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(0)._3 must beCloseTo(1.0, 0.1)
            results(results.length-1)(1)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(1)._3 must beCloseTo(1.0, 0.1)
            results(results.length-1)(2)._2 must beCloseTo(1.41e-5, 1e-5)
            results(results.length-1)(2)._3 must beCloseTo(7.05e-5, 1e-5)
          }
         }
      }
         /*
      * mkTuple with codingseq as input
      * return VectorD with corresponding concentration
      */
     "mkTuple" should {
       "be correct" in {
         running(FakeApplication()) {
           val A = CodingSeq("A", List((0.3, 0.2)), false)
           val csTupleA = ODEFactory.mkTuple(A)
           val csString = csTupleA.toString
           csString must contain("VectorD(0.3 0.2)")
         }
       }
     }
     
	running(FakeApplication()){
		val pa = CodingSeq("A", List((0.1, 0.1)), true)
		val pb = CodingSeq("B", List((0.2, 0.3)), true)
		val pc = CodingSeq("C", List((0.3, 0.2)), false)
		val pd = CodingSeq("D", List((0.4, 0.3)), false)
		val g1 = AndGate((pa, pb), pc)
		val g2 = NotGate(pc, pd)
		
		"CodingSeq" should {
			"retrieve the correct parameters for the coding sequences" in {
				pa.k2 must equalTo(4.6337)
				pb.d1 must equalTo(0.0205)
				pc.d2 must equalTo(0.8338)
			}
	
			"retrieve the correct parameters for the and gate" in {
				g1.k1 must equalTo(4.5272)
				g1.km must equalTo(238.9569)
				g1.n must equalTo(3)
			}
	
			"retrieve the correct parameters for the not gate" in {
				g2.k1 must equalTo(3.2155)
				g2.km must equalTo(213.2011)
				g2.n must equalTo(1)
			}
		}
		
		val pas = CodingSeq("A", List((0.1, 0.1)), true)
		val pbs = CodingSeq("B", List((0.2, 0.3)), true)
		val pcs = CodingSeq("C", List((0.3, 0.2)), false)
		val pds = CodingSeq("D", List((0.4, 0.3)), false)
		val g1s = AndGate((pas, pbs), pcs)
		val g2s = NotGate(pcs, pds)
		val simpleNetworkSave = new Network(List(pas, pbs), -2, "simpleNetworkSaveTest")
		
		val pac = CodingSeq("A", List((2.1, 3.4)), true)
		val pbc = CodingSeq("B", List((1.2, 1.3)), true)
		val pcc = CodingSeq("C", List((3.3, 2.2)), false)
		val pdc = CodingSeq("D", List((0.4, 1.3)), false)
		val g1c = AndGate((pac, pbc), pcc)
		val g2c = NotGate(pcc, pdc)
		val g3c = NotGate(pcc, pac)
		val g4c = AndGate((pcc,pdc),pbc)
		val complexNetworkSave = new Network(List(pac, pbc), -2, "complexNetworkSaveTest")
	
		"Network" should {
			"correctly save a simple network" in {
			  running(FakeApplication()){
			    simpleNetworkSave.save(0)
				  val id = simpleNetworkSave.getID
				  DB.withConnection { implicit connection => 
					  val cds = SQL("select name,next,isInput from cds where networkid={id}")
					  				.on('id -> id)
					  				.as {
					      	  		get[String]("name")~get[String]("next")~get[Boolean]("isInput") map{
					      	  		  case name~next~isInput => (name,next,isInput)
					      	  		} * }
					  println(cds)
					  cds.size must equalTo(3)
					  for(cur <- cds){
						  if(cur._1=="A"||cur._1=="B"){
						    cur._2 must equalTo("C")
						    cur._3 must equalTo(true)
						  }
						  else if(cur._1=="C"){
						    cur._2 must equalTo("D")
						    cur._3 must equalTo(false)
						  }
						  else{
						    throw new AssertionError("Network incorrectly saved.")
						  }
					  }
					  simpleNetworkSave.delete
				  }
			    }
			}
			
			"correctly save a more complex network, containing a cycle and one output to multiple inputs" in {
				running(FakeApplication()){
					  complexNetworkSave.save(0)
					  val id = complexNetworkSave.getID
					  DB.withConnection { implicit connection => 
						  val cds = SQL("select name,next,isInput from cds where networkid={id}")
						  				.on('id -> id)
						  				.as {
						      	  		get[String]("name")~get[String]("next")~get[Boolean]("isInput") map{
						      	  		  case name~next~isInput => (name,next,isInput)
						      	  		} * }
						  for(cur <- cds){
							  if(cur._1=="A"||cur._1=="B"){
							    cur._2 must equalTo("C")
							    cur._3 must equalTo(true)
							  }
							  else if(cur._1=="D"){
							    cur._2 must equalTo("B")
							    cur._3 must equalTo(false)
							  }
							  else if(cur._1=="C"&&(cur._2=="A"||cur._2=="B"||cur._2=="D")){
							    cur._3 must equalTo(false)
							  }
							  else{
							    throw new AssertionError("Network incorrectly saved.")
							  }
						  }
					   complexNetworkSave.delete
					 }
				}
			}
			
			"correctly insert the concentrations at the correct times from a csv file" in {
				running(FakeApplication()){
					val fileInput = Array("t,A,B","0,0,0","30,0,1","70,1,1")
					complexNetworkSave.setStartParameters(fileInput, 100, 10, 100)
					val net = complexNetworkSave
					val seen : Set[String] = Set()
					for(cs <- net.inputs){
					  if(cs.name=="A"){
					    cs.concentration(69)._1 must equalTo(0)
					    cs.concentration(69)._2 must equalTo(0)
					    cs.concentration(70)._1 must equalTo(30.36)
					    cs.concentration(70)._2 must equalTo(175.11)
					  }
					  else if(cs.name=="B"){
					    cs.concentration(29)._1 must equalTo(0)
					    cs.concentration(29)._2 must equalTo(0)
					    cs.concentration(30)._1 must equalTo(66.80)
					    cs.concentration(30)._2 must equalTo(297.16)
					  }
					  else throw new AssertionError("Wrong inputs")
					}
				}		    
			}
		}
	 }
	
     "Json"should {
       "return correct results" in {
         running(FakeApplication()) {
    	   val listName = List("mRNA_A", "protein_A")
    	   val concs = List.fill(3)(1.0)
           val A = CodingSeq("A",concs.zip(concs),isInput=true)
           val net = new Network(List(A),-1,"")
    	   val json = net.simJson(1.0)
    	   val jsonName = (json._1 \\ "name").map(_.as[String])
    	   jsonName must equalTo(listName)
         }
       }
     }
     
     "AndGate parameters" should {
		"return correct results" in {
			running(FakeApplication()) {
				val A = CodingSeq("A",isInput=true)
				val B = CodingSeq("B",isInput=true)
				val C = CodingSeq("C",isInput=false)
				val ABtoC = AndGate((A,B),C)
				ABtoC.input must equalTo((CodingSeq("A",isInput=true),CodingSeq("B",isInput=true)))
				ABtoC.output must equalTo(CodingSeq("C",isInput=false))
			}
		}
     }
     
     "Notgate parameters" should {
		"return correct results" in {
			running(FakeApplication()) {
				val A = CodingSeq("A",isInput=true)
				val B = CodingSeq("B",isInput=false)
				val AtoB = NotGate(A,B)
				AtoB.input must equalTo(CodingSeq("A",isInput=true))
				AtoB.output must equalTo(CodingSeq("B",isInput=false))
			}
		}
	}

     "Simulation with valid JsValue" should {
       "return something" in {
         running(FakeApplication()){
           val json: JsValue = Json.parse ("""
               {"name":"testCircuit",
			    "circuit":
			    {
			        "vertices":[
			            {"id":"input","type":"input","x":30,"y":30},
			            {"id":"output","type":"output","x":470,"y":50},
			            {"id":"not2","type":"not","x":258.140625,"y":159},
			            {"id":"and3","type":"and","x":287.140625,"y":59}
			            ],
			        "edges":[
			            {"source":"input","target":"not2","protein":"B"},
			            {"source":"input","target":"and3","protein":"A"},
			            {"source":"not2","target":"and3","protein":"C"},
			            {"source":"and3","target":"output","protein":"D"}
			            ]
			    },
			    "inputs":"t,A,B\n0,1,0\n10,0,1\n60,1,1\n90,0,0",
			    "steps":"100",
                "stepSize":"1",
			    "library":"0"
        		 }	""")
           val simulate = Network.simulate(json,-1)
           val jsonName = (simulate \\ "name").map(_.as[JsValue])
           jsonName must not beNull
           val data = (simulate \\ "data").map(_.as[JsArray])
           for(info <- data) {
            val x = (info \\ "x").map(_.as[Double])
            x must not beNull
	        val y = (info \\ "y").map(_.as[Double])
	        y must not beNull
           }
           val save = Network.saveFromJson(json,-1)
		   DB.withConnection { implicit connection =>
				val network = SQL("select networkname,userid from networkownedby where networkname={name}")
				.on('name -> "testCircuit")
				.as {
					get[String]("networkname")~get[Int]("userid") map{
					case input~userid => (input,userid)
					} * }
				network.size must equalTo(1)
				for(info <- network) {
					info._1 must equalTo("testCircuit")
					info._2 must equalTo(-1)
					}
				val deleteLib = SQL(" DELETE FROM networkownedby WHERE networkname={name}")
			    .on('name -> "testCircuit").executeUpdate()
				}  
           }
       }
     }
     
     "Get Network with correct input" should {
       "return something" in {
         running(FakeApplication()) {
           val getNetworkID = Network.getNetworks(-1)
           getNetworkID must not beNull
         }
       }
     }
}