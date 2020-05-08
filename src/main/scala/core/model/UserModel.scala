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

  def insertUser(user: User):ConnectionIO[Int] = {
    sql"""INSERT INTO PUBLIC.USER (ID, USERNAME, EMAIL, PASSWORD, IS_ACTIVE, DOB)
         | VALUES (${user.id}, ${user.username}, ${user.email}, ${user.passwordHash}, ${user.isActive}, ${user.dob})"""
      .stripMargin.update.run
  }

  private def findBy(by: Fragment): ConnectionIO[Option[User]] =
    (sql"SELECT id, username, email, password_hash, is_active, dob FROM public.user WHERE " ++ by)
      .query[User]
      .option
}
