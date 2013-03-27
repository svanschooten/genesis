package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import models._
import views._
import play.api.templates.Html

object About extends Controller with Secured{
  
  val aboutText = "A lot of About text"
  
  def about = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
    	Ok(html.base("About")(Html.apply(aboutText)))
    }.getOrElse(Forbidden)
  }
}