package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object ClassTest extends Controller {
  def something(msg : String) = Action {
	    Ok(html.index(msg))
	 }
}
