package core.serializer

import core.model.Debt
import org.json4s.{CustomSerializer, JString}
import org.json4s._
import JsonDSL._
import java.text.SimpleDateFormat

import org.json4s

class DebtSerializer extends CustomSerializer[Debt](format => ( {
    case JObject(
      JField("name", JString(name)) ::
      JField("user_id", JInt(userId)) ::
      JField("debtType", JString(debtType)) ::
      JField("lender", JString(lender)) ::
      JField("originalBalance", JDouble(originalBalance)) ::
      JField("balance", JDouble(balance)) ::
      JField("rate", JDouble(rate)) ::
      JField("interestPaid", JDouble(interestPaid)) ::
      JField("periodsToPayoff", JInt(periodsToPayoff)) ::
      JField("payoffDate", JString(payoffDate)) ::
      JField("maxInterest", JDouble(maxInterest)) ::
      JField("minPaymentValue", JDouble(minPaymentValue)) ::
      JField("minPaymentPercent", JDouble(minPaymentPercent)) ::
      JField("loanTerm", JInt(loanTerm)) ::
      JField("remainingTerm", JInt(remainingTerm)) ::
      JField("pmi", JDouble(pmi)) ::
      JField("purchasePrice", JDouble(purchasePrice)) ::
      JField("maxPeriods", JInt(maxPeriods)) ::
        JField("escrow", JDouble(escrow)) ::
        JField("maxLoc", JDouble(maxLoc)) ::
      Nil
    ) => Debt( name, userId.toInt, debtType, lender, originalBalance, balance, rate, interestPaid, periodsToPayoff.toInt,
        new SimpleDateFormat("yyyy-MM-dd").parse(payoffDate), maxInterest, minPaymentPercent, minPaymentPercent,
        loanTerm.toInt, remainingTerm.toInt, pmi, purchasePrice, maxPeriods.toInt, escrow, maxLoc
      )
    }, {
      case x: Debt =>
          ("name" -> JString(x.name)) ~
            ("user_id"-> JInt(x.userId)) ~
            ("debtType"-> JString(x.debtType)) ~
            ("lender"-> JString(x.lender)) ~
            ("originalBalance"-> JDouble(x.originalBalance)) ~
            ("balance"-> JDouble(x.balance)) ~
            ("rate"-> JDouble(x.rate)) ~
            ("interestPaid"-> JDouble(x.interestPaid)) ~
            ("periodsToPayoff"-> JInt(x.periodsToPayoff)) ~
            ("payoffDate"-> JString(x.payoffDate.toString)) ~
            ("maxInterest"-> JDouble(x.maxInterest)) ~
            ("minPaymentValue"-> JDouble(x.minPaymentValue)) ~
            ("minPaymentPercent"-> JDouble(x.minPaymentPercent)) ~
            ("loanTerm"-> JInt(x.loanTerm)) ~
            ("remainingLoanTerm"-> JInt(x.remainingTerm)) ~
            ("pmi"-> JDouble(x.pmi)) ~
            ("purchasePrice"-> JDouble(x.purchasePrice)) ~
            ("maxPeriods"-> JInt(x.maxPeriods)) ~
            ("escrow"-> JDouble(x.escrow)) ~
            ("maxLoc"-> JDouble(x.maxLoc))
    }
  ))

object DebtSerializer {
  def apply() = new DebtSerializer()
}
