import java.text.SimpleDateFormat

import cats.effect.IO
import core.model.{Debt, User, UserModel}
import core.serializer.{DebtSerializer, UserSerializer}
import core.service.{DebtService, UserService}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import io.circe.{Json, parser}
import org.http4s.circe._
import org.http4s.json4s.jackson.jsonOf
import org.http4s.{EntityDecoder, Response, Status}
import org.json4s.JsonAST.JValue
import org.json4s.{DefaultFormats, Reader}
import org.scalatest.FunSuite
import io.circe._, io.circe.parser._

class DebtServiceTest extends FunSuite {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  implicit val formats = DefaultFormats + DebtSerializer() //do i need this?
  implicit val debtReader = new Reader[Debt] {
    def read(value: JValue): Debt = value.extract[Debt]
  }
  implicit val debtDec = jsonOf[IO, Debt]

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

  test("DebtService.findByUsername success") {
    val debtResp = DebtService.findByUsername("gutscdav000", transactor)

    val inputString: Json = parse("""
      [
      {"id":1,"name":"david's mortgage","userId":1,"debtType":"Mortgage","lender":"Fannie Mae","originalBalance":200000.0,"balance":200000.0,"rate":0.05,"interestPaid":0.0,"periodsToPayoff":360,"payoffDate":"2050-05-01","maxInterest":186343.11,"minPaymentValue":1074.0,"minPaymentPercent":-1.0,"loanTerm":360,"remainingTerm":360,"pmi":-1.0,"purchasePrice":250000.0,"maxPeriods":360,"escrow":-1.0,"maxLoc":-1.0},
      {"id":2,"name":"david's credit card","userId":1,"debtType":"Credit Card","lender":"Chase","originalBalance":0.0,"balance":0.0,"rate":0.18,"interestPaid":0.0,"periodsToPayoff":0,"payoffDate":"0001-01-01","maxInterest":0.0,"minPaymentValue":-1.0,"minPaymentPercent":0.02,"loanTerm":-1,"remainingTerm":-1,"pmi":-1.0,"purchasePrice":-1.0,"maxPeriods":-1,"escrow":-1.0,"maxLoc":-1.0}
      ]
      """).getOrElse(Json.Null)

    assert(check(debtResp, Status.Ok, Some(inputString)))
  }

  test("DebtService.findByUsername fail") {
    val debtResp = DebtService.findByUsername("none", transactor)

    val inputString: Json = parse("""
      []
      """).getOrElse(Json.Null)

    assert(check(debtResp, Status.Ok, Some(inputString)))
  }
}
