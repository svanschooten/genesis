package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(email: String, password: String, fname: String = null, lname: String = null)

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
      case email~password~fname~lname => User(email, password, fname, lname)
      case email~password => User(email, password)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from inlog.
   */
  def findByInlog(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}")
        .on('email -> email)
        .as(simple.singleOpt)
    }
  }

  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where 
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
  
}
