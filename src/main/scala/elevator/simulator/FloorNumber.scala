package elevator.simulator

case class FloorNumber private(value: Int) extends AnyVal

object FloorNumber {

  def apply(floor: Int): FloorNumber = {
    require(minFloor <= floor && floor <= maxFloor,
      s"FloorNumber should be between $minFloor and $maxFloor")
    
    new FloorNumber(floor)
  }
  
  private val minFloor: Int = -9
  private val maxFloor: Int = 9
}
