package controllers

import play.api._
import play.api.mvc._
import models.Rungekuttatest
import play.libs.Json
import templates.Html

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def rk = Action {
    Ok(views.html.rungekutte("Runge-Kutta test app", Rungekuttatest()))
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        controllers.routes.javascript.Application.getJsonTest
      )
    ).as("text/javascript")
  }

  def getJsonTest = Action {
    Ok(Rungekuttatest.getJsonTest)
  }
  
  def jsontest = Action {
    Ok(views.html.rungekutte("Runge-Kutta json test",Rungekuttatest()))
  }
}
