# 4 (some more) SOLID principles, polymporhism 

## Reminders

First quiz on Thursday taken in lab.

## What we've talked about so far

So far, we've talked about the following principles for good program design:

* Minimise the scope of local variables
* Use `StringBuilder`
* Use the appropriate looping construct
* Know and use standard libraries
* Design and build loosely coupled modules
* Use strategies like encapsulation, interfaces, and dependency injection 
* Design immutable classes wherever possible
* Increase cohesion of individual classes

We've also started talking about the so-called "SOLID" principles of object-oriented design. We started by talking about the Single Responsibility Principle (the S in SOLID), which is the principle that guides our desire for LOOSE COUPLING between classes and TIGHT COHESION within classes.

## Open/Closed Principle

The Open/Closed Principle states that software entities should be open to extension, but closed to modification.

"Open to extension"

- Software design will change over time
- We should leave our code open to extensions due to changes in underlying requirements

"Closed to modification"

- Every entity or module should know how to do what it is designed to do
- If the behaviour changes, we should not change how the entity works, we should instead extend it

A good example is the regular expression example we saw last week. The `usePattern` function is closed to modification (i.e., we won't make changes to the function), but is open to extension (i.e., it can be extended to work with any regular expression parser, as long as it adheres to the `RegExp` interface).

## Polymorphism

**PONDER** Do you remember what Polymorphism is? Why is it useful?

Polymorphism is an important pillar of object-oriented programming. The word "polymorph" means "many forms". Polymorphism allows us to treat objects as having one of multiple "forms", and we don't necessarily know until runtime what that form might be. (This should remind you of interfaces!)

**DISCUSS** What different kinds of polymorphism are available to us in Java?

* Interfaces
  * `default` methods allow us to add shared method implementations to interfaces. These methods are inherited by any implementing subclasses that don't implement their own versions; if `default` methods are added to interfaces that have been in use by many clients, this can lead to subtle issues at runtime, even if the client's code compiles successfully.
* Extending a concrete class: a class extends another concrete class. Both the super-class *and* the sub-class are concrete, initialisable classes. The subclass may inherit the superclass's behaviour, modify it, or add to it.
* Abstract classes: An abstract class can define concrete methods as well as abstract methods that *must* be implemented by a subclass.

**EJ20: Prefer interfaces to abstract classes.**

It used to be that interfaces were quite limited in what they could do, compared to abstract classes. Interfaces could _only_ define abstract methods that all implementing subclasses had to implement. We've already talked about the benefits of this (see lecture notes on coupling and cohesion).

But this led to difficulties when, for example, an interface that was in use by many classes needed to be extended in some way. Any additions of abstract methods to the interface would require _all_ implementing subclasses to also need implementations of the new abstract methods, _even if the implementation was to be identical for all subclasses_.

Compare this to abstract classes, which allow a mix of fully implemented methods as well as abstract methods. All subclasses _must_ implement their own versions of abstract methods, but have the option to inherit the methods that are already implemented in the superclass.

Clearly, they seem more useful than interfaces!

**Enter `default` methods**

All of this changed with the introduction of `default` methods for interfaces. Default methods allow you provide implementations for certain behaviours in the interface itself, so that implementing classes can inherit them or override them.

As a result, using interfaces give you the following benefits:

* **Existing classes can easily be retrofitted to implement a new interface.** It's just a matter of declaring that the class `implements` the interface, and adding the required methods. Because classes in Java can implement multiple interfaces, this is not a problem. However, a Java class can `extend` at most one class. So it's not straightforward at all to retrofit an existing class to extend an abstract class.

* **Interfaces let you create non-hierarchical type frameworks.** Not all organisations lend themselves to tree structures. That is, you may want different combinations of types "mixed together" for specific subclasses. To achieve this flexibly with abstract classes, you would end up with a bloated class hierarchy, trying to create a separate type for each combination of functionality you want to support. With interfaces you have infinite flexibility to enhance class behaviours as needed. 

* **It's easy to enhance implementing subclasses behaviours by adding `default` methods**

For example, consider the [`Comparable`](https://github.com/openjdk/jdk20/blob/master/src/java.base/share/classes/java/util/Comparator.java) interface. In older versions of Java, the interface simply provided an abstract `compare` method that compared two objects. Implementing subclasses had to implement those methods. Now, the `Comparator` interface provides a number of useful `default` methods, which allow you to chain comparators together (using `thenComparing`) or to reverse the order of a comparison (using `reversed`).

No implementing classes needed to be aware of these additions to be able to benefit from them.


That said, there are risks with `default` method implementations. Default methods are "injected" into implementing subclasses without the knowledge or consent of the implementors. It is possible that the default method implementation that is being inherited by some implementor actually violates invariants that the implementor depends upon. good documentation is absolutely essential to communicate this information to implementors.

For example, a library maintainer who updated to Java 9 would suddenly have been saddled with a bunch of inherited behaviour in their classes that implement the `Comparable` interface.

_It is simply not possible to write interfaces that maintains all invariants of every conceivable implementation._

**EJ21 Design interfaces for posterity**

The `Collection` interface contains the [`removeIf`](https://github.com/openjdk/jdk20/blob/master/src/java.base/share/classes/java/util/Collection.java#L571) method. The method removes an element if it satisfies some boolean condition (a predicate).

Every class that implements the `Collection` interface (i.e., [a whole ton of classes in the JDK](https://download.java.net/java/early_access/jdk20/docs/api/java.base/java/util/Collection.html)) now inherits this `removeIf` method.

Unfortunately, this fails for the `SynchronizedCollection`, a collection object from Apache commons which synchronizes the collection based on a locking object. The `default` implementation of `removeIf` in the `Collection` interface doesn't know about this locking mechanism. And the `SynchronizedCollection` cannot override the method and provide its own implementation because that would mutate the underlying collection, breaking its fundamental promise to synchronize on each function call. If a client were to call `removeIf` while another thread was modifying the collection, it would lead to a `ConcurrentModificationException` or some other undefined behaviour.

## Liskov substitution principle

Proposed by **Barbara Liskov**, a pioneer of programming languages, object-oriented programming, and winner of the 2008 Turing award.

The LSP says:

> Any class `S` can be used to replace a class `B` if and only if `S` is a subclass of `B`.

This is a good rule-of-thumb for using _polymorphism_ currently.

The Liskov Substitution Principle says that in an OO program, if we substitute a superclass object reference with an object of any of its subclasses, the program should not break. This is in much the same way that code that uses a `List` type can be executed with an `ArrayList` or a `LinkedList` and everything works just fine. 

You can think of the methods defined in a supertype as defining a contract. Every subtype (e.g., everything that claims to be a `List`) should stick to the contract.

**Critique**

```java
public class Bird {
    public void fly() {
        System.out.println("Flying...");
    }

    public void eat() {
        System.out.println("Eating...");
    }
}

public class Crow extends Bird {}

public class Ostrich extends Bird {
    public void fly() {
        throw new UnsupportedOperationException();
    }
}
```

```java
public class TestBird {
    public static void main(String[] args){
        List<Bird> birdList = new ArrayList<Bird>();
        birdList.add(new Crow());
        birdList.add(new Ostrich());
        birdList.add(new Crow());
        letTheBirdsFly ( birdList );
    }

    public static void letTheBirdsFly ( List<Bird> birdList ){
        for ( Bird b : birdList ) {
            b.fly();
        }
    }
}
```

The LSP helps us to ensure that invariants in the superclass are maintained in subclasses (i.e., preconditions and postconditions are satisfied). This can also help clients rely on extensions to our existing classes without fear of unexpected functional outcomes.

In a language like Java, the _existence_ of the appropriate functions (e.g., methods with the right names, parameter lists, and return type) are more-or-less guaranteed by the language's type system. For example, if you were you create a new `List` implementation, your code would not compile until you had implementations for all of the methods that are required by the [`List` interface](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/List.html).

But the LSP goes beyond simply satisfying the type system. It's a promise of _semantically_ fulfilling the contract of the supertype. That is, the subtype should behave like the supertype (e.g., no matter what kind of list is being used, the effect of `add`ing an item is the same).

For example, subclasses can improve the performance of the superclass:

- a subclass can use a better search algorithm than the base class
- a subclass can use a better sort algorithm than the base class
- the expected behaviour and outcome should be the same

Currently, languages do not automatically enforce these properties.

## Design Patterns

A **design pattern** is a general, re-usable solution to a commonly occurring problem within a given context in software design. They offer templates for how to solve problems that can be used in multiple different solutions.

In 1994, a group of four authors wrote what was to become a famous book about Design Patterns (titled *Design Patterns*). The book describes three broad classes of Design Patterns:

- Behavorial Patterns: identifying common communication patterns between objects and realizing these patterns
- Structural Patterns: organizing different classes and objects to form larger structures and provide new functionality
- Creational Patterns: provide the capability to create objects based on a required criterion and in a controlled way


Today, we're going to talk about the **Composite design pattern**, billed as a Structural pattern.[^guru]

[^guru]: Much of these course notes reference the page on the [Refactoring Guru website](https://refactoring.guru/design-patterns/composite).

The composite design pattern makes sense when a portion of your application can be structured as a tree.

For example, suppose you need to "read" all the files in a computer. You have a root folder (the root of your directory tree). That root may have many children (files or folders inside of it). Some of those children may in turn have further children.

The Composite pattern involves you treating the entire structure as a tree (much like your file system does). Then each "node" of the tree might have a `read` operation. For files, the `read` operation simply prints out the contents of the file. For folders, the `read` operation involves further traversing its children. This recursively continues until there are no more files to be read.

**Benefits of this pattern**

- Using polymorphism and recursion, you can work with quite complex tree structures. For example, each folder doesn't need to know if its children are files or folders; they can simply be `read`, because they both belong to some supertype.
- You can introduce new types of "nodes" in this tree conveniently, and the rest of the structure doesn't need to change. For example, consider that our filesystem has a new kind of file (say, that needs to be processed before it can be `read`). You can simply create a new subclass and implement the new `read` method

**Drawbacks**

* According to Refactoring Guru, this is one drawback of this pattern:

>  It might be difficult to provide a common interface for classes whose functionality differs too much. In certain scenarios, you’d need to overgeneralize the component interface, making it harder to comprehend.

Personally, I think the above would be an indication that you shouldn't be using the Composite design pattern in the first place.

