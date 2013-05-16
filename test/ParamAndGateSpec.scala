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
				val A = CodingSeq("A",List((0,0)),true)
				val B = CodingSeq("B",List((0,0)),true)
				val C = CodingSeq("C",List(),false)
				val ABtoC = AndGate((A,B),C)
				ABtoC.input must equalTo((CodingSeq("A",List((0,0)),true),CodingSeq("B",List((0,0)),true)))
				ABtoC.output must equalTo(CodingSeq("C",List(),false))
			}
		}
	}
}