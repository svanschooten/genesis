package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import models._
import views._
import play.api.templates.Html

object About extends Controller with Secured{
  
  /** Placeholder content. */
  val aboutText = "A lot of About text"
  
  /** Placeholder about page. */
  def about = IsAuthenticated { email => _ =>
    User.findByEmail(email).map { user =>
    	Ok(html.main("About")(Html.apply("Placeholder"))(Html.apply(aboutText)))
    }.getOrElse(Forbidden)
  }
}