package test

import org.specs2.mutable._
import play.api.test._
import models._

class AndGateSpec extends Specification {
  /*
	    "Network" should {
        "return correct results" in new WithApplication {
            val A = CodingSeq("A",List((0,0)),true)
            val B = CodingSeq("B",List((0,0)),true)
            val C = CodingSeq("C",List(),false)
            val ABtoC = AndGate((A,B),C)
            val net = new Network(List(A,B),-1,"")
            val results = net.simulate(5.0)
            // expected results (still have to be checked)
            // A: 41.666666		228.054374
            // C: 192.646796	960.807989
            // B: 48.780487		260.792124	
            results(results.length-1)(0)._2 must beCloseTo(41.666, 0.01)
            results(results.length-1)(0)._3 must beCloseTo(228.054, 0.01)
            results(results.length-1)(1)._2 must beCloseTo(192.646, 0.01)
            results(results.length-1)(1)._3 must beCloseTo(960.807, 0.01)
            results(results.length-1)(2)._2 must beCloseTo(48.780, 0.01)
            results(results.length-1)(2)._3 must beCloseTo(260.792, 1) 
        }
    }
    * 
    */
}