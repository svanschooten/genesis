package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import models._

class NetworkSpec extends Specification {
/*
    "Network" should {
        "return correct results" in new WithApplication {
            val A = CodingSeq("A",List((0,0)),true)
            val B = CodingSeq("B",List((0,0)),true)
            val C = CodingSeq("C",List(),false)
            val D = CodingSeq("D",List(),false)
            val E = CodingSeq("E",List((0,0)),true)
            val F = CodingSeq("F",List(),false)
            val notA = NotGate(A,C)
            val notAandB = AndGate((C,B),D)
            val notAandBandE = AndGate((D,E),F)
            val net = new Network(List(A,B,E),-1,"")
            val results = net.simulate(5.0)
            // expected results generated using MATLAB R2012b
            //._2 = mRna, ._3 = protein
            // B: 48.7804878048759	260.792124555058
            // D: 334.934010152227	1279.12460265271
            // F: 271.331210172459	941.508188692855
            // A: 41.6666666666666	228.054374360186
            // E: 61.3496932500570	148.920900749709
            // C: 99.7682070794492	497.584659558515
            //println(results(results.length-1))
            results(results.length-1)(0)._2 must beCloseTo(48.780, 0.01)
            results(results.length-1)(0)._3 must beCloseTo(260.792, 0.01)
            results(results.length-1)(1)._2 must beCloseTo(334.934, 0.01)
            results(results.length-1)(1)._3 must beCloseTo(1279.124, 0.01)
            results(results.length-1)(2)._2 must beCloseTo(271.331, 0.01)
            results(results.length-1)(2)._3 must beCloseTo(941.508, 1) // oscillates slightly, don't get too close
            results(results.length-1)(3)._2 must beCloseTo(41.666, 0.01)
            results(results.length-1)(3)._3 must beCloseTo(228.054, 0.01)
            results(results.length-1)(4)._2 must beCloseTo(61.349, 0.01)
            results(results.length-1)(4)._3 must beCloseTo(148.920, 0.01)
            results(results.length-1)(5)._2 must beCloseTo(99.768, 0.01)
            results(results.length-1)(5)._3 must beCloseTo(497.584, 0.01)
        }
    }
    * 
    */
}