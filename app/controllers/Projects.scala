package controllers

import scala.collection.mutable.Set
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._
import scalation._
import factories._

object Projects extends Controller with Secured {
  var curNetwork:Network = new Network(List(),-1,"")
  
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
          //val pa = new CodingSeq(4.6337,(0.0240,0.8466),(1.0,2.0),None)
          val pa = new CodingSeq("A",0,List((0.1,0.1)),true)
          val pb = new CodingSeq("B",0,List((0.2,0.3)),true)
          val pc = new CodingSeq("C",0,List((0.3,0.2)),false)
          val pd = new CodingSeq("D",0,List((0.4,0.3)),false)
          val g1 = new AndGate((pa,pb),pc,0)
          val g2 = new NotGate(pc,pd,0)
          val concInput = Array("t,A,B","0,0,0","70,1,0","100,0,1","140,1,1")
          curNetwork = new Network(List(pa,pb),-1,"")
          l::="Concentrations:"
          val setConcs = curNetwork.setStartParameters(concInput, 200, 1, 150, 1)
          /*for(str <- setConcs){
            if(str==null) l::="null"
            else l::=str.toString
          }*/
          for(cs:CodingSeq <- curNetwork.inputs){
            var t = 0
            for(conc:(Double,Double) <- cs.concentration){
              l::=cs.name+" t="+t+": "+conc
              t += 1
            }
          }
          val seen:Set[String] = Set()
          /*def rec(cur: CodingSeq) {
            if(seen contains cur.name) return
            seen += cur.name
            l::=cur.name+"->"+cur.linksTo
            for(next <- cur.linksTo){
              rec(next.output)
            }
          }
          for(cur <- curNetwork.inputs){
            rec(cur)
          }*/
          
          /*l::="pa:"+pa.k2+" "+pa.d1+" "+pa.d2
          l::="pb:"+pb.k2+" "+pb.d1+" "+pb.d2
          l::="pc:"+pc.k2+" "+pc.d1+" "+pc.d2
          l::="g1:"+g1.k1+" "+g1.km+" "+g1.n
          l::="g2:"+g2.k1+" "+g2.km+" "+g2.n*/
          val cVec = new VectorD(Array(8.0,5.0,7.0))
          val t0 = 0.0
	      val tf = 5.0
	      val n  = 200.0
	      val dt = tf / n
	      //var result = Rungekuttatest.solve(t0, tf, dt, odes, cVec)
	      //step(List(pa,pb))
	      //var result = curNetwork.simulate(0.1)
	      var t = t0
	      //l::="result length:"+result.length.toString()
	      //result.foreach(r => l::="result:"+r.toString)
	      //l::=Network.simToJson(result).toString()
	      l=l.reverse
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
    User.findByEmail(username).map { user =>
      Ok(
       //html.index("Welcome")
       html.proteinform(ProteinForm)
    	//html.rungekutte.render("Runge-Kutta test app", Rungekuttatest())
      )
    }.getOrElse(Forbidden)
  }
}

