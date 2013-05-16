package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models._

//geen idee of dit nuttig is
class ParamAndGateSpec extends Specification {
	"Parameter" should {
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
}