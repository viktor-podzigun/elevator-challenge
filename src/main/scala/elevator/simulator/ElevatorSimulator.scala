package elevator.simulator

import elevator.simulator.Direction.Up

class ElevatorSimulator private(val status: List[(ElevatorId, FloorNumber, Direction, Seq[FloorNumber])],
                                pickupQueue: List[(FloorNumber, FloorNumber)] = Nil)
  extends ElevatorControlSystem {
  
  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): ElevatorControlSystem = {
    new ElevatorSimulator(status, (pickupFloor, goalFloor) :: pickupQueue)
  }

  def step(): ElevatorControlSystem = {
    if (pickupQueue.isEmpty && status.forall(_._4.isEmpty)) this
    else {
      val (resStatus, resQueue) = {
        status.foldLeft((List.empty[(ElevatorId, FloorNumber, Direction, Seq[FloorNumber])], pickupQueue)) {
          case ((res, pickups), (id, currFloor, direction, goalFloors)) =>
            val (resFloor, resDirection, resGoalFloors, resPickups) = move(currFloor, direction, goalFloors, pickups)
            (res :+ Tuple4(id, resFloor, resDirection, resGoalFloors), resPickups)
        }
      }

      new ElevatorSimulator(resStatus, resQueue)
    }
  }
  
  private def move(currFloor: FloorNumber,
                   direction: Direction,
                   goalFloors: Seq[FloorNumber],
                   pickups: List[(FloorNumber, FloorNumber)]
                  ): (FloorNumber, Direction, Seq[FloorNumber], List[(FloorNumber, FloorNumber)]) = {

    if (direction == Up) {
      (FloorNumber(currFloor.value + 1), direction, goalFloors, pickups)
    } else {
      (FloorNumber(currFloor.value - 1), direction, goalFloors, pickups)
    }
  }
}

object ElevatorSimulator {

  def apply(initialState: Seq[(ElevatorId, FloorNumber, Direction, Seq[FloorNumber])]): ElevatorSimulator = {
    require(initialState.nonEmpty, "at least one elevator should be provided")
    
    new ElevatorSimulator(initialState.toList)
  }
}
