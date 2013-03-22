package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import views._
import play.api.templates.Html

object Home extends Controller {
  
  val homeText = "Some text that'll make you feel right at home"

  def home() = Action {
    Ok(html.base("Home")(Html.apply(homeText)))
  }
  
}