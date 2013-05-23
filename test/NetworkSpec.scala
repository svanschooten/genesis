package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import models._

class NetworkSpec extends Specification {

    "Network" should {
        "return correct results" in new WithApplication {
            val concs = List.fill(1503)(1.0)
            val A = CodingSeq("A",concs.zip(concs), true)
            val B = CodingSeq("B",concs.zip(concs),isInput=true)
            val C = CodingSeq("C",isInput=false)
            val D = CodingSeq("D",isInput=false)
            val E = CodingSeq("E",concs.zip(concs),isInput=true)
            val F = CodingSeq("F",isInput=false)
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
}