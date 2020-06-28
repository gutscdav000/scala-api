package core.serializer

import java.text.SimpleDateFormat
import java.util.Date

import core.model.Debt
import org.json4s.JsonAST.JObject
import org.json4s.{CustomSerializer, DefaultFormats, JString}
import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._

final case class JwtObject(userId: Int, roleId: Int, expiry: Date)

class JwtSerializer extends CustomSerializer[JwtObject](implicit format => ( {
  case jsonObj: JObject =>
    val userId = (jsonObj \ "userId").extract[Int]
    val roleId = (jsonObj \ "roleId").extract[Int]
    val expiry = (jsonObj \ "expiry").extract[String]
    JwtObject(userId, roleId, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").parse(expiry))
}, {
  case x: JwtObject =>
    ("id" -> x.userId) ~
      ("name" -> x.roleId) ~
      ("userId"-> x.expiry.toString)
}
))

object JwtSerializer {
  def apply(): JwtSerializer = new JwtSerializer()
}