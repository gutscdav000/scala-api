package core.service

import cats.effect.IO
import core.model.{User, UserModel}
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.http4s.Uri.UserInfo
import org.http4s.dsl.io.{_}
//
import tsec.authentication._
import tsec.common.SecureRandomId
import tsec.mac.jca.{HMACSHA256, MacSigningKey}
import scala.concurrent.duration._

object AuthService {
  def login(userI: UserInfo, transactor: Transactor[IO]):IO[Response[IO]] = {
    UserModel.findByEmail(userI.username, transactor) match {
      case Right(user) => Ok("nice")
      case Left(err) => Forbidden(s"Username or Password incorrect ${err}")
    }
  }
}
