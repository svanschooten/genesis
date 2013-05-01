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

  val database = Db.query[Protein].fetch()
  //val database = Db.fetchWithSql[Protein]("SELECT id FROM protein")
  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }
  
  /**
   * Logout
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Home.home).withSession("inlog" -> user._1)
    )
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.jsontest
      )
    ).as("text/javascript")
  }
  
  def jsontest = Action {
    Ok(Rungekuttatest().genJson).as("text/plain")
  }

  def canvastest = Action {
    Ok(views.html.canvastest("just a quick test with a canvas"))
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


  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}
