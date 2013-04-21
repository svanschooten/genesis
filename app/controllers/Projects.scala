package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Projects extends Controller with Secured {
  
  /**
   * Handles the form submission.
   */
  def formFunction = Action { implicit request =>
    ProteinForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index("error")),
      {case (name, val1, val2) => Ok(html.proteinform(name, val1, val2))}
    )
  }
  
  val ProteinForm = Form(
    tuple(
      "name" -> text,
      "val1" -> number(min = 1, max = 100),
      "val2" -> number(min = 0, max = 10)
    )
  )

  def index = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
      Ok(
       html.index("ss")   
      )
    }.getOrElse(Forbidden)
  }
}

