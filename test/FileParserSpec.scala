package test

import org.specs2.mutable._
import models._
import factories._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

class FileParserSpec extends Specification {
  sequential
	  "Library" should {
		"correctly be created" in {
			running(FakeApplication()){
				val simpleCreateLibrary = FileParser.createLibrary(9999, "testLibrary")
				DB.withConnection { implicit connection =>
					val lib = SQL("select libraryname, userid from proteinlibraries where libraryname={testLibrary}")
					.on('testLibrary -> "testLibrary")
					.as {
						get[String]("libraryname")~get[Int]("userid") map{
						case libraryname~userid => (libraryname,userid)
						} * }
					lib.size must equalTo(1)
					for(info <- lib) {
						info._1 must equalTo("testLibrary")
						info._2 must equalTo(9999)
						}
					val deleteLib = SQL(""" DELETE FROM proteinlibraries WHERE userid = {id} """
					).on('id -> 9999).executeUpdate()
					}
				}
			}
		}
		
		"LibraryID" should {
			"correctly be returned" in {
				running(FakeApplication()){
					val getLibraryID = FileParser.getLibraryID(-1, "default")
					getLibraryID must equalTo(0)
				}
			}
		}
		
		"LibraryID" should {
			"correctly be returned (-1) if no correct search" in {
				running(FakeApplication()){
					val getNoneLibrary = FileParser.getLibraryID(-1, "none")
					getNoneLibrary must equalTo(-1)
				}
			}
		}
		
		"NewAndParameters" should {
			"correctly be saved" in {
				running(FakeApplication()){
				val saveParam = FileParser.saveParams(Array("","TESTA,TESTB,1,1,1"), "AND", -1, "default")
					DB.withConnection { implicit connection =>
						val andcds = SQL("select input1,input2,k1,km,n from andparams where input1={in1} and input2={in2}")
						.on('in1 -> "TESTA", 'in2 -> "TESTB")
						.as {
							get[String]("input1")~get[String]("input2")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
							case input1~input2~k1~km~n => (input1,input2,k1,km,n)
							} * }
						andcds.size must equalTo(1)
						for(info <- andcds) {
							info._1 must equalTo("TESTA")
							info._2 must equalTo("TESTB")
							info._3 must equalTo(1)
							info._4 must equalTo(1)
							info._5 must equalTo(1)
							}
					}
				}
			}
		}
		
		"NewAndParameters" should {
			"cannot override old values" in {
				running(FakeApplication()){
				val saveParam = FileParser.saveParams(Array("","TESTA,TESTB,2,2,2"), "AND", -1, "default")
					DB.withConnection { implicit connection =>
						val andcds = SQL("select input1,input2,k1,km,n from andparams where input1={in1} and input2={in2}")
						.on('in1 -> "TESTA", 'in2 -> "TESTB")
						.as {
							get[String]("input1")~get[String]("input2")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
							case input1~input2~k1~km~n => (input1,input2,k1,km,n)
							} * }
						andcds.size must equalTo(1)
						for(info <- andcds) {
							info._1 must equalTo("TESTA")
							info._2 must equalTo("TESTB")
							info._3 must equalTo(1)
							info._4 must equalTo(1)
							info._5 must equalTo(1)
							}
						val deleteLib = SQL(" DELETE FROM andparams WHERE input1={in1} and input2={in2}")
						.on('in1 -> "TESTA", 'in2 -> "TESTB").executeUpdate()
					}
				}
			}
		}
		
		"NewNotParameters" should {
			"correctly be saved" in {
				running(FakeApplication()){
				val saveParam = FileParser.saveParams(Array("","TEST,1,1,1"), "NOT", -1, "default")
					DB.withConnection { implicit connection =>
						val notcds = SQL("select input,k1,km,n from notparams where input={in}")
						.on('in -> "TEST")
						.as {
							get[String]("input")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
							case input~k1~km~n => (input,k1,km,n)
							} * }
						notcds.size must equalTo(1)
						for(info <- notcds) {
							info._1 must equalTo("TEST")
							info._2 must equalTo(1)
							info._3 must equalTo(1)
							info._4 must equalTo(1)
							}
					}
				}
			}
		}
		
		"NewNotParameters" should {
			"cannot override old values" in {
				running(FakeApplication()){
				val saveParam = FileParser.saveParams(Array("","TEST,2,2,2"), "NOT", -1, "default")
					DB.withConnection { implicit connection =>
						val notcds = SQL("select input,k1,km,n from notparams where input={in}")
						.on('in -> "TEST")
						.as {
							get[String]("input")~get[Double]("k1")~get[Double]("km")~get[Int]("n") map{
							case input~k1~km~n => (input,k1,km,n)
							} * }
						notcds.size must equalTo(1)
						for(info <- notcds) {
							info._1 must equalTo("TEST")
							info._2 must equalTo(1)
							info._3 must equalTo(1)
							info._4 must equalTo(1)
							}
						val deleteLib = SQL(" DELETE FROM notparams WHERE input={in} ")
						.on('in -> "TEST").executeUpdate()
					}
				}
			}
		}
		
		"NewCDSParameters" should {
			"correctly be saved" in {
				running(FakeApplication()){
				val saveParam = FileParser.saveParams(Array("","TEST,1,1,1"), "CDS", -1, "default")
					DB.withConnection { implicit connection =>
						val cds = SQL("select name,k2,d1,d2 from cdsparams where name={name}")
						.on('name -> "TEST")
						.as {
							get[String]("name")~get[Double]("k2")~get[Double]("d1")~get[Double]("d2") map{
							case name~k2~d1~d2 => (name,k2,d1,d2)
							} * }
						cds.size must equalTo(1)
						for(info <- cds) {
							info._1 must equalTo("TEST")
							info._2 must equalTo(1)
							info._3 must equalTo(1)
							info._4 must equalTo(1)
							}
					}
				}
			}
		}
		
		"NewCDSParameters" should {
			"cannot override old values" in {
				running(FakeApplication()){
				val saveParam = FileParser.saveParams(Array("","TEST,2,2,2"), "CDS", -1, "default")
					DB.withConnection { implicit connection =>
						val cds = SQL("select name,k2,d1,d2 from cdsparams where name={name}")
						.on('name -> "TEST")
						.as {
							get[String]("name")~get[Double]("k2")~get[Double]("d1")~get[Double]("d2") map{
							case name~k2~d1~d2 => (name,k2,d1,d2)
							} * }
						cds.size must equalTo(1)
						for(info <- cds) {
							info._1 must equalTo("TEST")
							info._2 must equalTo(1)
							info._3 must equalTo(1)
							info._4 must equalTo(1)
							}
						val deleteLib = SQL(" DELETE FROM cdsparams WHERE name={name}")
						.on('name-> "TEST").executeUpdate()
					}
				}
			}
		}
	}