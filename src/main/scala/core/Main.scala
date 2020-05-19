package core
package core.fp

// custom
import model.UserModel
import model.User
import serializer.UserSerializer

import java.util.Date
import org.json4s.JsonAST.JValue
import service.UserService
// libraries
import com.typesafe.scalalogging.StrictLogging
import org.http4s.server.blaze._
import org.http4s.HttpRoutes
import org.http4s.syntax.kleisli._
import org.http4s.dsl.io._
import org.http4s.json4s.jackson._
import org.json4s._
import doobie.{ConnectionIO, Fragment, Transactor}
import doobie.implicits._
import cats.effect._
import cats.effect.IO


object Main extends IOApp with StrictLogging {

  val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:ApplicationDb",
    "postgres",
    "postgres"
  )

  // *** USER JSON DECODER ***
  // create a json4s Reader[User]
  implicit val formats = DefaultFormats + UserSerializer()
  implicit val userReader = new Reader[User] {
    def read(value: JValue): User = value.extract[User]
  }
  // create a http4s EntityDecoder[User] (which uses the Reader)
  implicit val userDec = jsonOf[IO, User] // only had 1 type param originally

  def httpRoutes(transactor: Transactor[cats.effect.IO]) = HttpRoutes.of[IO] {
        // USER Routes
    case GET -> Root / "user" / IntVar(id) => UserService(User(id,"","","",true,new Date())).getById(id, transactor)
    case req @ POST -> Root / "user" =>
      req.as[User] flatMap ( user => UserService(user).insert(transactor))
    case req @ PUT -> Root / "user" =>
      req.as[User] flatMap ( user => UserService(user).update(transactor))
    case req @ DELETE -> Root / "user" =>
      req.as[User] flatMap( user => UserService(user).delete(transactor))
  }.orNotFound





  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](ConcurrentEffect[IO], cats.effect.Timer[cats.effect.IO])
      .bindHttp(8080, "localhost")
      .withHttpApp(httpRoutes(transactor))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}