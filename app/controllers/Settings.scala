package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.templates.Html
import play.api.data.Form
import play.api.data.validation.Constraints._
import play.api._
import models.User
import views.html

object Settings extends Controller with Secured{

  val settingsText = "Some Form"

  val form = Form(
    mapping(
      "Email" -> email.verifying(nonEmpty),
      "Password" -> text,
      "First name" -> optional(text),
      "Last name" -> optional(text)
    )((_email, _password, _fname, _lname) => User(email=_email, password=_password, fname=_fname, lname=_lname))
     ((user: User) => Some(user.email, user.password, user.fname, user.lname))
  )
  
   def settings = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
    	Ok(
    	    html.settings("Settings")
    	    	(Html.apply(settingsText))
    	    	(form)
    	)
    }.getOrElse(Forbidden)
  }
  
  def saveSettings = Action { implicit request =>
      form.bindFromRequest.fold(
	     formWithErrors => BadRequest(html.settings("Settings")(Html.apply(settingsText))(formWithErrors)),
	     value => Ok(Html(value.toString)) // TODO: Implement saving of the settings to DB here!
	  )
  }
}