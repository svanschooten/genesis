package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models._
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

class NetworkTest extends Specification {
	val pa = new CodingSeq("A", List((0.1, 0.1)), true)
	val pb = new CodingSeq("B", List((0.2, 0.3)), true)
	val pc = new CodingSeq("C", List((0.3, 0.2)), false)
	val pd = new CodingSeq("D", List((0.4, 0.3)), false)
	val g1 = new AndGate((pa, pb), pc)
	val g2 = new NotGate(pc, pd)
	
	"CodingSeq" should {
		"retrieve the correct parameters for the coding sequences" in {
			running(FakeApplication()) {
				pa.k2 must equalTo(4.6337)
				pb.d1 must equalTo(0.0205)
				pc.d2 must equalTo(0.8101)
			}
		}

		"retrieve the correct parameters for the and gate" in {
			running(FakeApplication()) {
				g1.k1 must equalTo(4.5272)
				g1.km must equalTo(238.9569)
				g1.n must equalTo(3)
			}
		}

		"retrieve the correct parameters for the not gate" in {
			running(FakeApplication()) {
				g2.k1 must equalTo(3.2155)
				g2.km must equalTo(213.2011)
				g2.n must equalTo(2)
			}
		}
	}

	"Network" should {
		val pas = new CodingSeq("A", List((0.1, 0.1)), true)
		val pbs = new CodingSeq("B", List((0.2, 0.3)), true)
		val pcs = new CodingSeq("C", List((0.3, 0.2)), false)
		val pds = new CodingSeq("D", List((0.4, 0.3)), false)
		val g1s = new AndGate((pas, pbs), pcs)
		val g2s = new NotGate(pcs, pds)
		val simpleNetwork = new Network(List(pas, pbs), -1, "simpleNetworkTest")

		"correctly save a simple network" in {
			running(FakeApplication()) {
			  simpleNetwork.save
			  val id = simpleNetwork.getID
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
				  else if(cur._1=="C"){
				    cur._2 must equalTo("D")
				    cur._3 must equalTo(false)
				  }
				  else{
				    throw new AssertionError("Wrong codingSequence saved.")
				  }
			  }
			  simpleNetwork.delete
			  }
			}
		}
		
		val pac = new CodingSeq("A", List((2.1, 3.4)), true)
		val pbc = new CodingSeq("B", List((1.2, 1.3)), true)
		val pcc = new CodingSeq("C", List((3.3, 2.2)), false)
		val pdc = new CodingSeq("D", List((0.4, 1.3)), false)
		val g1c = new AndGate((pac, pbc), pcc)
		val g2c = new NotGate(pcc, pdc)
		val g3c = new NotGate(pcc, pac)
		val g4c = new AndGate((pcc,pdc),pbc)
		val complexNetwork = new Network(List(pac, pbc), -1, "complexNetworkTest")

		"correctly save a more complex network, containing a cycle and one output to multiple inputs" in {
			running(FakeApplication()) {
			  complexNetwork.save
			  val id = complexNetwork.getID
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
				    throw new AssertionError("Wrong codingSequence saved.")
				  }
			  }
			  complexNetwork.delete
			  }
			}
		}

		"correctly load a simple network" in {
			running(FakeApplication()) {
			  
			}
		}

		"correctly load a more complex network" in {
			running(FakeApplication()) {
			  
			}
		}

	}
}