package elevator.simulator

import elevator.simulator.Direction._
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
      (ElevatorId(1), FloorNumber(0), Up, List(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(-1), Down, List(FloorNumber(-2))),
      (ElevatorId(3), FloorNumber(-1), Up, List(FloorNumber(-1)))
    )
    
    //when
    val result = ElevatorSimulator(initialState)
    
    //then
    result.status shouldBe initialState
  }
  
  "pickup" should "return new instance, but not change status" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, List(FloorNumber(1))),
      (ElevatorId(2), FloorNumber(-1), Down, List(FloorNumber(-2)))
    ))
    
    //when
    val result = simulator.pickup(FloorNumber(0), FloorNumber(1))
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe simulator.status
  }
  
  "step" should "do nothing if no goal floors and no pickup requests" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, Nil),
      (ElevatorId(2), FloorNumber(1), Down, Nil)
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
      (ElevatorId(1), FloorNumber(0), Up, List(FloorNumber(2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(1), Up, List(FloorNumber(2)))
    )
  }
  
  it should "simulate move Down to the next floor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Down, List(FloorNumber(-2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(-1), Down, List(FloorNumber(-2)))
    )
  }
  
  it should "change direction from Down to Up and move to the next floor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Down, List(FloorNumber(2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(1), Up, List(FloorNumber(2)))
    )
  }
  
  it should "change direction from Up to Down and move to the next floor" in {
    //given
    val simulator = ElevatorSimulator(List(
      (ElevatorId(1), FloorNumber(0), Up, List(FloorNumber(-2)))
    ))
    
    //when
    val result = simulator.step()
    
    //then
    result should not be theSameInstanceAs(simulator)
    result.status shouldBe List(
      (ElevatorId(1), FloorNumber(-1), Down, List(FloorNumber(-2)))
    )
  }
}
