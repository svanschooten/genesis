package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import com.github.t3hnar.bcrypt._

case class User(id: Int = -1, email: String, password: String, fname: Option[String], lname: Option[String])

object User {
  
  // -- Parsers
  
  /** Parse a User from a ResultSet */
  val userParser = {
    get[Long]("user.id") ~
    get[String]("user.email") ~
    get[String]("user.password") ~
    get[Option[String]]("user.fname") ~
    get[Option[String]]("user.lname") map {
      case id~email~password~fname~lname => User(id.toInt, email, password, fname, lname)
    }
  }

  /** Parse a hashed password from a ResultSet */
  val hashedPasswordParser = {
    get[String]("user.password") map {
      case password => password
    }
  }
  
  // -- Queries
  
  /** Retrieve a User from email. */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from \"User\" where email = {email}")
        .on('email -> email)
        .as(User.userParser.singleOpt)
    }
  }

  
  /** Authenticates a user given an email and password. */
  def authenticate(email: String, password: String): Option[User] = {
    findByEmail(email) match {
      case Some(User(id,email,hashedPw,fname,lname)) => 
        if (password.isBcrypted(hashedPw)) Some(User(id, email, hashedPw, fname, lname))
        else None
      case _ => None
    }
  }
  
  /** Creates a new User */
  def create(email: String, password: String, fname: Option[String], lname: Option[String]) = {
    val hashedPassword = password.bcrypt(12)
    DB.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO "User"( email, password, fname, lname )
        VALUES ({email}, {password}, {fname}, {lname})
        """
      ).on(
        'email -> email,
        'password -> hashedPassword,
        'fname -> fname,
        'lname -> lname
      ).executeUpdate
    }
  }
}
