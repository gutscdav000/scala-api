package core.serializer

import java.math.BigInteger

import core.model.User
import java.util.Date

import org.json4s
import org.json4s.{CustomSerializer, JBool, JInt, JString}
import org.json4s._
import org.json4s.jackson.Serialization.write
import JsonDSL._
import java.text.SimpleDateFormat

//object Utils {
//
//  val DATE_FORMAT = "EEE, MMM dd, yyyy h:mm a"
//
//  def getDateAsString(d: Date): String = {
//    val dateFormat = new SimpleDateFormat(DATE_FORMAT)
//    dateFormat.format(d)
//  }
//
//  def convertStringToDate(s: String): Date = {
//    val dateFormat = new SimpleDateFormat(DATE_FORMAT)
//    dateFormat.parse(s)
//  }
//
//}
// new SimpleDateFormat("YYYY-MM-DD").parse(dob)

class UserSerializer extends CustomSerializer[User](format => ( {
    case JObject(
    JField("id", JInt(id)) ::
      JField("username", JString(username)) ::
      JField("email", JString(email)) ::
      JField("passwordHash", JString(passwordHash)) ::
      JField("isActive", JBool(isActive)) ::
      JField("dob", JString(dob)) ::
      Nil
    ) => User(id.intValue, username, email, passwordHash, isActive, new SimpleDateFormat("yyyy-MM-dd").parse(dob))
  }, {
    case x: User =>
      ("id" -> JInt(x.id)) ~
        ("username"-> JString(x.username)) ~
        ("email", JString(x.email)) ~
        ("isActive", JBool(x.isActive)) ~
        ("dob", JString(x.dob.toString))
  }
))

object UserSerializer {
  def apply() = new UserSerializer()
}