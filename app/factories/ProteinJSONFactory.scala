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
  
    //PType can be CDS/AND/NOT
  def proteinParamsJSON(ptype: String, libraryID: Int = 0):JSONObject = {
    if(ptype=="AND") proteinAndParamsJSON(libraryID)
    if(ptype=="NOT") proteinNotParamsJSON(libraryID)
    if(ptype=="CDS") proteinCDSParamsJSON(libraryID)
  }
  
  def proteinAndParamsJSON(libraryID: Int = 0) = {
    DB.withConnection { implicit connection =>
      val results = SQL("select * from andparams where libraryid = {libraryid}")
      .on('libraryid -> libraryID)
      .as{
  		get[String]("input1")~get[String]("input2")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
  		  case in1~in2~k1~km~n => (in1,in2,k1,km,n)
  		} *
      }
      results.flatMap(data => {
        Map((Json.toJson(data._1),Json.toJson(data._2)) ->
            Map("k1"-> Json.toJson(data._3),"km"->Json.toJson(data._4),"n"->Json.toJson(data._5)))
      })
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
      results.flatMap(data => {
        Map(Json.toJson(data._1) ->
            Map("k1"-> Json.toJson(data._2),"km"->Json.toJson(data._3),"n"->Json.toJson(data._4)))
      })
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
      results.flatMap(data => {
        Map(Json.toJson(data._1) ->
            Map("k2"-> Json.toJson(data._2),"d1"->Json.toJson(data._3),"d2"->Json.toJson(data._4)))
      })
    }
  }
}

