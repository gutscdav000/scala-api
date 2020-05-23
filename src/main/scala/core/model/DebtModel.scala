package core.model

import java.util.Date

import cats.effect.IO
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import cats.effect.IO
import doobie.util.fragment.Fragment
import doobie.implicits._

final case class Debt(
                     name: String,
                     user_id: Int,
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
                     maxLoc: Double
                   )

object DebtModel {

  def findByUser(user: User, transactor: Transactor[IO]): Either[Throwable, List[Debt]] = {
    try {
      val debtLst: List[Debt] = findBy(fr"user_id = ${user.id}", transactor).get
      Right(debtLst)
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  private def findBy(by: Fragment, transactor: Transactor[IO]): Option[List[Debt]] =
    (
      sql"""SELECT name,
           |       user_id,
           |       debt_type,
           |       lender,
           |       original_balance,
           |       balance,
           |       rate,
           |       interest_paid,
           |       periods_to_payoff,
           |       payoff_date,
           |       max_interest,
           |       min_payment_value,
           |       min_payment_percent,
           |       loan_term,
           |       remaining_term,
           |       pmi,
           |       purchase_price,
           |       max_periods,
           |       escrow,
           |       max_loc
           |       FROM public.debt WHERE """ ++ by)
      .query[Debt]
      .option
      .transact(transactor)
      .unsafeRunSync()
}
