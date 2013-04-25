package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.templates.Html
import play.api.data.Form
import play.api._
import models.User
import views.html

object Settings extends Controller with Secured{
	
  val settingsText = "Some Form"
    
  val form = Form(
      tuple(
          "Email" -> nonEmptyText,
          "Password" -> text,
          "First name" -> text,
          "Last name" -> text
      )
  )
  
  
   def settings = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
    	Ok(
    	    html.settings("Settings")
    	    	(Html.apply(settingsText))
    	    	(form.fill(user.email, user.password, user.fname, user.lname))
    	)
    }.getOrElse(Forbidden)
  }
  
  def saveSettings = Action { implicit request =>
	  form.bindFromRequest.fold(
	     formWithErrors => BadRequest(html.settings("Settings")(Html.apply(settingsText))(formWithErrors)),
	     value => Ok(Html("It werks!")) // TODO: Implement saving of the settings to DB here!
	  )
  }
}