package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import views._
import play.api.templates.Html

object Settings extends Controller {
	
  val settingsForm = "Some Form"
  
  def settings = Action {
    Ok(html.base("Settings")(Html.apply(settingsForm)))
  }
  
}