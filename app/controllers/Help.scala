package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import views._
import play.api.templates.Html

object Help extends Controller {

  val helpText = "A lot of useful text"
  
  def help = Action {
    Ok(html.base("Help")(Html.apply(helpText)))
  }
  
}