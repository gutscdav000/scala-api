package core.service

//custom
import core.model.{User, UserModel}
import core.serializer.UserSerializer

import org.http4s.dsl.io.{Conflict, Created, _}
import doobie.{ConnectionIO, Fragment, Transactor}
import cats.effect.IO
import org.http4s.Response
import org.json4s._
import org.json4s.jackson.Serialization.write
import JsonDSL._



class UserService(val user: User) {

  // *** USER JSON ENCODER ***
  implicit val formats = DefaultFormats + UserSerializer()

  def getById(id: Int, transactor: Transactor[IO]): IO[Response[IO]]  = {
    UserModel.findById(id, transactor) match {
      case Some(user) => Ok(write[User](user))
      case None => NotFound(s"User Not Found.")
    }
  }

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
