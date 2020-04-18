package elevator.simulator

class ElevatorSimulator private(val status: Seq[(ElevatorId, FloorNumber, Seq[FloorNumber])])
  extends ElevatorControlSystem {
  
  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): ElevatorControlSystem = ???

  def step(): ElevatorControlSystem = ???
}

object ElevatorSimulator {

  def apply(initialState: Seq[(ElevatorId, FloorNumber, Seq[FloorNumber])]): ElevatorSimulator = {
    require(initialState.nonEmpty, "at least one elevator should be provided")
    
    new ElevatorSimulator(initialState)
  }
}
