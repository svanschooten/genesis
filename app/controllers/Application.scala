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

  def getJsonTest = Action {
    Ok(Rungekuttatest.getJsonTest)
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.getJsonTest
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
