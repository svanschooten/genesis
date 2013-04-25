package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

//case class User(email: String, password: String, fname: Option[String], lname: Option[String])

class User(email: String, password: String, fname: Option[String], lname: Option[String]) {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.password") ~
    get[Option[String]]("user.fname") ~
    get[Option[String]]("user.lname") map {
      case email~password~fname~lname => new User(email, password, fname, lname)
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
      ).as(simple.singleOpt)
    }
  }
  
  def save(email: String, password: String, fname: Option[String], lname: Option[String]) = {
    DB.withConnection{ implicit connection =>
      SQL(
        """
        INSERT INTO User
        VALUES ({email}, {password}, {fname}, {lname})
        """
      ).on(
        'email -> this.email,
        'password -> password,
        'fname -> fname,
        'lname -> lname
      )
    }
  }
  
}
