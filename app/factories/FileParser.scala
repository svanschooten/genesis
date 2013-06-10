package factories

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import libs.json.{Json, __}
import anorm._
import anorm.SqlParser._

import models._
import views._
import scalation._
import factories._

object FileParser {
  
  /**
   * Inserts a new protein library into the database and assigns a libraryID
   */
  def createLibrary(userid: Int, libraryname: String) {
    DB.withConnection { implicit connection =>
       SQL("insert into proteinlibraries(userid,libraryname) values({userid},{libname})")
	      .on(
	          'userid -> userid,
	          'libname -> libraryname
	      ).executeUpdate()
    }
  }
  
  /**
   * Returns the libraryID that corresponds with the input values userid and libraryname
   * Userid = -1 means the library is public.
   */
  def getLibraryID(userid: Int, libraryname: String): Int = {
      DB.withConnection { implicit connection =>
      	  val idResult = SQL(
	          """
	          select libraryid from proteinlibraries
	          where (userid={userid} OR userid=-1) AND libraryname={libraryname}
	          """
	          ).on(
		        'userid -> userid,
		        'libraryname -> libraryname
		      ).apply()
		  if(idResult.size==0) -1
		  else idResult.head[Int]("libraryid") 
	    }
    }
  
  /**
   * Saves all protein in the input file to the database with the corresponding parameters.
   * This proteinlibrary is only accessible by the user with ID userid.
   */
  def saveParams(input: Array[String], partType: String, userid: Int, libraryname: String) = {
	  DB.withConnection { implicit connection =>
	      var libraryid = getLibraryID(userid,libraryname)
	      if(libraryid == -1) createLibrary(userid, libraryname)
	      libraryid = getLibraryID(userid,libraryname)
	      for(i <- 1 to input.length-1){
	        val line = input(i).split(",")
	        if(partType.toUpperCase()=="AND"){
	          val exists = SQL("select * from andparams where libraryid={libraryid} and input1={in1} and input2={in2}")
	          .on(
	             'libraryid -> libraryid,
	             'in1 -> line(0),
	             'in2 -> line(1)
	          ).apply().size > 0
	          if(!exists){
		          SQL("insert into andparams values({libraryid},{tf1},{tf2},{k1},{km},{n})")
		          .on(
		             'libraryid -> libraryid,
		             'tf1 -> line(0),
		             'tf2 -> line(1),
		             'k1 -> line(2).toDouble,
		             'km -> line(3).toDouble,
		             'n -> line(4).toInt
		          ).executeUpdate()
	          }
	        }
	        else if(partType.toUpperCase()=="NOT"){
	          val exists = SQL("select * from notparams where libraryid={libraryid} and input={in}")
	          .on(
	             'libraryid -> libraryid,
	             'in -> line(0)
	          ).apply().size > 0
	          if(!exists){
		          SQL("insert into notparams values({libraryid},{tf},{k1},{km},{n})")
		          .on(
		             'libraryid -> libraryid,
		             'tf -> line(0),
		             'k1 -> line(1).toDouble,
		             'km -> line(2).toDouble,
		             'n -> line(3).toInt
		          ).executeUpdate()
	          }
	        }
	        else if(partType.toUpperCase()=="CDS"){
	          val exists = SQL("select * from cdsparams where libraryid={libraryid} and name={name}")
	          .on(
	             'libraryid -> libraryid,
	             'name -> line(0)
	          ).apply().size > 0
	          if(!exists){
		          SQL("insert into cdsparams values({libraryid},{gene},{k2},{d1},{d2})")
		          .on(
		             'libraryid -> libraryid,
		             'gene -> line(0),
		             'k2 -> line(1).toDouble,
		             'd1 -> line(2).toDouble,
		             'd2 -> line(3).toDouble
		          ).executeUpdate()
	          }
	        }
	      }
	    }
	  Json.toJson("Correctly saved parameters.")
	}
}

