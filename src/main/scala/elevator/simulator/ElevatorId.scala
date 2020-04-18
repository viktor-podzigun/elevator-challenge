package elevator.simulator

case class ElevatorId private(value: Int) extends AnyVal

object ElevatorId {

  def apply(id: Int): ElevatorId = {
    require(id > 0, "ElevatorId should be > 0")
    new ElevatorId(id)
  }
}
