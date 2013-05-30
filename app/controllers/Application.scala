package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.BodyParsers.parse
import models.Rungekuttatest
import libs.json.{Json, __}

import models._
import views._
import factories._

/** The Application object handles everything related to authentication. */
object Application extends Controller {

  val rkt = Rungekuttatest()

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
      user => {
        println(user)
        Redirect(routes.Home.home).withSession("email" -> user._1)}
    )
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.jsontest,
        routes.javascript.Application.getlibrary,
        routes.javascript.Application.getCooking,
        routes.javascript.Application.getalllibraries,
        routes.javascript.Application.simulate
      )
    ).as("text/javascript")
  }

  def simulate = Action { implicit request =>
    val data = request.body.asJson
    Ok("Hier moeten de resultaten in JSON komen")
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
      val libraryId = (request.body \ "id").asOpt[Int]
      libraryId match{
        case Some(id) => {
          val jsonObject = Json.obj("and"->ProteinJSONFactory.proteinAllAndParamsJSON(id),
            "not"->ProteinJSONFactory.proteinNotParamsJSON(id),
            "cds"->ProteinJSONFactory.proteinCDSParamsJSON(id))
          Ok(jsonObject).as("plain/text")
        }
        case _ => BadRequest("invalid JSON")
      }

  }
  
  def rk = Action {
    Ok(views.html.rungekutte("good ol' runge kutta test",rkt))
  }

  def jsontest = Action {
    Ok(Rungekuttatest().genJson).as("text/plain")
  }

  def canvastest = Action {
    Ok(views.html.canvastest("just a quick test with a canvas"))
  }

  def morefun = Action {
    Ok(views.html.mofu("another test with the lastest and greatest model"))
  }

  def plumbtest = Action {
    Ok(views.html.plumbtest("Testing jsPlumb"))
  }
  
    def dndtest = Action {
    Ok(views.html.dndtest("Testing jsPlumb"))
  }

  def rungekutta = Action {
    Ok(views.html.rungekutte("Testing the plot and rungeKutta", rkt))
  }

  def getCooking = Action(parse.json) { implicit request =>
    println(request.body)
    /*val body = request.body
    val data = body.asText
    if(data.isEmpty)
        BadRequest("Sorry, you need to provide data.")
    else*/
        Ok(Network.fromJSON(request.body).simJson(1500.0)).as("text/plain")
        //Ok("blA")
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
