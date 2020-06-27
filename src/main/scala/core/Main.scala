package core
package core.fp

// custom
import model.{Action, Debt, User}
import org.http4s.Uri.UserInfo
import serializer.{ActionSerializer, DebtSerializer, UserSerializer}
import service.{ActionService, DebtService, UserService, AuthService}
// libraries
import java.util.Date
import org.json4s.JsonAST.JValue
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
//import org.http4s.circe._


object Main extends IOApp with StrictLogging {

  val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:ApplicationDb",
    "postgres",
    "postgres"
  )


  // create a json4s Reader[User]
  implicit val formats = DefaultFormats + UserSerializer() + DebtSerializer() + ActionSerializer()
  implicit val userReader = new Reader[User] {
    def read(value: JValue): User = value.extract[User]
  }
  implicit val debtReader = new Reader[Debt] {
    def read(value: JValue): Debt = value.extract[Debt]
  }
  implicit val actionReader = new Reader[Action] {
    def read(value: JValue): Action = value.extract[Action]
  }
  implicit val authInfoReader = new Reader[UserInfo] {
    def read(value: JValue): UserInfo = value.extract[UserInfo]
  }
  // create a http4s EntityDecoder[User] (which uses the Reader)
  implicit val userDec = jsonOf[IO, User]
  implicit val debtDec = jsonOf[IO, Debt]
  implicit val actionDec = jsonOf[IO, Action]
  implicit def authInfoDec = jsonOf[IO, UserInfo]

  def httpRoutes(transactor: Transactor[cats.effect.IO]) = HttpRoutes.of[IO] {
      // LOGIN
    case req @ POST -> Root / "login" =>
      req.as[UserInfo] flatMap( user => AuthService.login(user, transactor))
      // USER Routes
    case GET -> Root / "user" / username => UserService(User(1, username,"","",true,new Date())).getByUsername(username, transactor)
    case req @ POST -> Root / "user" =>
      req.as[User] flatMap ( user => UserService(user).insert(transactor))
    case req @ PUT -> Root / "user" =>
      req.as[User] flatMap ( user => UserService(user).update(transactor))
    case req @ DELETE -> Root / "user" =>
      req.as[User] flatMap( user => UserService(user).delete(transactor))
      // DEBT Routes
    case GET -> Root / "debt" / username => DebtService.findByUsername(username, transactor)
    case req @ POST -> Root / "debt" =>
      req.as[Debt] flatMap(debt => DebtService.insert(debt, transactor))
    case req @ PUT -> Root / "debt" =>
      req.as[Debt] flatMap(debt => DebtService.update(debt, transactor))
    case req @ DELETE -> Root / "debt" =>
      req.as[Debt] flatMap( debt => DebtService.delete(debt, transactor))
      // ACTION Routes
    case GET -> Root / "action" / username => ActionService.findByUsername(username, transactor)
    case req @ POST -> Root / "action" =>
      req.as[Action] flatMap( action => ActionService.insert(action, transactor))
    case req @ PUT -> Root / "action" =>
      req.as[Action] flatMap( action => ActionService.update(action, transactor))
    case req @ DELETE -> Root / "action" =>
      req.as[Action] flatMap( action => ActionService.delete(action, transactor))
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