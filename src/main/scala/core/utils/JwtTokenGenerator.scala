package core.utils

import java.text.SimpleDateFormat
import java.util.Date

//import core.core.fp.Main.jwtGenerator
import core.serializer.JwtObject
import pdi.jwt.{Jwt, JwtAlgorithm}
import scala.util.{Success, Try}
import scala.util.parsing.json.JSON

trait JwtTokenGeneratorServices {
  def generateToken(userId : Int, role: Int) : String
  def verifyToken(token : String) : Try[(String,String,String)]
  def fetchPayload(token : String) : JwtObject
}

class JwtTokenGenerator extends JwtTokenGeneratorServices {

  val secret = "thsk38475hgreu2374ht"

  override def generateToken(userId : Int, roleId: Int): String = {
    val addMinuteTime = 1
    val expiry: Date = addMinutesToDate(addMinuteTime)
    val formattedExpiry: String = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(expiry)
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
    // TODO: don't think I'll need this in app. just forcing me to use Bearer.
    val strippedToken = token.substring(21).replaceAll("\\s", "")
    val payload: Map[String, String] = verifyToken(strippedToken) match {
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
