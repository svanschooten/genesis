package test

import scala.collection.mutable.Set
import org.specs2.mutable._
import models._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

class NetworkTest extends Specification {
	running(FakeApplication()){
		val pa = CodingSeq("A", List((0.1, 0.1)), true)
		val pb = CodingSeq("B", List((0.2, 0.3)), true)
		val pc = CodingSeq("C", List((0.3, 0.2)), false)
		val pd = CodingSeq("D", List((0.4, 0.3)), false)
		val g1 = AndGate((pa, pb), pc)
		val g2 = NotGate(pc, pd)
		
		"CodingSeq" should {
			"retrieve the correct parameters for the coding sequences" in {
				pa.k2 must equalTo(4.6337)
				pb.d1 must equalTo(0.0205)
				pc.d2 must equalTo(0.8338)
			}
	
			"retrieve the correct parameters for the and gate" in {
				g1.k1 must equalTo(4.5272)
				g1.km must equalTo(238.9569)
				g1.n must equalTo(3)
			}
	
			"retrieve the correct parameters for the not gate" in {
				g2.k1 must equalTo(3.2155)
				g2.km must equalTo(213.2011)
				g2.n must equalTo(1)
			}
		}
		
		val pas = CodingSeq("A", List((0.1, 0.1)), true)
		val pbs = CodingSeq("B", List((0.2, 0.3)), true)
		val pcs = CodingSeq("C", List((0.3, 0.2)), false)
		val pds = CodingSeq("D", List((0.4, 0.3)), false)
		val g1s = AndGate((pas, pbs), pcs)
		val g2s = NotGate(pcs, pds)
		val simpleNetworkSave = new Network(List(pas, pbs), -1, "simpleNetworkSaveTest")
		
		val pac = CodingSeq("A", List((2.1, 3.4)), true)
		val pbc = CodingSeq("B", List((1.2, 1.3)), true)
		val pcc = CodingSeq("C", List((3.3, 2.2)), false)
		val pdc = CodingSeq("D", List((0.4, 1.3)), false)
		val g1c = AndGate((pac, pbc), pcc)
		val g2c = NotGate(pcc, pdc)
		val g3c = NotGate(pcc, pac)
		val g4c = AndGate((pcc,pdc),pbc)
		val complexNetworkSave = new Network(List(pac, pbc), -1, "complexNetworkSaveTest")
	
		"Network" should {
			"correctly save a simple network" in {
			  running(FakeApplication()){
			    simpleNetworkSave.save
				  val id = simpleNetworkSave.getID
				  DB.withConnection { implicit connection => 
					  val cds = SQL("select name,next,isInput from cds where networkid={id}")
					  				.on('id -> id)
					  				.as {
					      	  		get[String]("name")~get[String]("next")~get[Boolean]("isInput") map{
					      	  		  case name~next~isInput => (name,next,isInput)
					      	  		} * }
					  cds.size must equalTo(3)
					  for(cur <- cds){
						  if(cur._1=="A"||cur._1=="B"){
						    cur._2 must equalTo("C")
						    cur._3 must equalTo(true)
						  }
						  else if(cur._1=="C"){
						    cur._2 must equalTo("D")
						    cur._3 must equalTo(false)
						  }
						  else{
						    throw new AssertionError("Network incorrectly saved.")
						  }
					  }
					  simpleNetworkSave.delete
				  }
			    }
			}
			
			"correctly save a more complex network, containing a cycle and one output to multiple inputs" in {
				running(FakeApplication()){
					  complexNetworkSave.save
					  val id = complexNetworkSave.getID
					  DB.withConnection { implicit connection => 
						  val cds = SQL("select name,next,isInput from cds where networkid={id}")
						  				.on('id -> id)
						  				.as {
						      	  		get[String]("name")~get[String]("next")~get[Boolean]("isInput") map{
						      	  		  case name~next~isInput => (name,next,isInput)
						      	  		} * }
						  for(cur <- cds){
							  if(cur._1=="A"||cur._1=="B"){
							    cur._2 must equalTo("C")
							    cur._3 must equalTo(true)
							  }
							  else if(cur._1=="D"){
							    cur._2 must equalTo("B")
							    cur._3 must equalTo(false)
							  }
							  else if(cur._1=="C"&&(cur._2=="A"||cur._2=="B"||cur._2=="D")){
							    cur._3 must equalTo(false)
							  }
							  else{
							    throw new AssertionError("Network incorrectly saved.")
							  }
						  }
					   complexNetworkSave.delete
					 }
				}
			}
				
			"correctly load a simple network" in {
			  running(FakeApplication()){
			    val simpleNetworkLoad = Network.load(-1,"simpleNetworkLoadTest",0)
			    val start = simpleNetworkLoad.inputs
			    start.size must equalTo(2)
			    
			    def check(cs: CodingSeq) {
			      if(cs.name=="A"||cs.name=="B"){
			        cs.linksTo.size must equalTo(1)
			        cs.linksTo.head.output.name must equalTo("C")
			        cs.isInput must equalTo(true)
			      }
			      else if(cs.name=="C"){
			        cs.linksTo.size must equalTo(1)
			        cs.linksTo.head.output.name must equalTo("D")
			        cs.isInput must equalTo(false)
			      }
			      else if(cs.name=="D"){
			        cs.linksTo.size must equalTo(0)
			        cs.isInput must equalTo(false)
			      }
			      else throw new AssertionError("Network incorrectly loaded: Found CodingSequence "+cs.name)
			      for(next <- cs.linksTo){
			        check(next.output)
			      }
			    }
			    
			    for(cs <- start){
			      check(cs)
			    }
			  }
			}
			
			"correctly load a more complex network" in {
			  running(FakeApplication()){
			    val complexNetworkLoad = Network.load(-1,"complexNetworkLoadTest",0)
			    val start = complexNetworkLoad.inputs
			    start.size must equalTo(2)
			    val seen:Set[String] = Set()
			    
			    def check(cs: CodingSeq) {
			      if(seen contains cs.name) return
			      seen += cs.name
			      if(cs.name=="A"||cs.name=="B"){
			        cs.linksTo.size must equalTo(1)
			        cs.linksTo.head.output.name must equalTo("C")
			        cs.isInput must equalTo(true)
			      }
			      else if(cs.name=="C"){
			        cs.linksTo.size must equalTo(3)
			        for(link <- cs.linksTo){
			          val name = link.output.name
			          if(!(name=="A" || name=="B" || name=="D")) throw new AssertionError("Network incorrectly loaded")
			        }
			        cs.isInput must equalTo(false)
			      }
			      else if(cs.name=="D"){
			        cs.linksTo.size must equalTo(1)
			        cs.linksTo.head.output.name must equalTo("B")
			        cs.isInput must equalTo(false)
			      }
			      else throw new AssertionError("Network incorrectly loaded: Found CodingSequence "+cs.name)
			      for(next <- cs.linksTo){
			        check(next.output)
			      }
			    }
			    
			    for(cs <- start){
			      check(cs)
			    }
			  }
			}
		}
	}
}