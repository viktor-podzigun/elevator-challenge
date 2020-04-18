package elevator.simulator

import org.scalatest.{FlatSpec, Matchers}

class ElevatorSimulatorSpec extends FlatSpec with Matchers {

  it should "fail if no elevators are provided" in {
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
      (ElevatorId(1), FloorNumber(0), List(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(-1), List(FloorNumber(2)))
    )
    
    //when
    val result = ElevatorSimulator(initialState)
    
    //then
    result.status shouldBe initialState
  }
  
  it should "return new instance, but not change status when pickup" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), List(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(-1), List(FloorNumber(2)))
    ))
    
    //when
    val result = simulator.pickup(FloorNumber(0), FloorNumber(1))
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe simulator.status
  }
}
