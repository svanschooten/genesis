package controllers

import play.api._
import play.api.mvc._
import models.rungekuttatest

object Application extends Controller {
  
  val rkt = rungekuttatest()
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def rk = Action {
    Ok(views.html.rungekutte("Runge-Kutta test app", rkt))
  }
  
}