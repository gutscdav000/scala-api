package core.serializer

import java.text.SimpleDateFormat

import core.model.{Action, Debt}
import org.json4s.{CustomSerializer, DefaultFormats, JString}
import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._

class ActionSerializer extends CustomSerializer[Action](implicit format => ( {
  case jsonObj: JObject =>
    val id = (jsonObj \ "id").extract[Int]
    val debtId = (jsonObj \ "debtId").extract[Int]
    val userId = (jsonObj \ "userId").extract[Int]
    val principal = (jsonObj \ "principal").extract[Double]
    val interest = (jsonObj \ "interest").extract[Double]
    val payDate = (jsonObj \ "payDate").extract[String]

    Action(id.toInt,
           debtId.toInt,
           userId.toInt,
           principal,
           interest,
           new SimpleDateFormat("yyyy-MM-dd").parse(payDate)
    )
}, {
  case x: Action =>
    ("id" -> x.id) ~
    ("debtId" -> x.debtId) ~
    ("userId" -> x.userId) ~
    ("principal" -> x.principal) ~
    ("interest" -> x.interest) ~
    ("payDate" -> x.payDate.toString)
}))

object ActionSerializer {
  def apply() = new ActionSerializer()
}
