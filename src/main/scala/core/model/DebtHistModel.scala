package core.model

import java.time.LocalDateTime
import java.util.{ Date}
import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._

final case class DebtHist(var id: Int,
                             name: String,
                             userId: Int,
                             debtType: String,
                             lender: String,
                             originalBalance: Double,
                             balance: Double,
                             rate: Double,
                             interestPaid: Double,
                             periodsToPayoff: Int,
                             payoffDate: Date,
                             maxInterest: Double,
                             minPaymentValue: Double,
                             minPaymentPercent: Double,
                             loanTerm: Int,
                             remainingTerm: Int,
                             pmi: Double,
                             purchasePrice: Double,
                             maxPeriods: Int,
                             escrow: Double,
                             maxLoc: Double,
                          updateStamp: LocalDateTime
                         )

object DebtHistModel {

  def insertDebtHist(debt: Debt, transactor: Transactor[IO]): Either[Throwable, DebtHist] = {
    try {
      val stamp: LocalDateTime = LocalDateTime.now()
      sql"""INSERT INTO PUBLIC.DEBT_HIST (ID, NAME, USER_ID, DEBT_TYPE, LENDER, ORIGINAL_BALANCE, BALANCE, RATE,
                                     INTEREST_PAID, PERIODS_TO_PAYOFF, PAYOFF_DATE, MAX_INTEREST, MIN_PAYMENT_VALUE,
                                     MIN_PAYMENT_PERCENT, LOAN_TERM, REMAINING_TERM, PMI, PURCHASE_PRICE, MAX_PERIODS,
                                     ESCROW, MAX_LOC, UPDATE_STAMP)
           VALUES (${debt.id}, ${debt.name}, ${debt.userId}, ${debt.debtType}, ${debt.lender}, ${debt.originalBalance}, ${debt.balance}, ${debt.rate},
                   ${debt.interestPaid}, ${debt.periodsToPayoff}, ${debt.payoffDate}, ${debt.maxInterest}, ${debt.minPaymentValue},
                   ${debt.minPaymentPercent}, ${debt.loanTerm}, ${debt.remainingTerm}, ${debt.pmi}, ${debt.purchasePrice}, ${debt.maxPeriods},
                   ${debt.escrow}, ${debt.maxLoc}, ${ stamp })
       """.stripMargin.update.run.transact(transactor).unsafeRunSync
      val debtHist: DebtHist = DebtHist(
        debt.id, debt.name, debt.userId, debt.debtType, debt.lender, debt.originalBalance,
        debt.balance, debt.rate, debt.interestPaid, debt.periodsToPayoff, debt.payoffDate, debt.maxInterest,
        debt.minPaymentValue, debt.minPaymentPercent, debt.loanTerm, debt.remainingTerm, debt.pmi, debt.purchasePrice, debt.maxPeriods,
        debt.escrow, debt.maxLoc, stamp
      )
      Right(debtHist)
    } catch {
      case err: Throwable => Left(err)
    }
  }
}
