package elevator.simulator

import org.scalatest.{FlatSpec, Matchers}

class FloorNumberSpec extends FlatSpec with Matchers {

  it should "fail if FloorNumber < minFloor" in {
    //given
    val floor = -10

    //when
    val e = the[IllegalArgumentException] thrownBy {
      FloorNumber(floor)
    }
    
    //then
    e.getMessage shouldBe s"requirement failed: FloorNumber should be between -9 and 9"
  }
  
  it should "fail if FloorNumber > maxFloor" in {
    //given
    val floor = 10
    
    //when
    val e = the[IllegalArgumentException] thrownBy {
      FloorNumber(floor)
    }
    
    //then
    e.getMessage shouldBe s"requirement failed: FloorNumber should be between -9 and 9"
  }
  
  it should "create FloorNumber" in {
    //when & then
    FloorNumber(-9).value shouldBe -9
    FloorNumber(-1).value shouldBe -1
    FloorNumber(0).value shouldBe 0
    FloorNumber(1).value shouldBe 1
    FloorNumber(9).value shouldBe 9
  }
}
