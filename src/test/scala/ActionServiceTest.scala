import java.text.SimpleDateFormat

import cats.effect.IO
import core.model.{Action, ActionModel}
import core.serializer.{ ActionSerializer }
import core.service.ActionService

import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.implicits._
import io.circe.Json
import org.http4s.circe._
import org.http4s.json4s.jackson.jsonOf
import org.http4s.{EntityDecoder, Response, Status}
import org.json4s.JsonAST.JValue
import org.json4s.{DefaultFormats, Reader}
import org.scalatest.FunSuite
import io.circe.parser._

class ActionServiceTest extends FunSuite {
  private var updateId: Int = 0;

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  implicit val formats = DefaultFormats + ActionSerializer()
  implicit val debtReader = new Reader[Action] {
    def read(value: JValue): Action = value.extract[Action]
  }
  implicit val debtDec = jsonOf[IO, Action]

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

  test("ActionService.insert Success") {
    val newAction: Action = Action(0, 2, 1, 120.05, 50.50,
      new SimpleDateFormat("yyyy-MM-dd").parse("2020-05-02")
    )

    val ret = ActionService.insert(newAction, transactor).unsafeRunSync
    assert(Status.Created == ret.status)

    val outputFormat = new SimpleDateFormat("yyyy-MM-dd")
    val dbAction: List[Action] = ActionModel.findBy(
      fr"""A.USER_ID=${newAction.userId}
          and A.DEBT_ID=${newAction.debtId}
          and A.INTEREST=${newAction.interest}
          and A.PRINCIPAL=${newAction.principal}
          """.stripMargin, transactor)

    if(!dbAction.isEmpty) {
      this.updateId = dbAction.head.id
      newAction.id = this.updateId
    }
    assert(newAction == dbAction.head)
  }

  test("ActionService.update Success") {
    val updateAction: Action = Action(this.updateId, 2, 1, 520.05, 89.99,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-06-03")
    )

    val db: List[Action] = ActionModel.findBy(fr"a.id = ${updateAction.id}", transactor)
    this.updateId = db.head.id

    val ret = ActionService.update(updateAction,transactor).unsafeRunSync
    assert( ret.status == Status.Ok)

    val dbAction: List[Action] = ActionModel.findBy(fr"a.id = ${updateAction.id}", transactor)
    assert( dbAction.head == updateAction)
  }

  test("ActionService.update Fail") {
    val updateAction: Action = Action(0, 2, 1, 520.05, 89.99,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-06-03")
    )

    val ret = ActionService.update(updateAction,transactor).unsafeRunSync
    assert( ret.status == Status.NotFound)
  }

  test("ActionService.findByUsername success") {
    val actionResp = ActionService.findByUsername("gutscdav000", transactor)

    val inputString: Json = parse(s"""
      [
        {"id":1,"debtId":1,"userId":1,"principal":240.67,"interest":833.33,"payDate":"0001-01-01"},
        {"id":2,"debtId":1,"userId":1,"principal":241.67,"interest":832.33,"payDate":"0001-01-01"},
        {"id":3,"debtId":1,"userId":1,"principal":242.68,"interest":831.32,"payDate":"0001-01-01"},
        {"id":4,"debtId":1,"userId":1,"principal":243.69,"interest":830.31,"payDate":"0001-01-01"},
        {"id":5,"debtId":1,"userId":1,"principal":244.7,"interest":829.3,"payDate":"0001-01-01"},
        {"id":${this.updateId},"debtId":2,"userId":1,"principal":520.05,"interest":89.99,"payDate":"2021-06-03"}
      ]
      """).getOrElse(Json.Null)

    assert(check(actionResp, Status.Ok, Some(inputString)))
  }

  test("ActionService.findByUsername fail") {
    val debtResp = ActionService.findByUsername("none", transactor)

    val inputString: Json = parse("""
      []
      """).getOrElse(Json.Null)

    assert(check(debtResp, Status.Ok, Some(inputString)))
  }

  test("ActionService.delete Success") {
    val db: List[Action] = ActionModel.findBy(fr"A.ID = ${this.updateId}", transactor)
    val ret = ActionService.delete(db.head, transactor).unsafeRunSync
    assert(ret.status == Status.Ok)

    val checkDebt: List[Action] = ActionModel.findBy(fr"A.ID = ${db.head.id}", transactor)
    assert(checkDebt.isEmpty)
  }

  test("ActionService.delete Gone") {
    val deletedAction: Action = Action(this.updateId, 2, 1, 520.05, 89.99,
      new SimpleDateFormat("yyyy-MM-dd").parse("2021-06-03")
    )
    val ret = ActionService.delete(deletedAction, transactor).unsafeRunSync
    assert(ret.status == Status.NotFound)
  }
}
