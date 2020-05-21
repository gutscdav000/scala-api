package core.model

import java.util.Date
import cats.effect.IO
import doobie.util.fragment.Fragment
import doobie.implicits._
import doobie.util.transactor.Transactor

final case class User(
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

  def findByEmail(email: String, transactor: Transactor[IO]): Either[Throwable, User] = {
    try {
      val user: User = findBy(fr"email = ${email}", transactor).get
      Right(user)
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  def findByUsername(username: String, transactor: Transactor[IO]): Either[Throwable, User] = {
    try {
      val user: User = findBy(fr"username = ${username}", transactor).get
      Right(user)
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  def insertUser(user: User, transactor: Transactor[IO]): Either[Throwable, User] = {
    try {
      sql"""INSERT INTO PUBLIC.USER (USERNAME, EMAIL, PASSWORD_HASH, IS_ACTIVE, DOB)
           | VALUES (${user.username}, ${user.email}, ${user.passwordHash}, ${user.isActive}, ${user.dob})"""
        .stripMargin.update.run
        .transact(transactor)
        .unsafeRunSync()
      Right(user)
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  def updateUser(user: User, transactor: Transactor[IO]): Either[Throwable, User] = {
    try {
      sql"""UPDATE PUBLIC.USER
           |SET EMAIL = ${user.email},
           |PASSWORD_HASH = ${user.passwordHash},
           |IS_ACTIVE = ${user.isActive},
           |DOB = ${user.dob}
           |where USERNAME = ${user.username}"""
        .stripMargin
        .update
        .run
        .transact(transactor)
        .unsafeRunSync
      Right(user)
      } catch {
      case exception: Throwable => Left(exception)
    }
  }

  def deleteUser(user: User, transactor: Transactor[IO]): Either[Throwable, User] = {
    try {
      findBy(fr"USERNAME = ${user.username} AND EMAIL = ${user.email}", transactor) match {
        case Some(u) => u
        case None => throw new IllegalArgumentException("record not found")
      }

      sql"""DELETE FROM PUBLIC.USER
           |WHERE USERNAME = ${user.username} AND EMAIL =  ${user.email}"""
        .stripMargin
        .update
        .run
        .transact(transactor)
        .unsafeRunSync

      Right(user)
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  private def findBy(by: Fragment, transactor: Transactor[IO]): Option[User] =
    (sql"SELECT username, email, password_hash, is_active, dob FROM public.user WHERE " ++ by)
      .query[User]
      .option
      .transact(transactor)
      .unsafeRunSync()
}
