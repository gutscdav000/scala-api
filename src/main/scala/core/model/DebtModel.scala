package core.model

import java.util.Date

import cats.effect.IO
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import cats.effect.IO
import doobie.util.fragment.Fragment
import doobie.implicits._
import doobie.util.transactor

final case class Debt(
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
                     maxLoc: Double
                   )

object DebtModel {

  def findByUsername(username: String, transactor: Transactor[IO]): Either[Throwable, List[Debt]] = {
    try {
      val debtLst: List[Debt] = findByUser(fr"u.username = ${username}", transactor).map(debt => debt.get)
      Right(debtLst)
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  private def findByUser(by: Fragment, transactor: Transactor[IO]): List[Option[Debt]] =
    (
      sql"""SELECT d.name,
                  d.user_id,
                  d.debt_type,
                  d.lender,
                  d.original_balance,
                  d.balance,
                  d.rate,
                  d.interest_paid,
                  d.periods_to_payoff,
                  d.payoff_date,
                  d.max_interest,
                  d.min_payment_value,
                  d.min_payment_percent,
                  d.loan_term,
                  d.remaining_term,
                  d.pmi,
                  d.purchase_price,
                  d.max_periods,
                  d.escrow,
                  d.max_loc
                  FROM public.debt d
                  join public.user u on u.id = d.user_id
                  WHERE """ ++ by)
      .query[Option[Debt]]
      .to[List]
      .transact(transactor)
      .unsafeRunSync()


  private def findBy(by: Fragment, transactor: Transactor[IO]): List[Debt] =
    (
      sql"""SELECT d.name,
           |       d.user_id,
           |       d.debt_type,
           |       d.lender,
           |       d.original_balance,
           |       d.balance,
           |       d.rate,
           |       d.interest_paid,
           |       d.periods_to_payoff,
           |       d.payoff_date,
           |       d.max_interest,
           |       d.min_payment_value,
           |       d.min_payment_percent,
           |       d.loan_term,
           |       d.remaining_term,
           |       d.pmi,
           |       d.purchase_price,
           |       d.max_periods,
           |       d.escrow,
           |       d.max_loc
           |       FROM public.debt d
           |       WHERE """ ++ by)
      .query[Debt]
      .to[List]
      .transact(transactor)
      .unsafeRunSync()
}
