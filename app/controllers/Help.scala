package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import models._
import views._
import play.api.templates.Html

object Help extends Controller with Secured {

  val helpText = "A lot of useful text"
  
  def help() = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
    	Ok(html.base("Help")(Html.apply(helpText)))
    }.getOrElse(Forbidden)
  }
}