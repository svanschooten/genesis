package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import play.api.templates.Html

import models._
import views._

object Settings extends Controller with Secured{
	
  val settingsForm = "Some Form"
  
   def settings = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
    	Ok(html.main("Settings")(Html.apply(settingsForm)))
    }.getOrElse(Forbidden)
  }
}