package core.service

import cats.Applicative
import cats.data.Kleisli
import cats.effect.IO
import core.model.{User, UserModel}
import doobie.util.transactor.Transactor
import org.http4s.{Request, Response, ResponseCookie}
import org.http4s.Uri.UserInfo
import org.http4s.dsl.io._
//
import org.reactormonk.{CryptoBits, PrivateKey}
import tsec.authentication._
import tsec.common.SecureRandomId
import tsec.mac.jca.{HMACSHA256, MacSigningKey}
import scala.concurrent.duration._

object AuthService {

  val key = PrivateKey(scala.io.Codec.toUTF8(scala.util.Random.alphanumeric.take(20).mkString("")))
  val crypto = CryptoBits(key)
  val clock = java.time.Clock.systemUTC

  def login(userI: UserInfo, transactor: Transactor[IO]):IO[Response[IO]] = {
    UserModel.findByEmail(userI.username, transactor) match {
      case Right(user) => Ok("nice")
      case Left(err) => Forbidden(s"Username or Password incorrect ${err}")
    }
  }

  def login2(userI: UserInfo, transactor: Transactor[IO]): IO[Response[IO]] = {
    UserModel.findByEmail(userI.username, transactor) match {
      case Left(err) => Forbidden(s"something went wrong ${err}")
      case Right(user) => {
        val message = crypto.signToken(user.id.toString, clock.millis.toString)
        Ok("logged in yeet!").map(_.addCookie(ResponseCookie("authcookie", message)))
      }
    }
  }
}
