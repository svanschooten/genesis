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
				val A = CodingSeq("A",(0,0))
				val B = CodingSeq("B",(0,0))
				val C = CodingSeq("C",(0,0))
				val ABtoC = AndGate((A,B),C)
				ABtoC.input must equalTo((CodingSeq("A",(0,0)),CodingSeq("B",(0,0))))
				ABtoC.output must equalTo(CodingSeq("C",(0,0)))
			}
		}
	}
}