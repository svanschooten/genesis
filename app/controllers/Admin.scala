package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.templates.Html

import models._
import views._

import com.github.t3hnar.bcrypt._

object Admin extends Controller with Secured{
  val form = Form(
    mapping(
      "Email" -> email.verifying(nonEmpty),
      "Password" -> text,
      "First name" -> optional(text),
      "Last name" -> optional(text)
    )((_email, _password, _fname, _lname) => User(email=_email, password=_password, fname=_fname, lname=_lname))
     ((user: User) => Some(user.email, user.password, user.fname, user.lname))
  )
  
  def createUser = Action {
	  Ok(html.newuser("Admin")(form))
  }
  
  def saveUser = Action { implicit request =>
  	form.bindFromRequest.fold(
  		formWithErrors => BadRequest(html.newuser("Admin")(formWithErrors)),
  		value => {
  		  User.create(value.email, value.password, value.fname, value.lname)
  		  Ok(Html("Success!"))
  		}
  	)
  }
}