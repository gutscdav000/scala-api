import org.scalatest.Sequential

class MainTest extends Sequential(
  new UserServiceTest(),
  new DebtServiceTest(),
  new ActionServiceTest()
)
