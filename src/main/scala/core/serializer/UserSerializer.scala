package core.serializer

import core.model.User

import org.json4s.{CustomSerializer, JBool, JString}
import org.json4s._
import JsonDSL._
import java.text.SimpleDateFormat

class UserSerializer extends CustomSerializer[User](format => ( {
    case JObject(
      JField("username", JString(username)) ::
      JField("email", JString(email)) ::
      JField("passwordHash", JString(passwordHash)) ::
      JField("isActive", JBool(isActive)) ::
      JField("dob", JString(dob)) ::
      Nil
    ) => User( username, email, passwordHash, isActive, new SimpleDateFormat("yyyy-MM-dd").parse(dob))
  }, {
    case x: User =>
        ("username"-> JString(x.username)) ~
        ("email", JString(x.email)) ~
        ("isActive", JBool(x.isActive)) ~
        ("dob", JString(x.dob.toString))
  }
))

object UserSerializer {
  def apply() = new UserSerializer()
}