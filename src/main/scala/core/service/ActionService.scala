package core.service

import cats.effect.IO
import core.model.{Action, ActionModel}
import core.serializer.ActionSerializer

import org.json4s.DefaultFormats
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.json4s.jackson.Serialization.write
import org.http4s.dsl.io.{Conflict, Created, _}
import doobie.util.fragment.Fragment
import doobie.implicits._

object ActionService {
  implicit val formats = DefaultFormats + ActionSerializer()

  def insert(action: Action, transactor: Transactor[IO]): IO[Response[IO]] = {
    ActionModel.insertAction(action, transactor) match {
      case Right(action) => Created(s"action: ${action.debtId}, ${action.principal}, ${action.interest} created.")
      case Left(err) => Conflict(s"Error: ${err}")
    }
  }

  def update(action: Action, transactor: Transactor[IO]): IO[Response[IO]] = {
    val dbAction: List[Action] = ActionModel.findBy(fr"A.ID = ${action.id}", transactor)
    if( dbAction.length > 0  ) {
      ActionModel.updateAction (action, transactor) match {
        case Left(err) => InternalServerError(s"error: ${err}")
        case Right(action) => Ok (s"action (debtId, userId): (${action.debtId}, ${action.userId}) updated.")
      }
    } else {
      NotFound(s"action (debtId, userId): (${action.debtId}, ${action.userId}) not found.")
    }
  }

  def delete(action: Action, transactor: Transactor[IO]): IO[Response[IO]] = {
    ActionModel.deleteAction(action, transactor) match {
      case Left(err) => NotFound(s"Action (userId, debtId): (${action.userId}, ${action.debtId}) not found. Error: ${err}")
      case Right(debt) => Ok(s"Action (userId, debtId): (${action.userId}, ${action.debtId}) deleted.")
    }
  }

  def findByUsername(username: String, transactor: Transactor[IO]): IO[Response[IO]] = {
    ActionModel.findByUsername(username, transactor) match {
      case Right(actions) => Ok(write[List[Action]](actions))
      case err: Throwable => NotFound(s"Actions for ${username} weren't found.")
    }
  }
}
