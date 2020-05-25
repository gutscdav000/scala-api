package core.service

import java.util.Date

import cats.effect.IO
import core.model.{Debt, DebtModel, User}
import core.serializer.DebtSerializer
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.http4s.dsl.io.{Conflict, Created, _}
import doobie.util.fragment.Fragment
import doobie.implicits._

object DebtService {

  implicit val formats = DefaultFormats + DebtSerializer()

  def insert(debt: Debt, transactor: Transactor[IO]): IO[Response[IO]] = {
    DebtModel.insertDebt(debt, transactor) match {
      case Right(debt) => Created(s"Debt: ${debt.name}, ${debt.debtType}")
      case Left(err) => Conflict(s"Error: ${err}")
    }
  }

  def update(debt: Debt, transactor: Transactor[IO]): IO[Response[IO]] = {
    val dbDebt: List[Debt] = DebtModel.findBy(fr"D.ID = ${debt.id}", transactor)
    if( dbDebt.length > 0  ) {
      DebtModel.updateDebt (debt, transactor) match {
        case Left(err) => InternalServerError(s"error: ${err}")
        case Right(debt) => Ok (s"Debt: ${debt.name}, ${debt.debtType}, ${debt.userId} updated.")
      }
    } else {
      NotFound(s"Debt: ${debt.name}, ${debt.debtType}, ${debt.userId} not found.")
    }
  }

  def delete(debt: Debt, transactor: Transactor[IO]): IO[Response[IO]] = {
    DebtModel.deleteDebt(debt, transactor) match {
      case Left(err) => NotFound(s"Debt: ${debt.name}, ${debt.userId}, ${debt.debtType} not found. Error: ${err}")
      case Right(debt) => Ok(s"Debt: ${debt.name}, ${debt.debtType}, ${debt.userId} deleted.")
    }

  }

  def findByUsername(username: String, transactor: Transactor[IO]): IO[Response[IO]] = {
      DebtModel.findByUsername(username, transactor) match {
        case Right(debts) => Ok(write[List[Debt]](debts))
        case err: Throwable => NotFound(s"Debts for ${username} weren't found.")
      }
  }

}