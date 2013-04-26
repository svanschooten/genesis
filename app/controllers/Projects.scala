package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._
import scalation._
import factories._

object Projects extends Controller with Secured {
  
  /**
   * Handles the form submission.
   */
  def formFunction = Action { implicit request =>
    ProteinForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index("error")),
      {
        case (name1, name2) => {
          var l: List[String] = List()
          val pa = new ProteinActivator("A",0,List(),List(1,2),List(3,4),5,6)
          val pb = new ProteinActivator("B",0,List(),List(1,2),List(3,4),5,6)
          val pc = new ProteinActivator("C",0,List(pa,pb),List(1,2),List(3,4),5,6)
          val chain = new ProteinChain(List(pa,pb),List(List(1,2),List(3)))
          val parts = Part.parseProteinChain(chain)
          val odes = ODEFactory.mkODEs(parts).toArray
          l::=chain.toString()
          l::=("parts.length: "+parts.length.toString())
          odes.foreach(r => l = l:+r.toString)
          val cVec = new VectorD(Array(8.0,5.0,7.0))
          val t0 = 0.0
	      val tf = 5.0
	      val n  = 200.0
	      val dt = tf / n
	      var result = Rungekuttatest.solveFolding(0.0, 0.01, odes, cVec)
	      var t = t0
	      result.foreach(r => l::=("t = " + "%6.3f".format(t) + " : " + (r._1+r._2).toString))
          Ok(html.formResult(l))
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
       //html.index("Welcome")
       html.proteinform(ProteinForm)
    	//html.rungekutte("Runge-Kutta test app", Rungekuttatest())
      )
    }.getOrElse(Forbidden)
  }
}

