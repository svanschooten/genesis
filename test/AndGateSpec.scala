package test

import org.specs2.mutable._
import play.api.test._
import models._

class AndGateSpec extends Specification {
	"AndGate" should {
        "return correct results" in new WithApplication {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs),true)
            val B = CodingSeq("B",concs.zip(concs),true)
            val C = CodingSeq("C",isInput=false)
            val ABtoC = AndGate((A,B),C)
            val net = new Network(List(A,B),-1,"")
            val results = net.simulate(1500.0)
            // expected results generated using MATLAB R2012b
            // A: 1 1
            // B: 1 1
            // C: 1.41189714584990e-05		7.04461969998677e-05
            results(results.length-1)(0)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(0)._3 must beCloseTo(1.0, 0.1)
            results(results.length-1)(1)._2 must beCloseTo(1.41e-5, 1e-5)
            results(results.length-1)(1)._3 must beCloseTo(7.05e-5, 1e-5)
            results(results.length-1)(2)._2 must beCloseTo(1.0, 0.1)
            results(results.length-1)(2)._3 must beCloseTo(1.0, 0.1)
        }
    }
}