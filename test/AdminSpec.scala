package test
import org.specs2.mutable._
import controllers.Admin
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc._
import play.api.test.FakeApplication

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
}