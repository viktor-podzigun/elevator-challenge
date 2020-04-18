package elevator.simulator

class ElevatorSimulator private(val status: List[(ElevatorId, FloorNumber, Seq[FloorNumber])],
                                pickupQueue: List[(FloorNumber, FloorNumber)] = Nil)
  extends ElevatorControlSystem {
  
  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): ElevatorControlSystem = {
    new ElevatorSimulator(status, (pickupFloor, goalFloor) :: pickupQueue)
  }

  def step(): ElevatorControlSystem = ???
}

object ElevatorSimulator {

  def apply(initialState: Seq[(ElevatorId, FloorNumber, Seq[FloorNumber])]): ElevatorSimulator = {
    require(initialState.nonEmpty, "at least one elevator should be provided")
    
    new ElevatorSimulator(initialState.toList)
  }
}
