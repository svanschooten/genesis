package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(inlog: String, password: String, fname: String, lname: String)

object User {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.password") ~
    get[String]("user.fname") ~
    get[String]("user.lname") map {
      case inlog~password~fname~lname => User(inlog, password, fname, lname)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from inlog.
   */
  def findByInlog(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email
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
         email = {email} and password = {password}
        """
      ).on(
        'email -> inlog,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
  
}
