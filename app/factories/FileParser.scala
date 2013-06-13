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
import scala.Array
import scala.Predef._

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
  def saveParams(userid: Int, libraryname: String, cds: Array[String], and: Array[String], not: Array[String]) = {
      var libraryid = getLibraryID(userid,libraryname)
	  if(libraryid == -1){
	    createLibrary(userid, libraryname)
	    libraryid = getLibraryID(userid,libraryname)
	  }
	  saveCDSParams(userid, libraryid, cds);
      saveAndParams(userid, libraryid, and);
      saveNotParams(userid, libraryid, not);
	  Json.toJson("Correctly saved parameters.")
  }
  
  def saveCDSParams(userid: Int, libraryid: Int, cds: Array[String]) = {
	  DB.withConnection { implicit connection =>
		  for(i <- 1 to cds.length-1){
			  val line = cds(i).split(",")
			  val exists = SQL("select * from cdsparams where libraryid={libraryid} and name={name}")
			          .on(
			             'libraryid -> libraryid,
			             'name -> line(0)
			          ).apply().size > 0
			  if(exists) {
			    SQL("""
			        update cdsparams set k2={k2}, d1={d1}, d2={d2}
			        where libraryid={libraryid} and name={name}
			        """)
			        .on(
			         'libraryid -> libraryid,
			         'name -> line(0),
			         'k2 -> line(1).toDouble,
			         'd1 -> line(2).toDouble,
			         'd2 -> line(3).toDouble
			      ).executeUpdate()
			  }
			  else {
			      SQL("insert into cdsparams values({libraryid},{name},{k2},{d1},{d2})")
			      .on(
			         'libraryid -> libraryid,
			         'name -> line(0),
			         'k2 -> line(1).toDouble,
			         'd1 -> line(2).toDouble,
			         'd2 -> line(3).toDouble
			      ).executeUpdate()
			  }
		  }
	  }
  }
  
  def saveAndParams(userid: Int, libraryid: Int, and: Array[String]) = {
    DB.withConnection { implicit connection => 
    	for(i <- 1 to and.length-1){
    	  val line = and(i).split(",")
    	  val exists = SQL("select * from andparams where libraryid={libraryid} and input1={in1} and input2={in2}")
	          .on(
	             'libraryid -> libraryid,
	             'in1 -> line(0),
	             'in2 -> line(1)
	          ).apply().size > 0
	      if(exists){
	          SQL("""
	              update andparams set k1={k1}, km={km}, n={n}
	              where libraryid={libraryid} and input1={tf1} and input2={tf2}
	              """)
	          .on(
	             'libraryid -> libraryid,
	             'tf1 -> line(0),
	             'tf2 -> line(1),
	             'k1 -> line(2).toDouble,
	             'km -> line(3).toDouble,
	             'n -> line(4).toInt
	          ).executeUpdate()
          }
	      else {
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
    }
  }
  
  def saveNotParams(userid: Int, libraryid: Int, not: Array[String]) = {
    DB.withConnection { implicit connection => 
    	for(i <- 1 to not.length-1){
    	  val line = not(i).split(",")
          val exists = SQL("select * from notparams where libraryid={libraryid} and input={in}")
          .on(
             'libraryid -> libraryid,
             'in -> line(0)
          ).apply().size > 0
          if(exists){
	          SQL("""
	              update notparams set k1={k1}, km={km}, n={n}
	              where libraryid={libraryid} and input={tf}
	              """)
	          .on(
	             'libraryid -> libraryid,
	             'tf -> line(0),
	             'k1 -> line(1).toDouble,
	             'km -> line(2).toDouble,
	             'n -> line(3).toInt
	          ).executeUpdate()
          }
          else {
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
    }
  }
}
