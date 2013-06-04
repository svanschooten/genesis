package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.BodyParsers.parse
import libs.json.{Json, __}

import models._
import views._
import factories._

/** The Application object handles everything related to authentication. */
object Application extends Controller {

  /** Form used for authenticating a user. */
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Wrong email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /** Login page. */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }
  
  /** Logout page */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  /** Handle login form submission. */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Home.home).withSession("email" -> user._1)
    )
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.getlibrary,
        routes.javascript.Application.getCooking,
        routes.javascript.Application.getallcircuits,
        routes.javascript.Application.getalllibraries
      )
    ).as("text/javascript")
  }
  
  def getalllibraries = Action { implicit request =>
    request.session.get("user") match{
      case Some(email) => {
        User.findByEmail(email) match{
          case Some(u) => {
            Ok(ProteinJSONFactory.libraryListJSON(u.id)).as("text/plain")
          }
          case _ => BadRequest("No user found")
        }
      }
      case _ => BadRequest("No email found")
    }
  }

  def getlibrary = Action(parse.json) { implicit request =>
    val id = Integer.parseInt((request.body \ "id").as[String])
    val jsonObject = Json.obj("and"->ProteinJSONFactory.proteinAllAndParamsJSON(id),
      "not"->ProteinJSONFactory.proteinNotParamsJSON(id),
      "cds"->ProteinJSONFactory.proteinCDSParamsJSON(id))
    Ok(jsonObject).as("plain/text")
  }

  def getallcircuits = Action { implicit request =>
    request.session.get("user") match{
      case Some(email) => {
        User.findByEmail(email) match{
          case Some(u) => {
            val userNetworks = Network.getNetworks(u.id)   //TODO ANTOOOON!!
            Ok("test").as("text/plain")
          }
          case _ => BadRequest("No user found")
        }
      }
      case _ => BadRequest("No email found")
    }
  }

  def savecircuit = Action(parse.json) { implicit request =>
    //Ok(Network.saveCircuit(request.body))  TODO eerst parsen en simulatie scheiden.
    Ok("Placeholder")
  }

  def getCooking = Action(parse.json) { implicit request =>
    Ok(Network.simulate(request.body)).as("text/plain")
  }
}

/** Provide security features */
trait Secured {
  
  /** Retrieve the connected user. */
  private def username(request: RequestHeader) = request.session.get("email")

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  /** Action for authenticated users. */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}
