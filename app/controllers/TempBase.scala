package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._

import views._

object TempBase extends Controller {
	
	def hello() = Action {
		Ok(html.base("TITLE LIKES CAPS LOCK"))
	}
}