package elevator.simulator

trait ElevatorControlSystem {

  def status(): Seq[(ElevatorId, FloorNumber, Direction, Set[FloorNumber])]

  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): ElevatorControlSystem

  def step(): ElevatorControlSystem
}
