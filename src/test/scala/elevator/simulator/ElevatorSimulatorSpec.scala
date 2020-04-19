package elevator.simulator

import elevator.simulator.Direction._
import elevator.simulator.ElevatorSimulator._
import org.scalactic.source.Position
import org.scalatest.{FlatSpec, Matchers}

class ElevatorSimulatorSpec extends FlatSpec with Matchers {

  implicit class ElevatorSimulatorOps(simulator: ElevatorSimulator) {

    def assertSimulator(expected: ElevatorSimulator)(implicit pos: Position): ElevatorSimulator = {
      simulator shouldBe expected
      simulator
    }
  }
  
  "constructor" should "fail if no elevators are provided" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      ElevatorSimulator(Nil)
    }
    
    //then
    e.getMessage shouldBe "requirement failed: at least one elevator should be provided"
  }
  
  it should "create initial ElevatorSimulator" in {
    //given
    val initialState = List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
      (ElevatorId(3), FloorNumber(-1), Up, Set(FloorNumber(-1)))
    )
    
    //when
    val result = ElevatorSimulator(initialState)
    
    //then
    result.status shouldBe initialState
    result.pickups shouldBe Map.empty
  }
  
  "getDirection" should "return direction based on pickupFloor and goalFloor" in {
    //when & then
    getDirection(FloorNumber(0), FloorNumber(1)) shouldBe Up
    getDirection(FloorNumber(-1), FloorNumber(0)) shouldBe Up
    getDirection(FloorNumber(0), FloorNumber(-1)) shouldBe Down
    getDirection(FloorNumber(1), FloorNumber(0)) shouldBe Down
    getDirection(FloorNumber(0), FloorNumber(0)) shouldBe Down
  }

  "isSameDirection" should "return true if goalFloors is empty" in {
    //when & then
    isSameDirection(Up, Down, Set.empty) shouldBe true
  }

  it should "return true if the same direction" in {
    //when & then
    isSameDirection(Up, Up, Set(FloorNumber(1))) shouldBe true
  }

  it should "return false if not the same direction" in {
    //when & then
    isSameDirection(Up, Down, Set(FloorNumber(1))) shouldBe false
  }

  "ensureDirection" should "not change direction if goalFloors is empty" in {
    //when & then
    ensureDirection(FloorNumber(0), Up, Set.empty) shouldBe Up
  }

  it should "not change direction if only one goalFloor and it matches currFloor" in {
    //when & then
    ensureDirection(FloorNumber(1), Up, Set(FloorNumber(1))) shouldBe Up
    ensureDirection(FloorNumber(1), Down, Set(FloorNumber(1))) shouldBe Down
  }

  it should "change direction from Up to Down" in {
    //when & then
    ensureDirection(FloorNumber(1), Up, Set(FloorNumber(0))) shouldBe Down
  }

  it should "change direction from Down to Up" in {
    //when & then
    ensureDirection(FloorNumber(-1), Down, Set(FloorNumber(0))) shouldBe Up
  }

  "pickElevator" should "pick closest elevator of same direction" in {
    //given
    val status = List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(1), Down, Set.empty[FloorNumber]),
      (ElevatorId(3), FloorNumber(1), Down, Set(FloorNumber(0)))
    )

    //when
    val result = pickElevator(status, FloorNumber(2), FloorNumber(3))

    //then
    result shouldBe ElevatorId(2)
  }

  it should "pick closest elevator of opposite direction" in {
    //given
    val status = List(
      (ElevatorId(1), FloorNumber(0), Down, Set(FloorNumber(-1))),
      (ElevatorId(2), FloorNumber(1), Down, Set(FloorNumber(-2)))
    )

    //when
    val result = pickElevator(status, FloorNumber(2), FloorNumber(3))

    //then
    result shouldBe ElevatorId(1)
  }

  "pickup" should "fail if pickupFloor and goalFloor are the same" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2)))
    ))

    //when
    val e = the[IllegalArgumentException] thrownBy {
      simulator.pickup(FloorNumber(1), FloorNumber(1))
    }

    //then
    e.getMessage shouldBe "requirement failed: pickupFloor and goalFloor cannot be the same"
  }

  it should "add pickupFloor to elevator's goalFloors" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1)))
    ))
    
    //when
    val result = simulator.pickup(FloorNumber(2), FloorNumber(3))
    
    //then
    result shouldBe ElevatorSimulator(
      status = List(
        (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
        (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1), FloorNumber(2)))
      ),
      pickups = Map(
        FloorNumber(2) -> Set(FloorNumber(3))
      )
    )
  }
  
  it should "add goalFloor to existing pickupFloor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1)))
    )).pickup(FloorNumber(2), FloorNumber(3))
    
    simulator shouldBe ElevatorSimulator(
      status = List(
        (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
        (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1), FloorNumber(2)))
      ),
      pickups = Map(
        FloorNumber(2) -> Set(FloorNumber(3))
      )
    )
    
    //when
    val result = simulator.pickup(FloorNumber(2), FloorNumber(4))
    
    //then
    result shouldBe ElevatorSimulator(
      status = List(
        (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
        (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1), FloorNumber(2)))
      ),
      pickups = Map(
        FloorNumber(2) -> Set(FloorNumber(3), FloorNumber(4))
      )
    )
  }
  
  "move" should "do nothing if no goal floors and no pickup requests" in {
    //given
    val currFloor = FloorNumber(1)
    val direction = Up
    val goalFloors = Set.empty[FloorNumber]
    val pickups = Map.empty[FloorNumber, Set[FloorNumber]]
    
    //when
    val (resFloor, resDirection, resGoals, resPickups) =
      move(currFloor, direction, goalFloors, pickups)
    
    //then
    resFloor shouldBe currFloor
    resDirection shouldBe direction
    resGoals shouldBe goalFloors
    resPickups shouldBe pickups
  }
  
  it should "move Up to the next floor" in {
    //given
    val currFloor = FloorNumber(0)
    val direction = Up
    val goalFloors = Set(FloorNumber(2))
    val pickups = Map.empty[FloorNumber, Set[FloorNumber]]
    
    //when
    val (resFloor, resDirection, resGoals, resPickups) =
      move(currFloor, direction, goalFloors, pickups)
    
    //then
    resFloor shouldBe FloorNumber(1)
    resDirection shouldBe direction
    resGoals shouldBe goalFloors
    resPickups shouldBe pickups
  }
  
  it should "move Down to the next floor" in {
    //given
    val currFloor = FloorNumber(0)
    val direction = Down
    val goalFloors = Set(FloorNumber(-2))
    val pickups = Map.empty[FloorNumber, Set[FloorNumber]]
    
    //when
    val (resFloor, resDirection, resGoals, resPickups) =
      move(currFloor, direction, goalFloors, pickups)
    
    //then
    resFloor shouldBe FloorNumber(-1)
    resDirection shouldBe direction
    resGoals shouldBe goalFloors
    resPickups shouldBe pickups
  }
  
  it should "move and change direction from Down to Up" in {
    //given
    val currFloor = FloorNumber(0)
    val direction = Down
    val goalFloors = Set(FloorNumber(2))
    val pickups = Map.empty[FloorNumber, Set[FloorNumber]]
    
    //when
    val (resFloor, resDirection, resGoals, resPickups) =
      move(currFloor, direction, goalFloors, pickups)
    
    //then
    resFloor shouldBe FloorNumber(1)
    resDirection shouldBe Up
    resGoals shouldBe goalFloors
    resPickups shouldBe pickups
  }
  
  it should "move and change direction from Up to Down" in {
    //given
    val currFloor = FloorNumber(0)
    val direction = Up
    val goalFloors = Set(FloorNumber(-2))
    val pickups = Map.empty[FloorNumber, Set[FloorNumber]]
    
    //when
    val (resFloor, resDirection, resGoals, resPickups) =
      move(currFloor, direction, goalFloors, pickups)
    
    //then
    resFloor shouldBe FloorNumber(-1)
    resDirection shouldBe Down
    resGoals shouldBe goalFloors
    resPickups shouldBe pickups
  }
  
  it should "move Up to the next floor and pick up/drop off" in {
    //given
    val currFloor = FloorNumber(0)
    val direction = Up
    val goalFloors = Set(FloorNumber(1))
    val pickups = Map(
      FloorNumber(0) -> Set(FloorNumber(1)),
      FloorNumber(1) -> Set(FloorNumber(2))
    )

    //when
    val (resFloor, resDirection, resGoals, resPickups) =
      move(currFloor, direction, goalFloors, pickups)

    //then
    resFloor shouldBe FloorNumber(1)
    resDirection shouldBe direction
    resGoals shouldBe Set(FloorNumber(2))
    resPickups shouldBe Map(
      FloorNumber(0) -> Set(FloorNumber(1))
    )
  }

  it should "move Down to the next floor and pick up/drop off" in {
    //given
    val currFloor = FloorNumber(0)
    val direction = Down
    val goalFloors = Set(FloorNumber(-1))
    val pickups = Map(
      FloorNumber(0) -> Set(FloorNumber(-1)),
      FloorNumber(-1) -> Set(FloorNumber(-2))
    )

    //when
    val (resFloor, resDirection, resGoals, resPickups) =
      move(currFloor, direction, goalFloors, pickups)

    //then
    resFloor shouldBe FloorNumber(-1)
    resDirection shouldBe direction
    resGoals shouldBe Set(FloorNumber(-2))
    resPickups shouldBe Map(
      FloorNumber(0) -> Set(FloorNumber(-1))
    )
  }

  "step" should "do nothing if no goal floors and no pickup requests" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set.empty),
      (ElevatorId(2), FloorNumber(1), Down, Set.empty)
    ))
    simulator.pickups shouldBe Map.empty

    //when
    val result = simulator.step()

    //then
    result should be theSameInstanceAs simulator
  }

  it should "simulate example ride" in {
    //given
    ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(2))),
      (ElevatorId(2), FloorNumber(0), Down, Set(FloorNumber(-2))),
      (ElevatorId(3), FloorNumber(5), Down, Set.empty),
      (ElevatorId(4), FloorNumber(-5), Up, Set.empty)
    ))
      .pickup(FloorNumber(1), FloorNumber(3))
      .pickup(FloorNumber(-1), FloorNumber(-3))
      .assertSimulator(ElevatorSimulator(
        status = List(
          (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1), FloorNumber(2))),
          (ElevatorId(2), FloorNumber(0), Down, Set(FloorNumber(-1), FloorNumber(-2))),
          (ElevatorId(3), FloorNumber(5), Down, Set.empty),
          (ElevatorId(4), FloorNumber(-5), Up, Set.empty)
        ),
        pickups = Map(
          FloorNumber(1) -> Set(FloorNumber(3)),
          FloorNumber(-1) -> Set(FloorNumber(-3))
        )
      ))

      //when & then
      .step()
      .assertSimulator(ElevatorSimulator(List(
        (ElevatorId(1), FloorNumber(1), Up, Set(FloorNumber(2), FloorNumber(3))),
        (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2), FloorNumber(-3))),
        (ElevatorId(3), FloorNumber(5), Down, Set.empty),
        (ElevatorId(4), FloorNumber(-5), Up, Set.empty)
      )))
    
      //when & then
      .step()
      .assertSimulator(ElevatorSimulator(List(
        (ElevatorId(1), FloorNumber(2), Up, Set(FloorNumber(3))),
        (ElevatorId(2), FloorNumber(-2), Down, Set(FloorNumber(-3))),
        (ElevatorId(3), FloorNumber(5), Down, Set.empty),
        (ElevatorId(4), FloorNumber(-5), Up, Set.empty)
      )))
    
      //when & then
      .step()
      .assertSimulator(ElevatorSimulator(List(
        (ElevatorId(1), FloorNumber(3), Up, Set.empty),
        (ElevatorId(2), FloorNumber(-3), Down, Set.empty),
        (ElevatorId(3), FloorNumber(5), Down, Set.empty),
        (ElevatorId(4), FloorNumber(-5), Up, Set.empty)
      )))
  }
}
