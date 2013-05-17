package test

import org.specs2.mutable._
import play.api.test._
import models._

class NotGateSpec extends Specification {
  /*
		"Network" should {
        "return correct results" in new WithApplication {
            val A = CodingSeq("A",List((0,0)),true)
            val B = CodingSeq("B",List(),false)
            val AtoB = NotGate(A,B)
            val net = new Network(List(A),-1,"")
            val results = net.simulate(5.0)
            // expected results (still have to be checked)
            // A: 41.666666		228.054374
            // B: 114.368432	611.440923	
            results(results.length-1)(0)._2 must beCloseTo(41.666, 0.01)
            results(results.length-1)(0)._3 must beCloseTo(228.054, 0.01)
            results(results.length-1)(1)._2 must beCloseTo(114.368, 0.01)
            results(results.length-1)(1)._3 must beCloseTo(611.441, 0.01)
        }
    }
    * 
    */
}