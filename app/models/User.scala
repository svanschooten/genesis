package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id: Int = -1, email: String, password: String, fname: Option[String], lname: Option[String])

object User {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[Int]("user.id") ~
    get[String]("user.email") ~
    get[String]("user.password") ~
    get[Option[String]]("user.fname") ~
    get[Option[String]]("user.lname") map {
      case id~email~password~fname~lname => User(id, email, password, fname, lname)
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
         SELECT *
         FROM User
         WHERE email = {email} 
          AND password = {password}
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
  
  /** Updates a user's email, provided the user's id */
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
  
  /** Updates a User's first name, provided the user's id */
  def updateFirstName(id: Int, fname: String) = {
    DB.withConnection{ implicit connection => 
      SQL(
        """
          UPDATE User
          SET fname={fname}
          WHERE id = {id}
        """
      ).on(
        'fname -> fname,
        'id -> id
      ).executeUpdate
    }
  }
  
  /** Updates a User's last name, provided the user's id */
  def updateLastName(id: Int, lname: String) = {
    DB.withConnection{ implicit connection => 
      SQL(
        """
          UPDATE User
          SET lname={lname}
          WHERE id = {id}
        """
      ).on(
        'lname -> lname,
        'id -> id
      ).executeUpdate
    }
  }
}
