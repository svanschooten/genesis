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
      {
        case (name1, name2) => {
          
          Ok(html.formResult(name1, name2, 1, 2, 3))
        }   
      }
    )
  }
  
  val ProteinForm = Form(
    tuple(
      "name1" -> text,
      "name2" -> optional(text)
    )
  )

  def index = IsAuthenticated { username => _ =>
    User.findByInlog(username).map { user =>
      Ok(
       /*html.index("Welcome")*/
       html.proteinform(ProteinForm)
      )
    }.getOrElse(Forbidden)
  }
}

