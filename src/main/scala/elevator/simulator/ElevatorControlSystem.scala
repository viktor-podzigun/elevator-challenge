package elevator.simulator

trait ElevatorControlSystem {

  def status(): Seq[(ElevatorId, FloorNumber, Seq[FloorNumber])]

  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): Unit

  def step(): Unit
}
