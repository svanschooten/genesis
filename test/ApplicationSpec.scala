package test

import org.specs2.mutable._
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc._
import play.api.test.FakeApplication
import play.api.libs.concurrent.Promise

class ApplicationSpec extends Specification {
  sequential
    
  "Application" should {
    
    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/fail")) must beNone        
      }
    }
    
    "render the login page" in {
      running(FakeApplication()) {
        val login = route(FakeRequest(GET, "/login")).get
        
        status(login) must equalTo(OK)
        contentType(login) must beSome.which(_ == "text/html")
        contentAsString(login) must contain ("Please sign in")
      }
    }
    
    /*
     * Tests with session fail, I do not know how to fix this yet.
     */
     "render the home page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/").withSession("email"->"hello@world.com","password"->"helloworld")).get
        
        status(home) must equalTo(200)
      }
    }
        
     "render the help page" in {
      running(FakeApplication()) {
        val help = route(FakeRequest(GET, "/help").withSession("email"->"hello@world.com","password"->"helloworld")).get
        
        status(help) must equalTo(200)
      }
    }
     
     "render the setting page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/settings").withSession("email"->"hello@world.com","password"->"helloworld")).get
        
        status(home) must equalTo(200)
      }
    }
     
     "render the about page" in {
      running(FakeApplication()) {
        val about = route(FakeRequest(GET, "/about").withSession("email"->"hello@world.com","password"->"helloworld")).get
        
        status(about) must equalTo(200)
      }
    }
     
    "render the login page without credentials" in {
      running(FakeApplication()) {
        val result  = route( FakeRequest( GET, "/")).get
        status(result) must equalTo(303)
      }      
    }
  }  
}