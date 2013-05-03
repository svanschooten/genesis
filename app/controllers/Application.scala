package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Rungekuttatest

import models._
import views._

object Application extends Controller {
  
  val rkt = Rungekuttatest()
  
  val loginForm = Form(
    tuple(
      "inlog" -> text,
      "password" -> text
    ) verifying ("Wrong inlog or password", result => result match {
      case (inlog, password) => User.authenticate(inlog, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Projects.index).withSession("inlog" -> user._1))
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.jsontest
      )
    ).as("text/javascript")
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
}

/**
 * Provide security features
 */
trait Secured {
  
  /**
   * Retrieve the connected user inlog.
   */
  private def username(request: RequestHeader) = request.session.get("inlog")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}
