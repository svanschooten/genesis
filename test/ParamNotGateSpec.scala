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
				val A = CodingSeq("A",(0,0))
				val B = CodingSeq("B",(0,0))
				val AtoB = NotGate(A,B)
				AtoB.input must equalTo(CodingSeq("A",(0,0)))
				AtoB.output must equalTo(CodingSeq("B",(0,0)))
			}
		}
	}
}