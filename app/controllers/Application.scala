package controllers

import play.api._
import play.api.mvc._
import models.Rungekuttatest

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def rk = Action {
    Ok(views.html.rungekutte("Runge-Kutta test app", Rungekuttatest()))
  }
  
}