package test

import org.specs2.mutable._
import models._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

class UserSpec extends Specification{
sequential

   "User" should {
     "be created correctly" in {
       running(FakeApplication()) {
         val createUser = User.create("throw@away.com", "testUser", Option("throw"), Option("away"))
         DB.withConnection { implicit connection => 
			  val user = SQL(""" select email,fname,lname from "User" where email={mail} """)
			  				.on('mail -> "throw@away.com")
			  				.as {
			      	  		get[String]("email")~get[String]("fname")~get[String]("lname") map{
			      	  		  case email~fname~lname => (email,fname,lname)
			      	  		} * } 
			  user.size must equalTo(1)
			  for (info <- user) {
				  info._1 must equalTo("throw@away.com")
				  info._2 must equalTo("throw")
				  info._3 must equalTo("away")
			  }
			  val deleteLib = SQL(""" DELETE FROM "User" WHERE email = {email} """
	    	     ).on('email -> "throw@away.com").executeUpdate()
         }
       }
     }
   }
   
	"Find user by Email" should {
	  "be correct" in {
	    running(FakeApplication()) {
	      val findUserByEmail = User.findByEmail("test@user.com")
	      for (info <- findUserByEmail) {
	        info.email must equalTo("test@user.com")
	        info.fname must equalTo(Some("tester"))
	        info.lname must equalTo(Some("user"))
	      }
	    }
	  }
	}
	
	/*
	 * Testing authentication with different inputs
	 */
	"Authentication" should {
	  "be correct" in {
	    running(FakeApplication()) {
	      val authen = User.authenticate("test@user.com", "$2a$12$2otYEkWr.kIMw69E9E4kS.yC7gv9dPDCJ97ubZk/vvrYhZEb09vvO")
	    	for(info <- authen) {
	    	  info.email must equalTo("test@user.com")
	    	  info.fname must equalTo("tester")
	    	  info.lname must equalTo("user")
	    	}
	    }
	  }
	}
	
	"Authentication" should {
	  "be return none" in {
	    running(FakeApplication()) {
	      val authen = User.authenticate("fail@fail.com", "failure")
	    	for(info <- authen) {
	    	  info.email must equalTo(None)
	    	  info.fname must equalTo(None)
	    	  info.lname must equalTo(None)
	    	}
	    }
	  }
	}
	
	"Authentication" should {
	  "be return Some" in {
	    running(FakeApplication()) {
	      val authen = User.authenticate("test@user.com", "updated")
	    	for(info <- authen) {
	    	  info.email must equalTo("test@user.com")
	    	  info.fname must equalTo(Some("tester"))
	    	  info.lname must equalTo(Some("user"))
	    	}
	    }
	  }
	}
}