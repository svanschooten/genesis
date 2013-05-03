package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import models._
import views._
import play.api.templates.Html

object Help extends Controller with Secured {
  
  /** Placeholder help content */
  val helpText = "A lot of useful text"
  
  /** Placeholder help page */
  def help = IsAuthenticated { email => _ =>
    User.findByEmail(email).map { user =>
    	Ok(html.main("Help")(Html.apply(helpText)))
    }.getOrElse(Forbidden)

  }
}