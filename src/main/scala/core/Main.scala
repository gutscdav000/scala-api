package core
package core.fp

// custom
import java.util.Date

import model.UserModel
import model.User
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

//  val userJsonEncoder: Encoder[User] = {
//    Encoder.instance { user: User =>
//    json"""{
//          |	"username": ${user.username},
//          |	"email": ${user.email},
//          |	"password": ${user.passwordHash},
//          |	"is_active": ${user.isActive},
//          |	"dob": ${user.dob}
//          |}""".stripMargin
//    }
//  }

  // create a json4s Reader[User]
  implicit val formats = DefaultFormats
  implicit val fooReader = new Reader[User] {
    def read(value: JValue): User = value.extract[User]
  }
  // create a http4s EntityDecoder[User] (which uses the Reader)
  implicit val userDec = jsonOf[IO, User] // only had 1 type param originally

//  implicit lazy val userDecoder = deriveDecoder[User]
  def httpRoutes(transactor: Transactor[cats.effect.IO]) = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      logger.debug("***hello world service***")
      Ok(s"Hello, $name.")
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