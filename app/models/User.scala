package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(email: String, password: String, fname: Option[String], lname: Option[String])

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
      case email~password~fname~lname => User(email, password, fname, lname)
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
  
  def insert(email: String, password: String, fname: Option[String], lname: Option[String]) = {
    DB.withConnection{ implicit connection =>
      SQL(
        """
        INSERT INTO User
        VALUES ({email}, {password}, {fname}, {lname})
        """
      ).on(
        'email -> email,
        'password -> password,
        'fname -> fname,
        'lname -> lname
      )
    }
  }
    
  def update(id: Int, email: String,	 password: String, fname: Option[String], lname: Option[String]) = {
    DB.withConnection{ implicit connection =>
      SQL(
        """
        UPDATE User
        SET 
          email={email},
          password={password},
          fname={fname},
          lname={lname}
        WHERE
          id = {id}
        """
      ).on(
        'email -> email,
        'password -> password,
        'fname -> fname,
        'lname -> lname,
        'id -> id
      )
    }
  }
  
}
