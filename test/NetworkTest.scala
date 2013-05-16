package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import models._

class NetworkTest extends Specification {
  val pa = new CodingSeq("A",List((0.1,0.1)),true)
  val pb = new CodingSeq("B",List((0.2,0.3)),true)
  val pc = new CodingSeq("C",List((0.3,0.2)),false)
  val pd = new CodingSeq("D",List((0.4,0.3)),false)
  val g1 = new AndGate((pa,pb),pc)
  val g2 = new NotGate(pc,pd)
  val network = new Network(List(pa,pb),"testuser","network1")
  
  "CodingSeq" should{
	  "retrieve the correct parameters for the coding sequences" in {
	    pa.k2 must equalTo(4.6337)
	    pb.d1 must equalTo(0.0205)
	    pc.d2 must equalTo(0.8101)
	  }
	  
	  "retrieve the correct parameters for the and gate" in {
	    g1.k1 must equalTo(4.5272)
	    g1.km must equalTo(238.9569)
	    g1.n must equalTo(3)
	  }
	  
	  "retrieve the correct parameters for the not gate"in {
	    g2.k1 must equalTo(3.2155)
	    g2.km must equalTo(213.2011)
	    g2.n must equalTo(2)
	  }
	  
	  "correctly save into the database" in {
	    
	  }
	  
	  "correctly load from the database" in {
	    
	  }
  }
  
  "Network" should {
	  
	  "correctly save a circuit" in {
	    
	  }
	  
	  "correctly load a circuit" in {
	    
	  }
	  
	  
  }
}