import java.text.SimpleDateFormat

import cats.effect.IO
import core.model.{Debt, DebtModel}
import core.serializer.{DebtSerializer}
import core.service.{DebtService}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.implicits._
import io.circe.{Json}
import org.http4s.circe._
import org.http4s.json4s.jackson.jsonOf
import org.http4s.{EntityDecoder, Response, Status}
import org.json4s.JsonAST.JValue
import org.json4s.{DefaultFormats, Reader}
import org.scalatest.FunSuite
import io.circe.parser._

class DebtServiceTest extends FunSuite {

  private var updateId: Int = 0;

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

  test("DebtService.insert Success") {
    val newDebt: Debt = Debt(
      0,
      "test debt",
      1,
      "Mortgage",
      "Test Lender",
      50000.0,
      50000.0,
      0.10,
      1500.0,
      15,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-08-01"),
      15000.0,
      1200.0,
      -1.0,
      180,
      15,
      6000,
      450000.0,
      180,
      10000.0,
      15000.0
    )

    val ret = DebtService.insert(newDebt, transactor).unsafeRunSync
    assert(Status.Created == ret.status)

    val dbDebt: List[Debt] = DebtModel.findBy(fr"NAME=${newDebt.name} and DEBT_TYPE=${newDebt.debtType} and USER_ID=${newDebt.userId}", transactor)
    if(dbDebt.head != null) {
      newDebt.id = dbDebt.head.id
    }
    assert(newDebt == dbDebt.head)
  }

  test("DebtService.insert Fail") {
    val newDebt: Debt = Debt(
      0,
      "test debt",
      1,
      "Mortgage",
      "Test Lender",
      50000.0,
      50000.0,
      0.10,
      1500.0,
      15,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-08-01"),
      15000.0,
      1200.0,
      -1.0,
      180,
      15,
      6000,
      450000.0,
      180,
      10000.0,
      15000.0
    )

    val ret = DebtService.insert(newDebt, transactor).unsafeRunSync
    assert(Status.Conflict == ret.status)
  }

  test("DebtService.update Success") {
    val db: List[Debt] = DebtModel.findBy(fr"NAME='test debt' and DEBT_TYPE='Mortgage' and USER_ID=1", transactor)
    this.updateId = db.head.id

    val updateDebt: Debt = Debt(
      db.head.id,
      "test debt 123",
      1,
      "Mortgage",
      "Test Lender 2",
      50000.0,
      50000.0,
      0.08,
      15000.0,
      12,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-12-01"),
      15500.0,
      1000.0,
      -1.0,
      180,
      18,
      6000,
      450000.0,
      170,
      9000.0,
      14000.0
    )

    val ret = DebtService.update(updateDebt,transactor).unsafeRunSync
    assert( ret.status == Status.Ok)

    val dbDebt: List[Debt] = DebtModel.findBy(fr"NAME=${updateDebt.name} and DEBT_TYPE=${updateDebt.debtType} and USER_ID=${updateDebt.userId}", transactor)
    assert( dbDebt.head == updateDebt)
  }

  test("DebtService.update Fail") {
    val updateDebt: Debt = Debt(
      0,
      "test debt 123",
      1,
      "Mortgage",
      "Test Lender 2",
      50000.0,
      50000.0,
      0.08,
      15000.0,
      12,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-12-01"),
      15500.0,
      1000.0,
      -1.0,
      180,
      18,
      6000,
      450000.0,
      170,
      9000.0,
      14000.0
    )

    val ret = DebtService.update(updateDebt,transactor).unsafeRunSync
    assert( ret.status == Status.NotFound)
  }

  test("DebtService.delete Success") {
    val db: List[Debt] = DebtModel.findBy(fr"NAME='test debt 123' and DEBT_TYPE='Mortgage' and USER_ID=1", transactor)
    val ret = DebtService.delete(db.head, transactor).unsafeRunSync
    assert(ret.status == Status.Ok)

    val checkDebt: List[Debt] = DebtModel.findBy(fr"ID = ${db.head.id}", transactor)
    assert(checkDebt.isEmpty)
  }

  test("DebtService.delete Gone") {
    val deletedDebt: Debt = Debt(
      this.updateId,
      "test debt 123",
      1,
      "Mortgage",
      "Test Lender 2",
      50000.0,
      50000.0,
      0.08,
      15000.0,
      12,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-12-01"),
      15500.0,
      1000.0,
      -1.0,
      180,
      18,
      6000,
      450000.0,
      170,
      9000.0,
      14000.0
    )
    val ret = DebtService.delete(deletedDebt, transactor).unsafeRunSync
    assert(ret.status == Status.NotFound)
  }
}
