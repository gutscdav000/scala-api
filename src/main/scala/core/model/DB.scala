package core.model

import doobie._
import doobie.util.ExecutionContexts
import doobie.implicits._
import doobie.postgres.implicits._
import cats.effect.IO

class DB private( val driver: String,
                  val url: String,
                  val username: String,
                  val password: String ) {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  val transactor = Transactor.fromDriverManager[ConnectionIO](
    driver,
    url,
    username,
    password
  )
}

object DB {
  def apply(driver: String, url: String, username: String, password: String): DB = new DB(driver, url, username, password)
}