
[![Build Status](https://travis-ci.org/viktor-podzigun/elevator-challenge.svg?branch=master)](https://travis-ci.org/viktor-podzigun/elevator-challenge)
[![Coverage Status](https://coveralls.io/repos/github/viktor-podzigun/elevator-challenge/badge.svg?branch=master)](https://coveralls.io/github/viktor-podzigun/elevator-challenge?branch=master)

## elevator-challenge
elevator coding challenge

### How to build/run it

To build and run all tests use the following command:
```bash
sbt test
```

### How to use it

Elevator simulation interface is the following:

```scala
trait ElevatorControlSystem {

  def status(): Seq[(ElevatorId, FloorNumber, Direction, Seq[FloorNumber])]

  def pickup(pickupFloor: FloorNumber, goalFloor: FloorNumber): ElevatorControlSystem

  def step(): ElevatorControlSystem
}
```

* To query the status - call `status()` method, it returns sequence
of elevators states with the following data:
`elevatorId`, `currFloor` and `goalFloors` for each elevator
in the system

* To request a pickup - call `pickup()` method, it accepts
two parameters: `pickupFloor` and `goalFloor`, the direction
is the following:
  * `pickupFloor` < `goalFloor` - going up (`+`)
  * `pickupFloor` > `goalFloor` - going down (`-`)
  
* By calling `step()` method the system will simulate `time-stepping`:
  * elevator that is empty (no goal floors) - is unchanged
  (only if there is no new pickup requests)
  * elevator that is between floors - going to the next floor
  (`currFloor` `+`/`-` 1)
  * for elevator that is on either `pickupFloor` or `goalFloor`
  the system will either `add` or `remove` goal floors accordingly

* Since both `pickup()` and `step()` methods alter system state
  they both return new `ElevatorControlSystem` instance,
  the old instance remains `unchanged`.
  
* You can chain several calls:
  ```scala
  controlSystem
    .pickup(...)
    .pickup(...)
    .step()
    .step()
    .status()
  ```

#### Setting initial state

To set initial state - call `ElevatorSimulator` constructor:
```scala
val controlSystem = ElevatorSimulator(List(
  (ElevatorId(1), FloorNumber(0), Up, Set(FloorNumber(1))),
  (ElevatorId(2), FloorNumber(-1), Down, Set(FloorNumber(-2))),
  ...
))
```
**Notes**:
* at least `one` elevator should be provided, maximum is `16`
* `FloorNumber` should be between `-9` and `9`
* for simplicity elevators has `unlimited` capacity
  (number of people)
