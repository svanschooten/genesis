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
    get[Option[String]]("user.fname") ~
    get[Option[String]]("user.lname") map {
      case email~password~Some(_fname: String)~Some(_lname: String) => User(email, password, _fname, _lname)
      case email~password~Some(_fname: String)~None => User(email, password, fname=_fname)
      case email~password~None~Some(_lname: String) => User(email, password, lname=_lname)
      case email~password~None~None => User(email, password)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from the user database.
   */
  def findByInlog(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}")
        .on('email -> email)
        .as(User.simple.singleOpt)
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
