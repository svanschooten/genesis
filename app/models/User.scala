package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(inlog: String, password: String, fname: String, lname: String)

object User {
  
  // -- Parsers
  
  /**
   * 
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.inlog") ~
    get[String]("user.password") ~
    get[String]("user.fname") ~
    get[String]("user.lname") map {
      case inlog~password~fname~lname => User(inlog, password, fname, lname)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve an User from inlog.
   */
  def findByInlog(inlog: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where inlog = {inlog}").on(
        'inlog -> inlog
      ).as(User.simple.singleOpt)
    }
  }

  
  /**
   * Authenticate a User.
   */
  def authenticate(inlog: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where 
         inlog = {inlog} and password = {password}
        """
      ).on(
        'inlog -> inlog,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
  
}
