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

  
  /** Authenticates a User. */
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
  
  /** Creates a new User */
  def create(email: String, password: String, fname: Option[String], lname: Option[String]) = {
    DB.withConnection{ implicit connection =>
      SQL(
        """
        INSERT INTO User( email, password, fname, lname )
        VALUES ({email}, {password}, {fname}, {lname})
        """
      ).on(
        'email -> email,
        'password -> password,
        'fname -> fname,
        'lname -> lname
      ).executeUpdate
    }
  }
  
  /** Updates user's email */
  def updateEmail(id: Int, email: String) = {
    DB.withConnection{ implicit connection =>
      SQL(
        """
        UPDATE User
        SET email={email}
        WHERE id = {id}
        """
      ).on(
        'id -> id,
        'email -> email
      ).executeUpdate
    }
  }
  
  /** Update the password, given that the user can insert the old password. */
  def updatePassword(id: Int, oldPass: String, newPass: String) = {
    DB.withConnection{ implicit connection =>
      SQL(
         """
           SELECT password
           FROM User
           WHERE id = {id} AND password = {oldPass}
         """
         ).on(
           'id -> id,    
           'password -> oldPass
         ).as(scalar[String].singleOpt) 
         
      match {
        case None => None
        case Some(_) => 
          SQL(
              """
                UPDATE User
                SET password = {newPass}
                WHERE id = {id}
              """
              ).on(
                'newPass -> newPass,
                'id -> id
              ).executeUpdate
      } 
    }
  }
  
}
