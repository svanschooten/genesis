package test
import org.specs2.mutable._
import controllers.Admin
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc._
import play.api.test.FakeApplication
import play.api.db.DB
import anorm._
import play.api.Play.current
import models.User


class AdminSpec extends Specification{
  case class User(email: String, password: String, fname: Option[String], lname: Option[String])
  
   "User create page" should {
	  "be rendered" in {
	    running(FakeApplication()) {
	      val createPage = route(FakeRequest(GET, "/newuser")).get 
	      status(createPage) must equalTo(OK)
	    }
	  }
	}
	
	"Incorrect inputs for create user" should {
	  "not be saved" in {
	    running(FakeApplication()) {
	     val save = Admin.saveUser(FakeRequest().withFormUrlEncodedBody())
         status(save) must equalTo(BAD_REQUEST)
	    }
	  }
	}
	
    "Correct inputs for create user" should {
	  "be saved" in {
	    running(FakeApplication()) {
	     val save = Admin.saveUser(FakeRequest().withFormUrlEncodedBody("Email" -> "hello@hi.com", "Password"->"hi","First name" -> "name", "Last name"-> "name"))
	     status(save) must equalTo(OK)
	     
	     DB.withConnection { implicit connection =>
				val deleteLib = SQL(""" DELETE FROM "User" WHERE fname={name}""")
			    .on('name -> "name").executeUpdate()
			}
	    }
	  }
	}
    
    "Form" should {
	  "be correct" in {
	    running(FakeApplication()) {
    	 val user = models.User(-1,"hello@hi.com", "hi",Option("name"),Option("name"))
	     val fas = Admin.form.fill(user).get
	     fas.email must equalTo("hello@hi.com")
	     fas.fname must equalTo(Option("name"))
	     fas.lname must equalTo(Option("name"))
	    }
	  }
	}
}