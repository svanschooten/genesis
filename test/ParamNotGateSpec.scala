package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models._

//geen idee of dit nuttig is
class ParamNotGateSpec extends Specification {
	"Parameter" should {

		"return correct results" in {
			running(FakeApplication()) {
				val A = CodingSeq("A",List((0,0)),true)
				val B = CodingSeq("B",List((0,0)),false)
				val AtoB = NotGate(A,B)
				AtoB.input must equalTo(CodingSeq("A",List((0,0)),true))
				AtoB.output must equalTo(CodingSeq("B",List(),false))
			}
		}
	}
}