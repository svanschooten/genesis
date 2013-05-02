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
          //CodingSeq(k2:Double, d: (Double,Double), var concentration: (Double, Double))
          val pa = new CodingSeq(4.6337,(0.0240,0.8466),(1.0,2.0),None)
          val pb = new CodingSeq(4.6122,(0.0205,0.8627),(2.0,5.0),None)
          val pc = new CodingSeq(4.1585,(0.0235,0.8338),(0.0,2.0),None)
          //AndGate(input: (CodingSeq, CodingSeq), output: CodingSeq, k1: Double, Km: Double, n: Int)
          val g1 = new AndGate((pa,pb),pc,4.5272,238.9569,3)
          val odes = ODEFactory.mkODEs(List(g1))
          /*val pa = new ProteinActivator("A",0,List(),List(1,2),List(3,4),5,6)
          val pb = new ProteinActivator("B",0,List(),List(1,2),List(3,4),5,6)
          val pc = new ProteinActivator("C",0,List(pa,pb),List(1,2),List(3,4),5,6)
          val chain = new ProteinChain(List(pa,pb),List(List(1,2),List(3)))
          val parts = Part.parseProteinChain(chain)
          val odes = ODEFactory.mkODEs(parts).toArray*/
          
          l::=odes.length.toString()
          //l::=("parts.length: "+parts.length.toString())
          odes.foreach(r => l = l:+r.toString)
          val cVec = new VectorD(Array(8.0,5.0,7.0))
          val t0 = 0.0
	      val tf = 5.0
	      val n  = 200.0
	      val dt = tf / n
	      //var result = Rungekuttatest.solve(t0, tf, dt, odes, cVec)
	      var result = ODEFactory.solve(odes)
	      var t = t0
	      l::=result.length.toString()
	      result.foreach(r => l::=r.toString)
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
       //html.proteinform(ProteinForm)
    	html.rungekutte("Runge-Kutta test app", Rungekuttatest())
      )
    }.getOrElse(Forbidden)
  }
}

