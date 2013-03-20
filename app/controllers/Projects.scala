package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Projects extends Controller with Secured {

  def index = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
      Ok(
       html.index("Welcome to GENESIS")   
      )
    }.getOrElse(Forbidden)
}
}

