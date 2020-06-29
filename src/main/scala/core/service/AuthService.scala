package core.service

import java.text.SimpleDateFormat
import java.util.Date

import cats.Applicative
import com.fasterxml.jackson.databind.ObjectMapper
import scala.util.parsing.json._
import org.json4s.Reader
//import cats.data.{Kleisli, Reader}
import cats.Id
import cats.effect.IO
import core.core.fp.Main.jwtGenerator
import core.model.UserModel
import core.serializer.{JwtObject, JwtSerializer}
import doobie.util.transactor.Transactor
import io.circe.Json
import org.http4s.{Header, Request, Response, ResponseCookie}
import org.http4s.Uri.UserInfo
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.json4s.jackson.jsonOf
import org.json4s.{DefaultFormats, JValue}
import pdi.jwt.JwtClaim
//
import java.io.File
import com.typesafe.config.ConfigFactory
import pdi.jwt.{Jwt, JwtAlgorithm}
import scala.util.{Success, Failure, Try}


trait JwtTokenGeneratorServices {
  def generateToken(userId : Int, role: Int) : String
  def verifyToken(token : String) : Try[(String,String,String)]
  def fetchPayload(token : String) : JwtObject
}

class JwtTokenGenerator extends JwtTokenGeneratorServices {
  implicit val formats = DefaultFormats
  implicit val jwtObjectReader = new Reader[JwtObject] {
    def read(value: JValue): JwtObject = value.extract[JwtObject]
  }
  implicit val actionDec = jsonOf[IO, JwtObject]

//  val conf = ConfigFactory.parseFile(new File("conf/application.conf"))
//  val secret = ConfigFactory.load(conf).getString("play.crypto.secret")
  val secret = "thsk38475hgreu2374ht"

  override def generateToken(userId : Int, roleId: Int): String = {
    val addMinuteTime = 1
    val expiry: Date = addMinutesToDate(addMinuteTime)
    val formattedExpiry: String = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(expiry)
    println("------------------------")
//    println(expiry.toInstant.toString)
    println(formattedExpiry)
    println("------------------------")
    Jwt.encode(s"""{"userId":"${userId.toString}","expiry":"${formattedExpiry}"}""", secret, JwtAlgorithm.HS256)
  }

  override def verifyToken(token : String): Try[(String,String,String)] = Jwt.decodeRawAll(token, secret, Seq(JwtAlgorithm.HS256))

  override def fetchPayload(token: String): JwtObject = {
    val jwt = Jwt.decode(token, secret, Seq(JwtAlgorithm.HS256))
    jwt match {
      case Success(x) => x.asInstanceOf[JwtObject]
    }
  }

  def tokenIsValid(token: String): Boolean = {
    // TODO: don't think I'll need this in app. just postman giving me hell
    val strippedToken = token.substring(21).replaceAll("\\s", "")
    val payload: Map[String, String] = jwtGenerator.verifyToken(strippedToken) match {
      case Success(value) => {
        JSON.parseFull(value._2)
          match {
            case Some(res: Map[String, String]) => res
            case None => Map()
          }
      }
    }

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    !payload.isEmpty && dateFormat.parse(payload.get("expiry").get).after(new Date())
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
      }
    }
  }
}
