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
import io.circe.generic.auto._
import io.circe.{Encoder, Json}

object DebtService {

  // *** USER JSON ENCODER ***
  implicit val formats = DefaultFormats + DebtSerializer()

  def insert(debt: Debt, transactor: Transactor[IO]): IO[Response[IO]] = {
    DebtModel.insertDebt(debt, transactor) match {
      case Right(debt) => Created(s"Debt: ${debt.name}, ${debt.debtType}")
      case Left(err) => Conflict(s"Error: ${err}")
    }
  }

  def findByUsername(username: String, transactor: Transactor[IO]): IO[Response[IO]] = {
      DebtModel.findByUsername(username, transactor) match {
        case Right(debts) => Ok(write[List[Debt]](debts))
        case err: Throwable => NotFound(s"Debts for ${username} weren't found.")
      }
  }

}


//object DebtService {
//  def apply(debt: Debt) = new DebtService(debt)
//}