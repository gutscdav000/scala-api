package core
package core.fp

//import cats.effect.{ExitCode, IO, IOApp}
//import org.http4s.HttpRoutes
//import org.http4s.dsl.impl.Root
import cats.data.Kleisli
import cats.effect._
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import doobie.util.transactor.Transactor
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.global
// custom
import model.DB


object Main extends IOApp with StrictLogging {

  val httpRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      logger.debug("***hello world service***")
      Ok(s"Hello, $name.")
    case GET -> Root / "second" / name =>
      logger.debug("***second service***")
      logger.debug(s"testing transactor ${transactor.toString} ${transactor} type: ${transactor.transactor.getClass}")
      Ok(s"second, $name.")
  }.orNotFound

  val transactor: DB = DB(
        "org.postgresql.Driver",
        "jdbc:postgresql:ApplicationDb",
        "postgres",
        "postgres"
  )


  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](ConcurrentEffect[IO], cats.effect.Timer[cats.effect.IO])
      .bindHttp(8080, "localhost")
      .withHttpApp(httpRoutes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}