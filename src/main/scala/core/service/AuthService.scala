package core.service

import java.text.SimpleDateFormat
import java.util.Date

import scala.util.parsing.json._
import cats.effect.IO
import core.core.fp.Main.jwtGenerator
import core.model.UserModel
import core.serializer.JwtObject
import core.utils.JwtTokenGenerator
import doobie.util.transactor.Transactor
import org.http4s.{Header, Response}
import org.http4s.Uri.UserInfo
import org.http4s.dsl.io._
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.util.{Success, Try}




object AuthService {
  val jwtGenerator = new JwtTokenGenerator()

  def login(userI: UserInfo, transactor: Transactor[IO]): IO[Response[IO]] = {
    UserModel.findByEmail(userI.username, transactor) match {
      case Left(err) => Forbidden(s"something went wrong ${err}")
      case Right(user) => user.passwordHash.equals(userI.password.get) match {
          case true => {
            val token = jwtGenerator.generateToken(user.id, 1)
            Ok("logged in", Header("Authorization", token))
          }
          case false => Forbidden("Invalid username password combination.")
        }
      }
    }
}
