package core
package core.fp

import cats.data
import cats.effect._
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
//import org.http4s.circe.jsonOf
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
//import org.http4s.circe._
// new
import cats.effect._
//import io.circe._
//import io.circe.literal._
import org.http4s._
import org.http4s.dsl.io._
//import io.circe.syntax._
//import io.circe.generic.auto._
//import org.http4s.circe.CirceEntityEncoder._
//import org.http4s.circe.CirceEntityDecoder._
// new new
//import io.circe.parser
//import io.circe.generic.semiauto.deriveDecoder
//import io.circe.derivation.deriveDecoder
// new new new
import cats.effect.IO
//import io.circe.generic.semiauto._
//import io.circe.syntax._
//import io.circe.{Decoder, Encoder}
//import org.http4s.circe._
import org.http4s.client.blaze._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
// new new new new
import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.http4s._
import org.http4s.dsl._
import org.http4s.json4s.jackson._

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
    case GET -> Root / "user" / IntVar(id) =>
      UserModel.findById(id)
        .transact(transactor)
        .flatMap { userOption =>
          Ok(s"userOption is instance of: ${userOption.getClass} object: ${userOption.toString}")
        }
    case req @ POST -> Root / "user" =>
      req.as[User] flatMap ( user => {
        val conInt: IO[Int] = UserModel.insertUser(user).transact(transactor)
        Ok(s"user found ${conInt} ${user.username}, ${user.email}, ${user.passwordHash}")
      })



//      implicit val userDecoder: Decoder[User] = deriveDecoder[User]
//      implicit def jsonDecoder[A: Decoder]: EntityDecoder[IO, A] = jsonOf[IO, A]
//                      parser.decode(req.body.asJson) match {
//                        case Right(user) => Ok("we decoded the json")
//                        case Left(err) => BadRequest(err.toString)
//                      }
//      match {
//        case Left(failure) => BadRequest(failure.toString)
//        case Right(json) => Ok(json)
//      }
//      req.decodeJson[User] flatMap {(up: User) =>
//        Ok(UserModel.insertUser(up).asJson)
//      }
//      req.decode[User] { data => Ok(data) }

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