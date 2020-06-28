package core.service

import java.util.Date

import cats.Applicative
import cats.data.Kleisli
import cats.Id
import cats.effect.IO
import core.core.fp.Main.jwtGenerator
import core.model.{User, UserModel}
import core.serializer.{JwtObject, JwtSerializer}
import doobie.util.transactor.Transactor
import org.http4s.{Header, Request, Response, ResponseCookie}
import org.http4s.Uri.UserInfo
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import pdi.jwt.JwtClaim
//
//import org.reactormonk.{CryptoBits, PrivateKey}
//import tsec.authentication._
//import tsec.common.SecureRandomId
//import tsec.mac.jca.{HMACSHA256, MacSigningKey}
//import scala.concurrent.duration._
import java.io.File
import com.typesafe.config.ConfigFactory
import pdi.jwt.{Jwt, JwtAlgorithm}
//import play.libs.Json
import scala.util.{Success, Failure, Try}


trait JwtTokenGeneratorServices {
  def generateToken(userId : Int, role: Int) : String
  def verifyToken(token : String) : Try[(String,String,String)]
  def fetchPayload(token : String) : JwtObject
}

class JwtTokenGenerator extends JwtTokenGeneratorServices {

//  val conf = ConfigFactory.parseFile(new File("conf/application.conf"))
//  val secret = ConfigFactory.load(conf).getString("play.crypto.secret")
  val secret = "thsk38475hgreu2374ht"

  override def generateToken(userId : Int, roleId: Int): String = {
    val addMinuteTime = 5
    val expiry = addMinutesToDate(addMinuteTime)
    Jwt.encode(s"""{ "userId" : ${userId.toString}, "roleId": ${roleId.toString} "expiry": $expiry }""", secret, JwtAlgorithm.HS256)
  }

  override def verifyToken(token : String): Try[(String,String,String)] = Jwt.decodeRawAll(token, secret, Seq(JwtAlgorithm.HS256))

  override def fetchPayload(token: String): JwtObject = {
    val jwt = Jwt.decode(token, secret, Seq(JwtAlgorithm.HS256))
    jwt match {
      case Success(x) => x.asInstanceOf[JwtObject]
    }
  }

  def tokenIsValid(token: String): Boolean = {
    println(token)
    // TODO: don't think I'll need this in app. just postman giving me hell
    val strippedToken = token.substring(21)//.replaceAll("\\s", "")
    val payload: JwtObject = jwtGenerator.verifyToken(strippedToken) match {
      case Success(value) => value.asInstanceOf[JwtObject]
      case Failure(exception) => null
    }
    payload != null && payload.expiry.after(new Date())
  }

  private def addMinutesToDate(minutes: Int): Date = {
    val ONE_MINUTE_IN_MILLIS = 60000 // millis
    val curTimeInMs = new Date().getTime();
    new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS))
  }
}

object AuthService {
  val jwtGenerator = new JwtTokenGenerator()

  def login(userI: UserInfo, transactor: Transactor[IO]): IO[Response[IO]] = {
    UserModel.findByEmail(userI.username, transactor) match {
      case Left(err) => Forbidden(s"something went wrong ${err}")
      case Right(user) => {
        val token = jwtGenerator.generateToken(user.id, 1)
        Ok("logged in", Header("Authorization", token))
//        Ok("logged in", Header("X-Auth-Token", token))
      }
    }
  }
}
