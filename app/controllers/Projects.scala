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
          val c = new Gene(4.1585,(0.0235,0.8338))
          val ag = new AndGate(c,4.5272,238.9569,3)
          val odes = ODEFactory.mkODEs(Array(ag))
          val cVec = new VectorD(Array(8.0,5.0,7.0))
          //solveFolding(t: Double, dt: Double, odes: Array [DerivativeV], cVec: VectorD)
          val result = Rungekuttatest.solveFolding(1, 2, odes, cVec)
          //val vd1 = new VectorD(Array(1.0,2.0,3.0))
          //val vd2 = new VectorD(Array(4.0,5.0,6.0))
	      var l: List[String] = List()
	      // concentrations    H2, O2, O,   H,  OH, H2O
	      //                   0   1   2    3   4   5
	      var con = new VectorD (Array(4.0, 6.0, 0.0, .02, 0.1, 0.8))
	
	      var results = Rungekuttatest.solveFolding(tf, dt, odes, con)
	      var t = t0
	      for (i <- 1 to n - 1) {
	          if ( results.isEmpty){
	            c = new VectorD(Array(0.0))
	          } else {
	            c = results.head
	            results = results.tail
	          }
	            l = l ++ ("> at t = " + "%6.3f".format(t) + " c = " + c :: List())
	            t += dt
	        }
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
       /*html.index("Welcome")*/
       //html.proteinform(ProteinForm)
    	html.rungekutte("Runge-Kutta test app", Rungekuttatest())
      )
    }.getOrElse(Forbidden)
  }
}

