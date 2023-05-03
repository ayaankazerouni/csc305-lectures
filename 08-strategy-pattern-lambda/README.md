# 8 Strategy and command design patterns

Reading:

* [Strategy design pattern](https://refactoring.guru/design-patterns/strategy)
* [Command design pattern](https://refactoring.guru/design-patterns/command)

## Strategy design pattern

### Example: A routing application  

Suppose you're working on a maps application (kind of like Google Maps or Apple Maps).
Your application has a graph that represents geographic locations, and it allows the user to request a _driving route_ from point A to point B.
So put simply your app can do something like this:

```java
List<Point> path = this.pathfinder.computePath(graph, source, dest);
``` 

The `computePath` function above finds a driving path from `source` to `dest` and life is good.
You now get a request that the app should also allow people to request _walking_ paths or _biking_ paths. 
You could implement these as separate methods, e.g.,

```java
this.pathfinder.computeWalkingPath(...)

this.pathfinder.computeBikingPath(...)
```

But this can get unwieldy---your pathfinder object is now supporting three behaviours that are, by definition, distinct from each other.
This may indicate a lack of cohesion.
Worse, any clients that rely on this pathfinder would have to now have code added to them that can optionally call the correct path finding algorithm.
Effectively, each new pathfinding strategy you support will also result in a bunch of additions to other classes that need to consume the pathfinding feature.

_We are violating the open/closed principle_. See previous course notes and try to work out how this violates that principle.

The **Strategy design pattern** tries to solve this problem. It is useful when you have a class that accomplishes a _specific task_ in a lot of different ways, and extract these different algorithms into their own routines are called _strategies_.

It requires a few pieces:[^guru]

* The `Strategy` interface: This is the common interface to which all your individual strategies will adhere. In this case you might have a `PathingStrategy` interface with a `computePath` abstract method.
* Concrete strategy implementations: You could have several classes implement the `PathingStrategy` interface. So for example, `DrivingStrategy`, `WalkingStrategy`, `PublicTransportStrategy` would all have their own `computePath` implementations.
* The `Client`: This is the consumer of the various strategies. The client initialises a `new` concrete strategy and uses their `computePath` method to achieve this task. Exactly which strategy object is initialised is based on decisions made at runtime (e.g., the user selects a specific strategy). What's important is that the client's code doesn't change when the chosen strategy changes.

[^guru]: Note that the Refactoring Guru page on this pattern includes a fourth piece: the `Context`. This sits in between the `Client` and the `Strategy` interface. I...don't know why they recommend this. There is such a thing as too much abstraction. In this case, the `Context` class adds (what I think is) a needless layer between the `Client` and the `Strategy` interface, so I recommend ignoring that part.

So the strategy pattern helps us go from having a bunch of different methods (`computeDrivingPath`, `computeWalkingPath`, `computeBikingPath`) in the same class to having one method implemented (`computePath`) in a few different ways. You might be wondering why the latter way is deemed preferable.

With dependency injection, the `Client` is able to be totally decoupled from the specific strategy it is using. Without the common `StrategyInterface` binding it all together, each new strategy added in the future would require code added in various points in the `Client` (everywhere the strategy was used). This problem becomes compounded when the "strategies" have multiple behaviours (e.g., route finding, but also things like checking tolls or estimating time).

When to use the strategy design pattern:

* When you have multiple ways of accomplishing a task and you want to be able to swap between those ways at runtime.
* You want to isolate details of how an algorithm works from the client that uses it. 

Cons of using this pattern:

* It can be overkill for just a couple of strategies. That's why in project 2 I've suggested that you can simply use a `Function` object whose value is determined programmitically, instead of creating a `Parser` interface with two separate implementations for prefix and postfix notation.
* Refactoring guru says that another con is that clients must be aware of the differences between strategies in order to select the right one. I don't think this is a con. This is just the cost of doing business—we wouldn't have different strategies if there weren't some difference between them that is noticeable to _someone_.

## Command design pattern

The **Command design pattern** is used to turn a "request" into a standalone object. I'm not going to talk too much about details here because this pattern mostly tries to bring "functions as values" to languages that don't have that ability.

The classical Command pattern requires two pieces:

* A `Command` interface that has an `execute` abstract method.
* Concrete implementations that implement the `execute` method.

Voila! You have a function that can be stored in a variable, given to other functions, etc. This is what lambdas let you do in Java. Just use lambdas.

In fact, this is how lambdas are implemented in Java. Each lambda is really an instance of a _functional interface_; a Java interface that has only one abstract method (e.g., `Function`, `Predicate`, `Consumer`, `Comparator`). The only difference is instead of creating a whole new class that implements the interface, just to implement that one function, you use the lambda syntax to concisely implement just the function (which is usually all you care about anyway).

### Undo-able operations

One benefit of using the Command pattern (other than all the benefits of functions as values) is that you can support things like **undo-able operations**. Imagine a function that could _applied_ as well as _reversed_. You can do this in two ways.

First, each command simply saves a copy of its input. That way its "undo" operation is to return give back that old version (before the `execute` function was applied). This is not always feasible.

Another way is to define the `Command` interface with an `execute` method and also an `undo` method. That way each "command" that you create must also define its reverse operation.

In either case, you wouldn't just use a lambda, since each "command" requires _two_ functions, not just one.
This is a common operation for _database migrations_. A database migration is when some change is made to the schema of a database. For example, suppose we're maintaining Cal Poly's student management system, and we want to add a field to the `Student` table, indicating whether they first enrolled in Cal Poly on the quarter system or the semester system.

In production databases that are in active use and already have live data, a migration is not as simple as tacking on the new column. You need to define what should be done for existing rows in the table. This is what would be defined in the `execute` function. You also need to write a "rollback" operation, because you want this change to be _atomic_. Your table has millions of rows; if the `execute` function fails for some row in the middle, you don't want to quit now with your table in two inconsistent states. So your `undo` function tells the table how to roll back the change.

Undo-able operations are not always possible to support. For example, the old states might be too large to feasibly save for each command. Or the undo operation might be impossible to implement (e.g., encryptions).




