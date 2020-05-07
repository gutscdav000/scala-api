package core.model

import java.util.Date
import doobie.free.connection.ConnectionIO
import doobie.util.fragment.Fragment
import doobie.implicits._
import doobie.postgres.implicits._

final case class User(
                       id: Int,
                       username: String,
                       email: String,
                       passwordHash: String,
                       isActive: Boolean,
                       dob: Date
                     ) {
//  def verifyPassword(password: String) : VerificationStatus = SCrypt.checkpw[cats.Id](password, passwordHash)
}

object UserModel {
  def findById(id: Int): ConnectionIO[Option[User]] = findBy(fr"id = ${id}")

  private def findBy(by: Fragment): ConnectionIO[Option[User]] =
    (sql"SELECT id, username, email, password_hash, is_active, dob FROM public.user WHERE " ++ by)
      .query[User]
      .option
}
