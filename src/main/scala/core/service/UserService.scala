package core.service

import core.model.{User, UserModel}
import core.serializer.UserSerializer

import org.http4s.dsl.io.{Conflict, Created, _}
import doobie.{ Transactor }
import cats.effect.IO
import org.http4s.Response
import org.json4s._
import org.json4s.jackson.Serialization.write



class UserService(val user: User) {

  // *** USER JSON ENCODER ***
  implicit val formats = DefaultFormats + UserSerializer()

  def getByUsername(username: String, transactor: Transactor[IO]): IO[Response[IO]]  = {
    UserModel.findByUsername(username, transactor) match {
      case Right(user) => Ok(write[User](user))
      case Left(err) => NotFound(s"User Not Found.")
    }
  }

  def insert(transactor: Transactor[IO]): IO[Response[IO]] = {
    UserModel.insertUser(user, transactor) match {
      case Right(u) => Created(s"User: ${user.username}, ${user.email} created.")
      case Left(err) => Conflict(s"Error: ${err}")
    }
  }

  def update(transactor: Transactor[IO]): IO[Response[IO]] = {
    val dbUser: Either[Throwable, User] = UserModel.findByUsername(user.username, transactor)

    dbUser match {
      case Left(e) => NotFound(s"User: ${user.username} not found. Error: ${e}")
      case Right(u) => {
        UserModel.updateUser(user, transactor) match {
          case Left(exception) => InternalServerError(s"error: ${exception}")
          case Right(user) => Ok(s"user: ${user.username} updated.")
        }
      }
    }
  }

  def delete(transactor: Transactor[IO]): IO[Response[IO]] = {
    UserModel.deleteUser(user, transactor) match {
      case Left(exception) => Gone(s"resource gone: ${exception}")
      case Right(user) => Ok(s"user: ${user.email}, ${user.username} deleted.")
    }
  }

}

object UserService {
  def apply(user: User) = new UserService(user)
}
