package elevator.simulator

class ElevatorId private(val value: Int) extends AnyVal

object ElevatorId {

  def apply(id: Int): ElevatorId = {
    require(0 < id && id <= maxId,
      s"ElevatorId should be between 1 and $maxId")
    
    new ElevatorId(id)
  }

  private val maxId: Int = 16
}
