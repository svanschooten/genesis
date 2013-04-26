package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Forms._
import play.api.templates.Html
import models._
import views._

object Home extends Controller with Secured{
  
  val homeText = "Some text that'll make you feel right at home"
   // val proteinAdd = Db.save(Protein("B",4.6122,0.0205,0.8627))
    def home = {
    IsAuthenticated { username => _ =>
      User.findByInlog(username).map { user =>
    	Ok(html.home("Home",Application.database)(Html.apply(homeText)))
    }.getOrElse(Forbidden)
  }
  }
 
}