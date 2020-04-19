package elevator.simulator

import elevator.simulator.Direction._
import elevator.simulator.ElevatorSimulator._

case class ElevatorSimulator private(status: List[(ElevatorId, FloorNumber, Direction, Set[FloorNumber])],
                                     pickups: Map[FloorNumber, FloorNumber])
  extends ElevatorControlSystem {
  
  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): ElevatorSimulator = {
    copy(status, pickups + (pickupFloor -> goalFloor))
  }

  def step(): ElevatorSimulator = {
    if (pickups.isEmpty && status.forall(_._4.isEmpty)) this
    else {
      var currPickups = pickups
      val resStatus = status.map { case (id, currFloor, direction, goalFloors) =>
        val (resFloor, resDirection, resGoalFloors, resPickups) =
          move(currFloor, direction, goalFloors, currPickups)
        
        currPickups = resPickups
        (id, resFloor, resDirection, resGoalFloors)
      }

      copy(resStatus, currPickups)
    }
  }
}

object ElevatorSimulator {

  def apply(initialState: Seq[(ElevatorId, FloorNumber, Direction, Set[FloorNumber])]): ElevatorSimulator = {
    require(initialState.nonEmpty, "at least one elevator should be provided")
    
    new ElevatorSimulator(initialState.toList, Map.empty)
  }

  def move(currFloor: FloorNumber,
           direction: Direction,
           goalFloors: Set[FloorNumber],
           pickups: Map[FloorNumber, FloorNumber]
          ): (FloorNumber, Direction, Set[FloorNumber], Map[FloorNumber, FloorNumber]) = {

    if (goalFloors.isEmpty && pickups.isEmpty) (currFloor, direction, goalFloors, pickups)
    else {
      val resDirection = getDirection(currFloor, direction, goalFloors)

      if (resDirection == Up) {
        (FloorNumber(currFloor.value + 1), resDirection, goalFloors, pickups)
      }
      else {
        (FloorNumber(currFloor.value - 1), resDirection, goalFloors, pickups)
      }
    }
  }

  def getDirection(currFloor: FloorNumber, direction: Direction, goalFloors: Set[FloorNumber]): Direction = {
    if (goalFloors.isEmpty) direction
    else {
      val minFloor = goalFloors.minBy(_.value)
      val maxFloor = goalFloors.maxBy(_.value)

      if (direction == Up && maxFloor.value < currFloor.value) Down
      else if (direction == Down && minFloor.value > currFloor.value) Up
      else direction
    }
  }
}
