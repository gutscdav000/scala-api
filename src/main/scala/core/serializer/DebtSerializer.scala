package core.serializer

import core.model.Debt
import org.json4s.{CustomSerializer, JString, DefaultFormats}
import org.json4s._
import JsonDSL._
import java.text.SimpleDateFormat

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.Formats
import org.json4s


class DebtSerializer extends CustomSerializer[Debt](implicit format => ( {
    case jsonObj: JObject =>
      val name = (jsonObj \ "name").extract[String]
      val userId = (jsonObj \ "userId").extract[Int]
      val debtType = (jsonObj \ "debtType").extract[String]
      val lender = (jsonObj \ "lender").extract[String]
      val originalBalance = (jsonObj \ "originalBalance").extract[Double]
      val balance = (jsonObj \ "balance").extract[Double]
      val rate = (jsonObj \ "rate").extract[Double]
      val interestPaid = (jsonObj \ "interestPaid").extract[Double]
      val periodsToPayoff = (jsonObj \ "periodsToPayoff").extract[Int]
      val payoffDate = (jsonObj \ "payoffDate").extract[String]
      val maxInterest = (jsonObj \ "maxInterest").extract[Double]
      val minPaymentValue = (jsonObj \ "minPaymentValue").extract[Double]
      val minPaymentPercent = (jsonObj \ "minPaymentPercent").extract[Double]
      val loanTerm = (jsonObj \ "loanTerm").extract[Int]
      val remainingTerm = (jsonObj \ "remainingTerm").extract[Int]
      val pmi = (jsonObj \ "pmi").extract[Double]
      val purchasePrice = (jsonObj \ "purchasePrice").extract[Double]
      val maxPeriods = (jsonObj \ "maxPeriods").extract[Int]
      val escrow = (jsonObj \ "escrow").extract[Double]
      val maxLoc = (jsonObj \ "maxLoc").extract[Double]

      Debt( name, userId.toInt, debtType, lender, originalBalance, balance, rate, interestPaid, periodsToPayoff.toInt,
        new SimpleDateFormat("yyyy-MM-dd").parse(payoffDate), maxInterest, minPaymentValue, minPaymentPercent,
        loanTerm.toInt, remainingTerm.toInt, pmi, purchasePrice, maxPeriods.toInt, escrow, maxLoc
      )
    }, {
      case x: Debt =>
          ("name" -> x.name) ~
            ("userId"-> x.userId) ~
            ("debtType"-> x.debtType) ~
            ("lender"-> x.lender) ~
            ("originalBalance"-> x.originalBalance) ~
            ("balance"-> x.balance) ~
            ("rate"-> x.rate) ~
            ("interestPaid"-> x.interestPaid) ~
            ("periodsToPayoff"-> x.periodsToPayoff) ~
            ("payoffDate"-> x.payoffDate.toString) ~
            ("maxInterest"-> x.maxInterest) ~
            ("minPaymentValue"-> x.minPaymentValue) ~
            ("minPaymentPercent"-> x.minPaymentPercent) ~
            ("loanTerm"-> x.loanTerm) ~
            ("remainingTerm"-> x.remainingTerm) ~
            ("pmi"-> x.pmi) ~
            ("purchasePrice"-> x.purchasePrice) ~
            ("maxPeriods"-> x.maxPeriods) ~
            ("escrow"-> x.escrow) ~
            ("maxLoc"-> x.maxLoc)
    }
  ))

object DebtSerializer {
  def apply() = new DebtSerializer()
}

//case x: Debt =>
//("name" -> JString(x.name)) ~
//("userId"-> JInt(x.userId)) ~
//("debtType"-> JString(x.debtType)) ~
//("lender"-> JString(x.lender)) ~
//("originalBalance"-> JDouble(x.originalBalance)) ~
//("balance"-> JDouble(x.balance)) ~
//("rate"-> JDouble(x.rate)) ~
//("interestPaid"-> JDouble(x.interestPaid)) ~
//("periodsToPayoff"-> JInt(x.periodsToPayoff)) ~
//("payoffDate"-> JString(x.payoffDate.toString)) ~
//("maxInterest"-> JDouble(x.maxInterest)) ~
//("minPaymentValue"-> JDouble(x.minPaymentValue)) ~
//("minPaymentPercent"-> JDouble(x.minPaymentPercent)) ~
//("loanTerm"-> JInt(x.loanTerm)) ~
//("remainingTerm"-> JInt(x.remainingTerm)) ~
//("pmi"-> JDouble(x.pmi)) ~
//("purchasePrice"-> JDouble(x.purchasePrice)) ~
//("maxPeriods"-> JInt(x.maxPeriods)) ~
//("escrow"-> JDouble(x.escrow)) ~
//("maxLoc"-> JDouble(x.maxLoc))
//}
//
//{
//  case JObject(
//  JField("name", JString(name)) ::
//  JField("user_id", JInt(userId)) ::
//  JField("debtType", JString(debtType)) ::
//  JField("lender", JString(lender)) ::
//  JField("originalBalance", JDouble(originalBalance)) ::
//  JField("balance", JDouble(balance)) ::
//  JField("rate", JDouble(rate)) ::
//  JField("interestPaid", JDouble(interestPaid)) ::
//  JField("periodsToPayoff", JInt(periodsToPayoff)) ::
//  JField("payoffDate", JString(payoffDate)) ::
//  JField("maxInterest", JDouble(maxInterest)) ::
//  JField("minPaymentValue", JDouble(minPaymentValue)) ::
//  JField("minPaymentPercent", JDouble(minPaymentPercent)) ::
//  JField("loanTerm", JInt(loanTerm)) ::
//  JField("remainingTerm", JInt(remainingTerm)) ::
//  JField("pmi", JDouble(pmi)) ::
//  JField("purchasePrice", JDouble(purchasePrice)) ::
//  JField("maxPeriods", JInt(maxPeriods)) ::
//  JField("escrow", JDouble(escrow)) ::
//  JField("maxLoc", JDouble(maxLoc)) ::
//  Nil
//  ) => Debt( name, userId.toInt, debtType, lender, originalBalance, balance, rate, interestPaid, periodsToPayoff.toInt,
//  new SimpleDateFormat("yyyy-MM-dd").parse(payoffDate), maxInterest, minPaymentPercent, minPaymentPercent,
//  loanTerm.toInt, remainingTerm.toInt, pmi, purchasePrice, maxPeriods.toInt, escrow, maxLoc
//  )
//}