package core.model

import java.util.Date

import doobie.free.connection.ConnectionIO
import doobie.util.fragment.Fragment
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.meta.Meta
import tsec.common.VerificationStatus
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

final case class User(
                       id: Int,
                       username: String,
                       email: String,
                       passwordHash: String,//Meta[PasswordHash[SCrypt]],
                       isActive: Boolean,
                       dob: Date
                     ) {
//  def verifyPassword(password: String): VerificationStatus = {
//    SCrypt.checkpw[cats.Id](password, passwordHash)
//    PasswordHash.subst[SCrypt](implicitly[Meta[String]])
//  }
//  def verifyPassword(password: String) : VerificationStatus = SCrypt.checkpw[cats.Id](password, passwordHash)
}

object UserModel {
  def findById(id: Int): ConnectionIO[Option[User]] = findBy(fr"id = ${id}")
  def findByEmail(email: String): ConnectionIO[Option[User]] = findBy(fr"email = ${email}")
  def findByUsername(username: String): ConnectionIO[Option[User]] = findBy(fr"username = ${username}")

  def insertUser(user: User):ConnectionIO[Int] = {
    sql"""INSERT INTO PUBLIC.USER (USERNAME, EMAIL, PASSWORD_HASH, IS_ACTIVE, DOB)
         | VALUES (${user.username}, ${user.email}, ${user.passwordHash}, ${user.isActive}, ${user.dob})"""
      .stripMargin.update.run
  }

  def updateUser(user: User): ConnectionIO[Int] = {
    sql"""UPDATE PUBLIC.USER
         |SET USERNAME = ${user.username},
         |EMAIL = ${user.email},
         |PASSWORD_HASH = ${user.passwordHash},
         |IS_ACTIVE = ${user.isActive},
         |DOB = ${user.dob}
         |where ID = ${user.id}""".stripMargin.update.run
  }

  private def findBy(by: Fragment): ConnectionIO[Option[User]] =
    (sql"SELECT id, username, email, password_hash, is_active, dob FROM public.user WHERE " ++ by)
      .query[User]
      .option
}
