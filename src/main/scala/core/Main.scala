package core
package core.fp

import cats.effect._
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import org.http4s.server.blaze._
// custom
import model.DB
import model.UserModel
import model.User
import java.util.Date
import cats.effect.IO
import doobie.{ConnectionIO, Fragment, Transactor}
import doobie.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.syntax.kleisli._


object Main extends IOApp with StrictLogging {
//  val transactor: DB = DB(
//        "org.postgresql.Driver",
//        "jdbc:postgresql:ApplicationDb",
//        "postgres",
//        "postgres"
//  )
  val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:ApplicationDb",
    "postgres",
    "postgres"
  )


  def httpRoutes(transactor: Transactor[cats.effect.IO]) = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      logger.debug("***hello world service***")
      Ok(s"Hello, $name.")
    case GET -> Root / "second" / IntVar(id) =>
      UserModel.findById(id)
        .transact(transactor)
        .flatMap { userOption =>
          Ok(s"userOption is instance of: ${userOption.getClass} object: ${userOption.toString}")
        }
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