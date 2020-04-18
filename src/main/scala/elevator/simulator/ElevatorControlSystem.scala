package elevator.simulator

trait ElevatorControlSystem {

  def status(): Seq[(Int, Int, Int)]

  def pickup(floor: Int, direction: Int)

  def step()
}
