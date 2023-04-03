# 3 Mutability, SOLID principles 

## Topics

* A bit more about encapsulation
* Mutability
* Cohesion and the single responsibility principle

## A bit more on encapsulation

**EJ15** Minimise the accessibility of classes and members

Different access modifiers:

* `private`
* package private (no keyword)
* `protected`
* `public`

Instance fields of public classes should rarely be public. Some exceptions exist, like the [`Point`](https://docs.oracle.com/en/java/javase/19/docs/api/java.desktop/java/awt/Point.html) and [`Dimension`](https://docs.oracle.com/en/java/javase/19/docs/api/java.desktop/java/awt/Dimension.html) classes in the Java standard library.A

This opens up the `Dimension` class in particular (used to encapsulate the 2d dimensions of a component in, say, a desktop application) to some potential issues. For example, the Javadoc for says the following:

> Normally the values of width and height are non-negative integers. The constructors that allow you to create a dimension do not prevent you from setting a negative value for these properties. If the value of width or height is negative, the behavior of some methods defined by other objects is undefined.

Since the `Dimension` class has public, _mutable_ fields for `width` and `height`, they can be freely changed by clients, with no validation done on the incoming values.
Ideally, we'd want to use mutator ("setter") methods to mutate these values, or better yet, to return a new object with the new values. This way, we can check the new values for validity, and throw a decidedly "defined" exception instead of simply saying the behaviour is undefined.

For the `Point` class, an argument can be made that the class is a simple container for two integer values---x and y coordinates. Making them `private` and exposing them through getters and setters would be overkill, especially since any input validation is already done by the type system (i.e., x and y can only be `int`s). If you need more validation (e.g., to only allow positive integers), you can create a new abstraction to handle that.

Note also that the `Point` Javadoc doesn't say that it's specifically a point representing a location in desktop windows---if that were the case, then we _would_ only want to allow positive numbers. But that's not part of the `Point` _contract_, so it's not something we can or should rely on. This is as opposed to the `Dimension` Javadoc, where negative numbers are possible even though they would not make sense in that context. 

## Mutability

When something is _mutable_, that means it is possible for it to change or be changed. In the example above, the `width` and `height` fields in the `Dimension` class are mutable, since they are not declared to be `final` fields. The same goes for the `Point` class's `x` and `y` values.

This means that `Dimension` and `Point` objects are themselves mutable, since their internal state is mutable.

In contrast, the `String` class is _immutable_. Once a `String` has been created, it cannot be changed.

```java
String myString = "CSC 305 Individual Software Desine and Development";
myString.replace("Desine", "Design");

System.out.println(myString); // prints "CSC 305 Individual Software Desine and Development"
```

In the code above, the `String::replace` method call returns a _new_ `String` with the transformation applied. If we wanted to store that new value, we would need to re-assign the `myString` variable to that returned value.

**EJ17: Minimise mutability**

An immutable class is a class whose instances cannot be modified. You can achieve this by not providing methods that modify the instance variables of a class, and ensure that the class cannot be extended (declare it as a `final class`).

**DISCUSS**: Critique the following code. The class `Person` is meant to be an immutable class.

**`Person.java`**

```java
public class Person {
  private final String name;
  private Date dateOfBirth;

  public Person(String name, Date dateOfBirth) {
    this.name = name;
    this.dateOfBirth = dateOfBirth;
  }

  public String getName() {
    return this.name;
  }

  public Date getDateOfBirth() {
    return this.dateOfBirth;
  }
}
```

Have we successfully made it immutable? A few changes need to be made:

* If we mean for the fields to be immutable, it's good to signal that by marking them both as `final`. We haven't provided setter methods for either of the `private` vars, so this isn't a huge deal.
* But more insidiously, the `Date` object in Java is _not_ immmutable. That means that, with access to the `dateOfBirth` reference, a client could use the mutator methods in the `Date` class to change its value! And our `Person` is no longer immutable.
  - note that marking it as `final` only prevents the `dateOfBirth` variable from being assigned a new value. it doesn't prevent one from calling mutator methods on it. 

```java
public static void main(String[] args) {
  Person p = new Person("Steph Curry", new Date());

  Date dateOfBirth = p.getDateOfBirth();
  dateOfBirth.setTime(0);
}
```

**DISCUSS** How would you account for the `Date` class's mutability if you wanted to make the `Person` class immutable?

**EJ50 Make defensive copies of mutable objects when needed.** 

```java
public Date getDateOfBirth() {
    return new Date(dateOfBirth.getTime()); 
}
```

### What's the point of mutability?

There are many benefits to creating immutable objects.

* **Changes become visible**. You can read code and reason about the chain of events that occur from a sequence of function calls, resting assured that there were no invisible changes that occurred within those functions. This can make program comprehension significantly easier, which in turn simplifies things like debugging, contributing to an existing codebase, or refactoring.  
* **Concurrency becomes safer**. If objects are immutable, they cannot be corrupted by competing threads. Algorithms operating on immutable objects are more easily parallelisable, since the object is guaranteed to always be in a "valid" state, i.e., it hasn't been inappropriately modified by multiple clients who are unaware of each other.
* **Composability**. You can chain or "compose" functions together to solve larger problems. For example, consider the `String` class. Because each method returns a copy of the `String`, you can chain function calls:

```java
String myString = "    this is a string   ";
System.out.println(myString.trim().toUpperCase()); // prints "THIS IS A STRING"
```

This may seem like a small benefit, but when functions are defined at the right level abstraction, you can compose them to solve problems of ever-increasing complexity. For example, consider the `map`, `filter`, and `reduce` patterns.

One drawback of immutability is _performance_. It can be costly to create a new Object each time some changes have to be made. This cost tends to be overstimated in most cases since optimisations in the programming language can absorb most of the cost (by not actually copying data that doesn't change, advanced garbage collection techniques, etc.). But in programs with large, complex objects that frequently change state (e.g., characters in games) immutability may be too expensive to justify.

## Cohesion

The degree to which a module (class, function, package) has a single, well-focused responsibility. A module should _do one thing_. What does "one thing" mean in this context? It is highly dependent on the problem you're solving and abstractions you've chosen to help you.

Why are cohesive modules good?

* For starters, a module that does one thing is much easier to reason about than a module that handles several pieces of unrelated logic.
* If functionality is appropriately isolated in a module, it becomes easier to test. That is, you can test for individual requirements without other requirements getting in the way. 

For example, consider a text-based Tic Tac Toe game you're making for Lab 2. A solution with **highly cohesive** modules might have classes that handle the following responsibilities, _separately_. 

* Managing the board state. 
* Keeping track of the state of the current game. 
* Managing the user interface. That is, handling all interactions with the user.
* Managing communication between the UI and the game.

A solution with **low cohesion** might tightly _couple_ two or more of the above modules together. For example, to handle the job

> "place `X` at the top-right corner"

a low-cohesion solution might place the `X` and save that information, handle the update of the user interface, and check whether the user has won the game or not. This displays low cohesion. The function is doing at least three things.


Moreover, future changes are rendered difficult with this design. Suppose you wanted to replace the text-based user interface with a graphical user interface. Instead of swapping out the UI (like the Formula 1 pit crew swaps out the front/rear wing), you now need to fiddle with the Tic Tac Toe game logic as well.

Does this all sound a lot like the arguments we made against tight _coupling_ last week? Good! The two ideas of coupling and cohesion go hand-in-hand. A way to reduce coupling between modules is to ensure that individual modules have a _single responsibility_.

This is so important that this marks the first of the **SOLID** principles---a set of five principles for good software design. (The way the principles are phrased is regrettably, ahem, coupled with the object-oriented paradigm, but the ideas are broadly applicable in general software design.)

Remember: **Loose Coupling, Tight Cohesion**

## An example in the wild

Consider the following chapter written by [James Crook](http://aosabook.org/en/intro1.html#crook-james) about the **[user interface of Audacity](http://aosabook.org/en/audacity.html)**, a popular open-source application for sound recording and mixing.

The chapter is pretty long and worth a read in its entirety[^aosa], but we'll focus on section 2.4 _The TrackPanel_.

[^aosa]: The book it's from&mdash;_The Architecture of Open-Source Applications_, edited by Amy Brown and Greg Wilson&mdash; is pretty cool because it contains descriptions of the designs of large mature open-source projects written by the project maintainers themselves.

**DISCUSSION**: Take a moment to read the referenced section.

The author is describing exactly the kind of problem we discussed with the Tic Tac Toe example. Namely, part of the user interface are deeply coupled with the domain-specific logic that is being implemented. The reasons for this choice are related to other, earlier tradeoffs regarding a third-party library (wxWidgets).

The Summary section puts it nicely:

> In the TrackPanel of Audacity we needed to go outside the features that could easily be got from existing widgets. As a result we rolled our own ad hoc system. There is a cleaner system with widgets and sizers and logically distinct application level objects struggling to come out of the TrackPanel.

