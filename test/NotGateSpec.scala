package test

import org.specs2.mutable._
import play.api.test._
import models._

class NotGateSpec extends Specification {
		"NotGate" should {
        "return correct results" in new WithApplication {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs),isInput=true)
            val C = CodingSeq("C",isInput=false)
            val AtoB = NotGate(A,C)
            val net = new Network(List(A))
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
}