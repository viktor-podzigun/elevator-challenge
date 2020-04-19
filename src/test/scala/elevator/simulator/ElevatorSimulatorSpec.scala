package elevator.simulator

import elevator.simulator.Direction._
import elevator.simulator.ElevatorSimulator._
import org.scalatest.{FlatSpec, Matchers}

class ElevatorSimulatorSpec extends FlatSpec with Matchers {

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

  it should "add pickupFloor to goalFloors and return new instance" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1)))
    ))
    simulator.pickups shouldBe Map.empty
    
    //when
    val result = simulator.pickup(FloorNumber(2), FloorNumber(3))
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1), FloorNumber(2)))
    )
    result.pickups shouldBe Map(
      FloorNumber(2) -> FloorNumber(3)
    )
  }
  
  it should "choose closest elevator of same direction" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(1), Down, Set.empty),
      (ElevatorId(3), FloorNumber(1), Down, Set(FloorNumber(0)))
    ))
    simulator.pickups shouldBe Map.empty
    
    //when
    val result = simulator.pickup(FloorNumber(2), FloorNumber(3))
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(1), Down, Set(FloorNumber(2))),
      (ElevatorId(3), FloorNumber(1), Down, Set(FloorNumber(0)))
    )
    result.pickups shouldBe Map(
      FloorNumber(2) -> FloorNumber(3)
    )
  }
  
  it should "choose closest elevator of opposite direction" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Down, Set(FloorNumber(-1))),
      (ElevatorId(2), FloorNumber(1), Down, Set(FloorNumber(-2)))
    ))
    simulator.pickups shouldBe Map.empty
    
    //when
    val result = simulator.pickup(FloorNumber(2), FloorNumber(3))
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(0), Down, Set(FloorNumber(-1), FloorNumber(2))),
      (ElevatorId(2), FloorNumber(1), Down, Set(FloorNumber(-2)))
    )
    result.pickups shouldBe Map(
      FloorNumber(2) -> FloorNumber(3)
    )
  }
  
  "step" should "do nothing if no goal floors and no pickup requests" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set.empty),
      (ElevatorId(2), FloorNumber(1), Down, Set.empty)
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should be theSameInstanceAs simulator
    result.status shouldBe simulator.status
  }
  
  it should "simulate move Up to the next floor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(1), Up, Set(FloorNumber(2)))
    )
  }
  
  it should "simulate move Down to the next floor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Down, Set(FloorNumber(-2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(-1), Down, Set(FloorNumber(-2)))
    )
  }
  
  it should "change direction from Down to Up and move to the next floor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Down, Set(FloorNumber(2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(1), Up, Set(FloorNumber(2)))
    )
  }
  
  it should "change direction from Up to Down and move to the next floor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(-2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(-1), Down, Set(FloorNumber(-2)))
    )
  }
  
  ignore should "pick person when going Up if same direction" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(2)))
    )).pickup(FloorNumber(1), FloorNumber(3))
    simulator.status shouldBe List(
      (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(2), FloorNumber(3)))
    )
    simulator.pickups shouldBe Map(
      FloorNumber(1) -> FloorNumber(3)
    )
    
    //when & then
    val result1 = simulator.step()
    result1.status shouldBe List(
      (ElevatorId(1), FloorNumber(1), Up, Set(FloorNumber(2), FloorNumber(3)))
    )
    
    //when & then
    val result2 = result1.step()
    result2.status shouldBe List(
      (ElevatorId(1), FloorNumber(2), Up, Set(FloorNumber(3)))
    )
    
    //when & then
    val result3 = result2.step()
    result3.status shouldBe List(
      (ElevatorId(1), FloorNumber(3), Up, Set.empty)
    )
    
    //finish
    result2.step() should be theSameInstanceAs result2
  }
}
