package elevator.simulator

import org.scalatest.{FlatSpec, Matchers}

class ElevatorIdSpec extends FlatSpec with Matchers {

  it should "fail if ElevatorId <= 0" in {
    //given
    val id = 0

    //when
    val e = the[IllegalArgumentException] thrownBy {
      ElevatorId(id)
    }
    
    //then
    e.getMessage shouldBe "requirement failed: ElevatorId should be between 1 and 16"
  }
  
  it should "fail if ElevatorId > maxId" in {
    //given
    val id = 17
    
    //when
    val e = the[IllegalArgumentException] thrownBy {
      ElevatorId(id)
    }
    
    //then
    e.getMessage shouldBe "requirement failed: ElevatorId should be between 1 and 16"
  }
  
  it should "create ElevatorId" in {
    //given
    val id = 1
    
    //when & then
    ElevatorId(id).value shouldBe id
  }
}
