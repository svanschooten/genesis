package factories

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.json._

import models._
import views._
import scalation._
import factories._

object ProteinJSONFactory {
  
  def libraryListJSON(userID: Int) = {
    DB.withConnection { implicit connection =>
      val results = SQL("select * from proteinlibraries where userid={userid} or userid=-1")
      .on('userid -> userID)
      .as{
  		get[String]("libraryname")~get[Int]("libraryid") map{
  		  case lbname~lbid => (lbname,lbid)
  		} *
      }
      Json.toJson(results.map(data => {
    	  Json.obj("libraryname"->data._1,"libraryId"->data._2)
      	}))
      }
    }
  
  def proteinAllAndParamsJSON(libraryID: Int = 0) = {
    DB.withConnection { implicit connection =>
      val results = SQL("select * from andparams where libraryid = {libraryid}")
      .on('libraryid -> libraryID)
      .as{
  		get[String]("input1")~get[String]("input2")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
  		  case in1~in2~k1~km~n => (in1,in2,k1,km,n)
  		} *
      }
      Json.toJson(results.map(data => {
            Json.obj("input1"->data._1,"input2"->data._2,"k1"->data._3,"km"->data._4,"n"->data._5)
      }))
    }
  }
  
  def proteinAndParamsJSON(input1: String, libraryID: Int = 0) = {
    DB.withConnection { implicit connection =>
      val results = SQL("""select * from andparams where libraryid = {libraryid}
    		  				and (input1={input1} or input2={input1})""")
      .on('libraryid -> libraryID,
          'input1 -> input1)
      .as{
  		get[String]("input1")~get[String]("input2")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
  		  case in1~in2~k1~km~n if(in1==input1) => (in2,k1,km,n)
  		  case in1~in2~k1~km~n if(in2==input1) => (in1,k1,km,n)
  		} *
      }
      Json.toJson(results.map(data => {
        Json.obj(data._1 ->
            Json.obj("k1"-> data._2,"km"-> data._3,"n"-> data._4))
      }))
    }
  }
  
  def proteinNotParamsJSON(libraryID: Int = 0) = {
    DB.withConnection { implicit connection =>
      val results = SQL("select * from notparams where libraryid = {libraryid}")
      .on('libraryid -> libraryID)
      .as{
  		get[String]("input")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
  		  case in~k1~km~n => (in,k1,km,n)
  		} *
      }
      Json.toJson(results.map(data => {
        Json.obj(data._1 ->
            Json.obj("k1"-> data._2,"km"-> data._3,"n"-> data._4))
      }))
    }
  }
  
  def proteinCDSParamsJSON(libraryID: Int = 0) = {
    DB.withConnection { implicit connection =>
      val results = SQL("select * from cdsparams where libraryid = {libraryid}")
      .on('libraryid -> libraryID)
      .as{
  		get[String]("name")~get[Double]("k2")~get[Double]("d1")~get[Double]("d2") map{
  		  case name~k2~d1~d2 => (name,k2,d1,d2)
  		} *
      }
      val res = Json.toJson(results.map(data => {
        Json.obj(data._1 ->
            Json.obj("k2"-> data._2,"d1"-> data._3,"d2"-> data._4))
      }))
      println(res)
      res
    }
  }
}

