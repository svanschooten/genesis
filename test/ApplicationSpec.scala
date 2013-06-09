package test

import org.specs2.mutable._
import controllers.Application
import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.db.DB
import anorm._
import play.api.Play.current

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
    
     "render the home page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/").withSession("email"->"hello@world.com","password"->"helloworld")).get
        status(home) must equalTo(OK)
        contentAsString(home) must contain ("Setup the circuit")
      }
    }
     
    "render the home page without credentials" in {
      running(FakeApplication()) {
        val result  = route( FakeRequest( GET, "/")).get
        status(result) must equalTo(303)
      }      
    }
    
     "render the logout page" in {
      running(FakeApplication()) {
    	  val logout = route(FakeRequest(GET, "/logout")).get
	      status(logout) must equalTo(SEE_OTHER)
	      redirectLocation(logout) must equalTo(Some("/login"))
          }
    }
     
     "render incorrect authentication" in {
       running(FakeApplication()) {
         val authen = Application.authenticate(FakeRequest().withFormUrlEncodedBody())
         status(authen) must equalTo(BAD_REQUEST)
  
         val authenWithEmail = Application.authenticate(FakeRequest().withFormUrlEncodedBody("email" -> "hello@world.com"))
         status(authenWithEmail) must equalTo(BAD_REQUEST)
       }
     }
     
     "render correct authentication" in {
       running(FakeApplication()) {
         val authen = Application.authenticate(FakeRequest().withFormUrlEncodedBody("email" -> "hello@world.com","password"->"helloworld"))
         status(authen) must equalTo(SEE_OTHER)
         redirectLocation(authen) must equalTo(Some("/"))
       }
     }
     
     "render javascript routing" in {
       running(FakeApplication()) {
         val jsRoutes = Application.javascriptRoutes(FakeRequest())
         status(jsRoutes) must equalTo(OK)
         contentType(jsRoutes) must equalTo (Some("text/javascript"))
       }
     }
     
     "incorrectly getting all libraries from user" in {
       running(FakeApplication()) {
         val lib = Application.getalllibraries(FakeRequest())
         status(lib) must equalTo(BAD_REQUEST)
         
         val libWithWrongEmail = Application.getalllibraries(FakeRequest().withSession("email" -> "wrong@world.com"))
         status(libWithWrongEmail) must equalTo(BAD_REQUEST)
       }
     }
     
     "get all libraries from user" in {
       running(FakeApplication()) {
         val lib = Application.getalllibraries(FakeRequest().withSession("email"->"hello@world.com"))
         status(lib) must equalTo(OK)
         contentType(lib) must equalTo (Some("text/plain"))
       }
     }
     
     "incorrectly getting all circuits from user" in {
       running(FakeApplication()) {
         val circuit = Application.getallcircuits(FakeRequest())
         status(circuit) must equalTo(BAD_REQUEST)
         
         val circuitWithWrongEmail = Application.getallcircuits(FakeRequest().withSession("email" -> "wrong@world.com"))
         status(circuitWithWrongEmail) must equalTo(BAD_REQUEST)
       }
     }
     
     "get all circuits from user" in {
       running(FakeApplication()) {
         val lib = Application.getallcircuits(FakeRequest().withSession("email"->"hello@world.com"))
         status(lib) must equalTo(OK)
         contentType(lib) must equalTo (Some("text/plain"))
       }
     }
     
     "get library" in {
       running(FakeApplication()) {
         //val lib = Application.getlibrary(FakeRequest().withFormUrlEncodedBody("id"->"A"))
         val json: JsValue = Json.parse ("""
               {"id":"1"}	""")
         val fakereq = FakeRequest().withBody(json)
         val lib = Application.getlibrary(fakereq)
		 status(lib) must equalTo(OK)
         contentType(lib) must equalTo (Some("text/plain"))
       }
     }
     
     "get Cooking and save circuit" in {
       running(FakeApplication()) {
           val json: JsValue = Json.parse ("""
               {"name":"testCircuit",
			    "circuit":
			    {
			        "vertices":[
			            {"id":"input","type":"input","x":30,"y":30},
			            {"id":"output","type":"output","x":470,"y":50},
			            {"id":"not2","type":"not","x":258.140625,"y":159}
			            ],
			        "edges":[
			            {"source":"input","target":"not2","protein":"A"},
			            {"source":"not2","target":"output","protein":"B"}
			            ]
			    },
			    "inputs":"t,A\n0,1",
			    "time":"1",
			    "steps":"1",
			    "library":"2"
        		 }	""")
		
		val fakereq = FakeRequest().withSession("email" -> "hello@world.com","password"->"helloworld").withBody(json)
		val getcooking = Application.getCooking(fakereq)
		status(getcooking) must equalTo(OK)
        contentType(getcooking) must equalTo (Some("text/plain"))
		val savecircuit = Application.savecircuit(fakereq)
		status(savecircuit) must equalTo(OK)
        contentType(savecircuit) must equalTo (Some("text/plain"))
		DB.withConnection { implicit connection =>
				val deleteLib = SQL(" DELETE FROM networkownedby WHERE networkname={name}")
			    .on('name -> "testCircuit").executeUpdate()
				}
       }
     }
  }  
}