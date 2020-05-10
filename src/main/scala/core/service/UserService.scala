package core.service

import cats.effect.IO
import core.model.{User, UserModel}
import org.http4s.dsl.io.{Conflict, Created, _}
import doobie.{ConnectionIO, Fragment, Transactor}
import cats.effect.IO
import org.http4s.Response
///////



class UserService(val user: User) {

  def insert(transactor: Transactor[IO]): IO[Response[IO]] = {
    UserModel.insertUser(user, transactor) match {
      case Right(u) => Created(s"User: ${user.username}, ${user.email} created.")
      case Left(err) => Conflict(s"Error: ${err}")
    }
  }

  def update(transactor: Transactor[IO]): IO[Response[IO]] = {
    val dbUser: Either[Throwable, User] = UserModel.findByEmail(user.email, transactor)

    dbUser match {
      case Left(e) => NotFound(s"User: ${user.email} not found. Error: ${e}")
      case Right(u) => {
        UserModel.updateUser(u, transactor) match {
          case Left(exception) => InternalServerError(s"error: ${exception}")
          case Right(user) => Ok(s"user: ${user.email} updated.")
        }
      }
    }
  }

}

object UserService {
  def apply(user: User) = new UserService(user)
}
