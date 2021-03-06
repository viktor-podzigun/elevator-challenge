
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

  def status(): Seq[(ElevatorId, FloorNumber, Direction, Set[FloorNumber])]

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

### How it's implemented

#### The Challenge

Then challenge is to schedule and process the pickup requests.
There are few different options, though:

1. First requests handled first, `FCFS` (first-come, first-served).
   But, it may lead to both:
   * inefficient elevator usage, and
   * delays of requests
   
   Elevators may go to the far floors first, ignoring more close
   but less priority requests.

2. Schedule requests for elevators that are:
   * the closest to the pickup floor, and
   * going the same direction has a priority

Ofcourse, there are many other possible options and optimisations.
They are not considered here for the simplicity of the solution.

#### The Solution

For handling request in more natural and good enough way,
the `2nd` option was chosen.

The pickup requests are scheduled at the `moment of pickup request`,
by adding `pickupFloor` to the `goalFloors` of the `closest`
elevator with giving priority to the elevators going
the `same direction` as `pickup direction`.

[ElevatorSimulator](src/main/scala/elevator/simulator/ElevatorSimulator.scala) => [tests](src/test/scala/elevator/simulator/ElevatorSimulatorSpec.scala)

#### Example ride

` ` | Pickups | Elevator 1 | Elevator 2
---|---|---|---
`initial`  |     | 0 Up (2) | 0 Down (-2)
`pickup(1, 3)`   | `1` -> (`3`)  | 0 Up (`1`, 2) | 0 Down (-2)
`pickup(-1, -3)` | 1 -> (3), `-1` -> (`-3`) | 0 Up (1, 2) | 0 Down (`-1`, -2)
`step()` |  | `1` Up (2, `3`) | `-1` Down (-2, `-3`)
`step()` |  | `2` Up (3) | `-2` Down (-3)
`step()` |  | `3` Up | `-3` Down
