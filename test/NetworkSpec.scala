package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray

class NetworkSpec extends Specification {

	//testing multiple gates
    "Network" should {
        "return correct results" in new WithApplication {
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
            // B: 1 1
            // D: 334.730082600319	1278.34579625832
            // F: 270.671535349476	939.211356776036
            // A: 1 1
            // E: 1 1
            // C: 200.437196650108	999.661888066052
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
    
    //testing notgate
    "NotGate" should {
        "return correct results" in new WithApplication {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs),isInput=true)
            val C = CodingSeq("C",isInput=false)
            val AtoB = NotGate(A,C)
            val net = new Network(List(A),-1,"")
            val results = net.simulate(1500.0)
            // expected results generated using MATLAB R2012b
            // A: 1 1
            // B: 200.437196650108	999.694633766756
            results(results.length-1)(0)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(0)._3 must beCloseTo(1.0, 0.1)
            results(results.length-1)(1)._2 must beCloseTo(200.44, 0.1)
            results(results.length-1)(1)._3 must beCloseTo(999.69, 0.1)
        }
    }
    
    //testing andgate
     "AndGate" should {
        "return correct results" in new WithApplication {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs),isInput=true)
            val B = CodingSeq("B",concs.zip(concs),isInput=true)
            val C = CodingSeq("C",isInput=false)
            val ABtoC = AndGate((A,B),C)
            val net = new Network(List(A,B),-1,"")
            val results = net.simulate(1500.0)
            // expected results generated using MATLAB R2012b
            // A: 1 1
            // B: 1 1
            // C: 1.41189714584990e-05 7.04461969998677e-05
            results(results.length-1)(0)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(0)._3 must beCloseTo(1.0, 0.1)
            results(results.length-1)(1)._2 must beCloseTo(1.41e-5, 1e-5)
            results(results.length-1)(1)._3 must beCloseTo(7.05e-5, 1e-5)
            results(results.length-1)(2)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(2)._3 must beCloseTo(1.0, 0.1)
       }
      }
     
     /* 
      * testing json, however this is not complete
      * missing verification of data
      */
     "Json"should {
       "return correct results" in new WithApplication {
    	   val listName = List("mRNA_A", "protein_A")
    	   val concs = List.fill(3)(1.0)
           val A = CodingSeq("A",concs.zip(concs),isInput=true)
           val net = new Network(List(A),-1,"")
    	   val json = net.simJson(1.0)
    	   val jsonName = (json._1 \\ "name").map(_.as[String])
    	   jsonName must equalTo(listName)
       }
     }
     
     //testing the parameters of andgate
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
     
     //testing the parameters of orgate
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
     
     /*
      * I do not know how to make/create json
      */
     "Simulate with JsValue" should {
       "return correct results" in {
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
			    "time":"5",
			    "steps":"100",
			    "library":"2"
        		 }	""")
           val simulate = Network.simulate(json)
           val jsonName = (simulate \\ "name").map(_.as[JsValue])
           jsonName must not beNull
           val data = (simulate \\ "data").map(_.as[JsArray])
           for(info <- data) {
            val x = (info \\ "x").map(_.as[Double])
            x must not beNull
	        val y = (info \\ "y").map(_.as[Double])
	        y must not beNull
           }
           val save = Network.saveFromJson(json)
           //todo check the result
         }
       }
     }
     

     /*
      * Getting network id with correct input,
      * it should return something.
      */
     "Get Network" should {
       "return correct ID" in {
         running(FakeApplication()) {
           val getNetworkID = Network.getNetworks(-1)
           //println(getNetworkID)
           getNetworkID must not beNull
         }
       }
     }
}