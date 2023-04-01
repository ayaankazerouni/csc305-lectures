# 3 Mutability, cohesion, single-responsibility

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

In contrast, the `String` class is _immutable_. Once a `String` has been created, it cannot be changed.

```java
String myString = "CSC 305 Individual Software Desine and Development";
myString.replace("Desine", "Design");

System.out.println(myString); // prints "CSC 305 Individual Software Design and Development"
```

In the code above, the `String::replace` method call returns a _new_ `String` with the transformation applied. If we wanted to store that new value, we would need to re-assign the `myString` variable to that returned value.

**EJ17: Minimise mutability**

An immutable class is a class whose instances cannot be modified. You can achieve this by not providing methods that modify the instance variables of a class, and ensure that the class cannot be extended (declare it as a `final class`).

**DISCUSS**: Critique the following code. The class `Person` is meant to be an immutable class.

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

Have we successfully made it immutable? A couple of changes need to be made:
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

* simple, easy-to-reason about code
* concurrency

Drawbacks

* can be costly to keep creating new objects (though this cost tends to be [overestimated](https://docs.oracle.com/javase/tutorial/essential/concurrency/immutable.html))

