package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import views._
import play.api.templates.Html

object About extends Controller {
  
  val aboutText = "A lot of About text"

  def about() = Action {
    Ok(html.base("About")(Html.apply(aboutText)))
  }
  
}