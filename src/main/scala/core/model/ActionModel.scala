package core.model

import java.util.Date

import doobie.util.transactor.Transactor
import cats.effect.IO
import doobie.util.fragment.Fragment
import doobie.implicits._

case class Action(var id: Int, debtId: Int, userId: Int, principal: Double, interest: Double, payDate: Date)

object ActionModel {

  def insertAction(action: Action, transactor: Transactor[IO]): Either[Throwable, Action] = {
    try {
      sql"""INSERT INTO PUBLIC.ACTION ( DEBT_ID, USER_ID, PRINCIPAL, INTEREST, PAY_DATE )
           VALUES (${action.debtId}, ${action.userId}, ${action.principal}, ${action.interest}, ${action.payDate})
       """.stripMargin.update.run.transact(transactor).unsafeRunSync
      Right(action)
    } catch {
      case err: Throwable => Left(err)
    }
  }

  def updateAction(action: Action, transactor: Transactor[IO]): Either[Throwable, Action] = {
    try {
      sql"""UPDATE PUBLIC.ACTION
             SET PRINCIPAL = ${action.principal},
                 INTEREST = ${action.interest},
                 PAY_DATE = ${action.payDate}
              WHERE ID = ${action.id}
           """.stripMargin.update.run.transact(transactor).unsafeRunSync
      Right(action)
    } catch {
      case err: Exception => Left(err)
    }
  }

  def deleteAction(action: Action, transactor: Transactor[IO]): Either[Throwable, Action] = {
    try {
      val dbAction: List[Action] = findBy(fr"a.id = ${action.id}", transactor)
      if(dbAction.length == 1) {
        sql"""delete from public.action where id = ${dbAction.head.id}"""
          .stripMargin
          .update
          .run
          .transact(transactor)
          .unsafeRunSync
        Right(dbAction.head)
      } else {
        Left(new IllegalArgumentException(s"record not found. userId, debtId: ${action.userId}, ${action.debtId}."))
      }
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  def findByUsername(username: String, transactor: Transactor[IO]): Either[Throwable, List[Action]] = {
    try {
      val actionLst: List[Action] = findByUser(fr"u.username = ${username}", transactor).map(act =>  act.get)
      Right(actionLst)
    } catch {
      case exception: Throwable => Left(exception)
    }
  }

  def findByUser(by: Fragment, transactor: Transactor[IO]): List[Option[Action]] = {
    (sql"""SELECT a.id, a.debt_id, a.user_id, a.principal, a.interest, a.pay_date
                  FROM public.action a
                  join public.user u on u.id = a.user_id
                  WHERE """ ++ by)
      .query[Option[Action]]
      .to[List]
      .transact(transactor)
      .unsafeRunSync
  }

  def findBy(by: Fragment, transactor: Transactor[IO]): List[Action] = {
    (sql"""
           select a.id, a.debt_id, a.user_id, a.principal, a.interest, a.pay_date
            from public.action a where """ ++ by)
      .query[Action]
      .to[List]
      .transact(transactor)
      .unsafeRunSync
  }
}
