import java.text.SimpleDateFormat

import cats.effect.IO
import core.model.{User, UserModel}
import core.service.UserService
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import io.circe.Json
import org.http4s.circe._
import org.http4s.{EntityDecoder, Response, Status}
import org.scalatest.FunSuite


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

  test("UserService.getByUsername Positive") {
    val dob = new SimpleDateFormat("yyyy-MM-dd").parse("1996-02-08")
    val user = User(1, "gutscdav000", "gutscdav000@gmail.com", "pass", true, dob)
    val expectedJson = Json.obj(
      ("id",  Json.fromBigInt(user.id)),
      ("username",  Json.fromString(user.username)),
      ("email", Json.fromString(user.email)),
      ("isActive", Json.fromBoolean(user.isActive)),
      ("dob", Json.fromString(new SimpleDateFormat("yyyy-MM-dd").format(user.dob)))
    )

    val userResp = UserService(user).getByUsername(user.username, transactor)
    assert(check(userResp, Status.Ok, Some(expectedJson)))
  }

  test("UserService.getByUsername Negative") {
    val dob = new SimpleDateFormat("yyyy-MM-dd").parse("1996-02-08")
    val user = User(9999999, "nonexistentUser", "nonexistentUser@gmail.com", "pass", true, dob)

    val userResp = UserService(user).getByUsername(user.username, transactor)
    assert(userResp.unsafeRunSync.status == Status.NotFound)
  }

  test("UserService.insert Success") {
    val newUser = User(
      9,
      "test_user",
      "test_user@email.com",
      "test_pass",
      true,
      new SimpleDateFormat("yyyy-MM-dd").parse("1955-05-05")
    )

    val ret = UserService(newUser).insert(transactor).unsafeRunSync
    assert(ret.status == Status.Created)

    val dbUser: User = UserModel.findByUsername(newUser.username, transactor)
       match {
        case Left(err) => throw new Exception("DB error.")
        case Right(user) => user
      }

    val compUser = User(dbUser.id, newUser.username, newUser.email, newUser.passwordHash, newUser.isActive, newUser.dob)
    assert(compUser == dbUser)
  }

  test("UserService.insert Fail") {
    val existingUser = User(
      9,
      "test_user",
      "test_user@email.com",
      "test_pass",
      true,
      new SimpleDateFormat("yyyy-MM-dd").parse("1996-02-08")
    )

    val ret = UserService(existingUser).insert(transactor).unsafeRunSync
    assert(ret.status == Status.Conflict)
  }

  test("UserService.update Success") {
    val existingUser = User(
      9,
      "test_user",
      "test_user123@email.com",
      "pass_test",
      false,
      new SimpleDateFormat("yyyy-MM-dd").parse("1996-02-08")
    )

    val updated = UserService(existingUser).update(transactor).unsafeRunSync
    assert(updated.status == Status.Ok)
  }

  test("UserService.update NotFound") {
    val dob = new SimpleDateFormat("yyyy-MM-dd").parse("1996-02-08")
    val user = User(99999, "nonexistentUser", "nonexistentUser@gmail.com", "pass", true, dob)
    val ret = UserService(user).update(transactor).unsafeRunSync
    assert(ret.status == Status.NotFound)
  }

  test("UserService.delete Success") {
    val existingUser = User(
      9,
      "test_user",
      "test_user123@email.com",
      "pass_test",
      false,
      new SimpleDateFormat("yyyy-MM-dd").parse("1955-05-05")
    )
    val ret = UserService(existingUser).delete(transactor).unsafeRunSync
    assert(ret.status == Status.Ok)

    val notFound = UserService(existingUser).getByUsername(existingUser.username, transactor).unsafeRunSync
    assert(notFound.status == Status.NotFound)
  }

  test("UserService.delete Gone") {
    val deletedUser = User(
      9,
      "test_user",
      "test_user123@email.com",
      "pass_test",
      false,
      new SimpleDateFormat("yyyy-MM-dd").parse("1955-05-05")
    )

    val ret = UserService(deletedUser).delete(transactor).unsafeRunSync()
    assert(ret.status == Status.Gone)
  }
}
