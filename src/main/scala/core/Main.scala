package core
package core.fp

//import cats.effect.{ExitCode, IO, IOApp}
//import org.http4s.HttpRoutes
//import org.http4s.dsl.impl.Root
import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext.global


object Main extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](ConcurrentEffect[IO], cats.effect.Timer[cats.effect.IO])
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}