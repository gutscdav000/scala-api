package core.serializer

import java.text.SimpleDateFormat

import core.model.Debt
import org.json4s.{CustomSerializer, JString, DefaultFormats}
import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._

class DebtSerializer extends CustomSerializer[Debt](implicit format => ( {
    case jsonObj: JObject =>
      val id = (jsonObj \ "id").extract[Int]
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

      Debt(id.toInt, name, userId.toInt, debtType, lender, originalBalance, balance, rate, interestPaid, periodsToPayoff.toInt,
        new SimpleDateFormat("yyyy-MM-dd").parse(payoffDate), maxInterest, minPaymentValue, minPaymentPercent,
        loanTerm.toInt, remainingTerm.toInt, pmi, purchasePrice, maxPeriods.toInt, escrow, maxLoc
      )
    }, {
      case x: Debt =>
        ("id" -> x.id) ~
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