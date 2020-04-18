
[![Build Status](https://travis-ci.org/viktor-podzigun/elevator-challenge.svg?branch=master)](https://travis-ci.org/viktor-podzigun/elevator-challenge)
[![Coverage Status](https://coveralls.io/repos/github/viktor-podzigun/elevator-challenge/badge.svg?branch=master)](https://coveralls.io/github/viktor-podzigun/elevator-challenge?branch=master)

## elevator-challenge
elevator coding challenge

### How to build/run it

To build and run all the tests use the following command:
```bash
sbt test
```

### How to use it

```scala
trait ElevatorControlSystem {

  def status(): Seq[(Int, Int, Int)]

  def pickup(floor: Int, direction: Int)

  def step()
}
```
