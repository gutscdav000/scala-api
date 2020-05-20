import java.text.SimpleDateFormat
import java.util.Date

import cats.effect.IO
import core.model.User
import core.serializer.UserSerializer
import core.service.UserService
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import io.circe.{Encoder, Json}
import io.circe.generic.semiauto._
import org.http4s.circe._
import org.http4s.json4s.jackson.jsonOf
import org.http4s.{EntityDecoder, Response, Status}
import org.json4s.{DefaultFormats, Reader}
import org.json4s.JsonAST.JValue
import org.scalatest.FunSuite

import scala.tools.nsc.doc.html.HtmlTags.A

class UserServiceTest extends FunSuite {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  private val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:ApplicationDb",
    "postgres",
    "postgres"
  )

  def check[A](actual:        IO[Response[IO]],
               expectedStatus: Status,
               expectedBody:   Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Boolean =  {
    val actualResp         = actual.unsafeRunSync
    val statusCheck        = actualResp.status == expectedStatus
    val bodyCheck          = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync.isEmpty)( // Verify Response's body is empty.
      expected => actualResp.as[A].unsafeRunSync == expected
    )
    statusCheck && bodyCheck
  }

  test("UserService.getById positive") {
    val dob = new SimpleDateFormat("yyyy-MM-dd").parse("1996-02-08")
    val user = User(1, "gutscdav000", "gutscdav000@gmail.com", "pass", true, dob)
    val expectedJson = Json.obj(
      ("id", Json.fromInt(user.id)),
      ("username",  Json.fromString(user.username)),
      ("email", Json.fromString(user.email)),
      ("isActive", Json.fromBoolean(user.isActive)),
      ("dob", Json.fromString(new SimpleDateFormat("yyyy-MM-dd").format(user.dob)))
    )

    val userResp = UserService(user).getById(user.id, transactor)
    assert(check(userResp, Status.Ok, Some(expectedJson)))
  }

  test("UserService.getById negative") {
    val dob = new SimpleDateFormat("yyyy-MM-dd").parse("1996-02-08")
    val user = User(9999999, "nonexistentUser", "nonexistentUser@gmail.com", "pass", true, dob)

    val userResp = UserService(user).getById(user.id, transactor)
    assert(userResp.unsafeRunSync.status == Status.NotFound)
  }

}
