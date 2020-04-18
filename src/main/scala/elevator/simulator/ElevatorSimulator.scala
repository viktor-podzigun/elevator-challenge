package elevator.simulator

import elevator.simulator.Direction.{Down, Up}

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
        val resStatus = List.empty[(ElevatorId, FloorNumber, Direction, Seq[FloorNumber])]
        
        status.foldLeft((resStatus, pickupQueue)) { case ((res, pickups), (id, currFloor, direction, goalFloors)) =>
          val (resFloor, resDirection, resGoalFloors, resPickups) = move(currFloor, direction, goalFloors, pickups)
          ((id, resFloor, resDirection, resGoalFloors) :: res, resPickups)
        }
      }

      new ElevatorSimulator(resStatus.reverse, resQueue)
    }
  }
  
  private def move(currFloor: FloorNumber,
                   direction: Direction,
                   goalFloors: Seq[FloorNumber],
                   pickups: List[(FloorNumber, FloorNumber)]
                  ): (FloorNumber, Direction, Seq[FloorNumber], List[(FloorNumber, FloorNumber)]) = {

    if (goalFloors.isEmpty) (currFloor, direction, goalFloors, pickups)
    else {
      val resDirection = {
        val minFloor = goalFloors.minBy(_.value)
        val maxFloor = goalFloors.maxBy(_.value)

        if (direction == Up && maxFloor.value < currFloor.value) Down
        else if (direction == Down && minFloor.value > currFloor.value) Up
        else direction
      }

      if (resDirection == Up) {
        (FloorNumber(currFloor.value + 1), resDirection, goalFloors, pickups)
      }
      else {
        (FloorNumber(currFloor.value - 1), resDirection, goalFloors, pickups)
      }
    }
  }
}

object ElevatorSimulator {

  def apply(initialState: Seq[(ElevatorId, FloorNumber, Direction, Seq[FloorNumber])]): ElevatorSimulator = {
    require(initialState.nonEmpty, "at least one elevator should be provided")
    
    new ElevatorSimulator(initialState.toList)
  }
}
