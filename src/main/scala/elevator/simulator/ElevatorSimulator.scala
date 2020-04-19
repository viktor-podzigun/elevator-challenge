package elevator.simulator

import elevator.simulator.Direction._
import elevator.simulator.ElevatorSimulator._

case class ElevatorSimulator private(status: List[(ElevatorId, FloorNumber, Direction, Set[FloorNumber])],
                                     pickups: Map[FloorNumber, FloorNumber])
  extends ElevatorControlSystem {
  
  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): ElevatorSimulator = {
    require(pickupFloor != goalFloor, "pickupFloor and goalFloor cannot be the same")
    
    val pickupDirection = getDirection(pickupFloor, goalFloor)
    
    val (sameDir, oppositeDir) = status.partition { case (_, currFloor, direction, goalFloors) =>
      val elevatorDirection = ensureDirection(currFloor, direction, goalFloors)
      isSameDirection(pickupDirection, elevatorDirection, goalFloors)
    }

    val sameDirSorted = sameDir.map { case (id, currFloor, _, _) =>
      val distance = math.abs(currFloor.value - pickupFloor.value)
      (id, distance)
    }.sortBy(_._2)

    val (elevatorId, _) = sameDirSorted.headOption.getOrElse {
      val oppositeDirSorted = oppositeDir.map { case (id, _, _, goalFloors) =>
        val distance = goalFloors.map { goalFloor =>
          math.abs(goalFloor.value - pickupFloor.value)
        }.max
        (id, distance)
      }.sortBy(_._2)

      oppositeDirSorted.head
    }

    val resStatus = status.map {
      case (id, currFloor, direction, goalFloors) if id == elevatorId =>
        (id, currFloor, direction, goalFloors + pickupFloor)
      case res => res
    }
    
    copy(resStatus, pickups + (pickupFloor -> goalFloor))
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

  def getDirection(pickupFloor: FloorNumber, goalFloor: FloorNumber): Direction = {
    if (pickupFloor.value < goalFloor.value) Up
    else Down
  }

  def isSameDirection(pickupDirection: Direction,
                      elevatorDirection: Direction,
                      goalFloors: Set[FloorNumber]): Boolean = {

    if (goalFloors.isEmpty) true
    else pickupDirection == elevatorDirection
  }

  def ensureDirection(currFloor: FloorNumber, direction: Direction, goalFloors: Set[FloorNumber]): Direction = {
    if (goalFloors.isEmpty) direction
    else {
      val minFloor = goalFloors.minBy(_.value)
      val maxFloor = goalFloors.maxBy(_.value)

      if (direction == Up && maxFloor.value < currFloor.value) Down
      else if (direction == Down && minFloor.value > currFloor.value) Up
      else direction
    }
  }
  
  def move(currFloor: FloorNumber,
           direction: Direction,
           goalFloors: Set[FloorNumber],
           pickups: Map[FloorNumber, FloorNumber]
          ): (FloorNumber, Direction, Set[FloorNumber], Map[FloorNumber, FloorNumber]) = {

    if (goalFloors.isEmpty && pickups.isEmpty) (currFloor, direction, goalFloors, pickups)
    else {
      val resDirection = ensureDirection(currFloor, direction, goalFloors)

      if (resDirection == Up) {
        (FloorNumber(currFloor.value + 1), resDirection, goalFloors, pickups)
      }
      else {
        (FloorNumber(currFloor.value - 1), resDirection, goalFloors, pickups)
      }
    }
  }
}
