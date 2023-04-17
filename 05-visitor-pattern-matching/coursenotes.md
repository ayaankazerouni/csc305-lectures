# 05 More on design patterns: Visitor pattern 

## Topics for today

* Project 1 design discussion
* Design patterns overview and composite design review
* Visitor design pattern
* Pattern matching and sealed types 

## Design patterns

**Design patterns** are general, re-usable solutions to commonly occurring problems within a given context in software design. They offer templates to solve problems that can be used in multiple contexts.

For example, last week we talked about the Composite design pattern. Recall that the Composite pattern is useful when you need to provide some functionality for a tree-like structure. It turns out that this "pattern" is applicable to a number of different problem contexts. Some examples that we talked about are:

* Traversing an HTML document
* Reading files and folders in a file system
* Performing tree operations in a [Bintree](https://opendsa-server.cs.vt.edu/OpenDSA/Books/Everything/html/Bintree.html)
* Evaluating an _expression tree_ (like you are doing in Project 1)

In 1994, a group of four authors wrote what was to become a famous book about Design Patterns (titled *Design Patterns*). The book describes three broad classes of Design Patterns:

- Behavorial Patterns: identifying common communication patterns between objects and realizing these patterns
- Structural Patterns: organizing different classes and objects to form larger structures and provide new functionality
- Creational Patterns: provide the capability to create objects based on a required criterion and in a controlled way

The Composite pattern is billed as a "Structural" pattern, because it involves organising your modules into a tree structure (which presumably makes sense for that problem context).

## Visitor design pattern

The **Visitor design pattern** is a "behavioural" pattern. It makes sense when the you need perform some task on all objects in a complex structure (like a graph or a tree). The underlying classes get "visited" by some code which executes on each object in the structure.

At this point, you may wonder about the difference between the Visitor pattern and the Composite pattern. It's true, they're similar in focus and intent. Let's consider an example.

### Example use-case of Visitor Design

(Example from [Refactoring Guru](https://refactoring.guru/design-patterns/visitor))

Suppose you're working on an app that maintains a large graph of geographical information. Each node represents an complex entity in the graph, like a city, sightseeing area, industry, shopping mall, etc. Depending on its type, each node has various details that make up its internal state, but everything is a "node" in the graph.

You're asked to export the entire graph to some format, like XML. This is a pretty common ask: you often want to transmit data in some language-agnostic format so that different subsystems can operate on the same data.

Each different type of node in the graph will need to write out its salient details, meaning that the "export" operation looks different for each node. Moreover, the export of one node (like a `SightseeingArea`) might lead to the export of its other component nodes (like a `Museum` or a `Landmark`). So, like we did with Composite design, we could make use of polymorphism and recursion to implement an "export to XML" function for each type of node.

> **DISCUSS** What are some drawbacks of adding the XML export behaviour to the existing graph nodes?

* It requires us to modify an existing, fairly complex data structure that is already in production. Bugs in the new code would impact existing users.
* The graph's primary purpose is model geographic data. An argument can be made that an XML export function would reduce the class's cohesion.
* What if somewhere down the line we wanted to export the graph as JSON, another commonly used format for representing structured data? We would need to further modify the nodes in our graph, further exposing existing uses to potentially buggy behaviour, or even requiring further changes in clients to support the new change.

The **Visitor pattern** helps us _extend_ our graph to give it XML export behaviouri, without _modifying_ it. It lets us adhere to the **Open/closed principle** (the "O" in the SOLID principles of software design).

I like to think about the Visitor design pattern as _the Composite pattern, but from the outside of the object structure instead of inside the object structure_.

A nice added benefit of not coupling your extension to the entire object structure is that you can use the Visitor pattern when some action makes sense for only _some_ objects in the larger structure, but not _all_.

We'll go over the basic structure of the Visitor pattern and then look at a real-world example.

### Implementing the visitor design pattern

There are 5 pieces involved in implementing the visitor pattern

1. **`Element`s**. these are the objects that make up the complex structure for which you want to accomplish some task. E.g., nodes in your expression tree, locations in our geographical graph, etc. Ideally, the nodes in the object structure are extensions or implementations of a common `Element` interface.
2. The `Element` interface must have a method to "accept" a visitor, and each subtype of `Element` must implement this method.


```java
public interface Element {
  void accept(Visitor visitor);
}
```

```java
public class SightseeingArea implements Element {
  // location-specific stuff...

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
} 
```

3. **A  `Visitor` interface**. The `Visitor` has abstract (unimplemented) methods to visit each possible type of node. I.e., in the geographical graph example, the `Visitor` might look something like this:

```java
public interface Visitor {
  void visit(SightseeingArea node);
  void visit(Museum node);
  void visit(Landmark node);
  // ... overloaded for all types of nodes 
}
``` 

> **DISCUSS** If we have `default` methods in Java, why can't we fully implement the `accept` method in the `Element` interface itself?

4. **Concrete visitor**. Now you have the machinery in place to perform _some arbitrary operation_ on _all or some nodes in an object structure_. In our running example, that "arbitrary" operation is to export the node to an XML string. We can write a concrete visitor class to do this:

```java
public class XMLExportVisitor implements Visitor {
  public void visit(SightseeingArea node) {
    // export the SightseeingArea 
  }

  public void visit(Museum node) {
    // export the Museum 
  }

  public void visit(Landmark node) {
    // export the Museum 
  }
}
```

> **DISCUSS** What if, in a particular Visitor, I only care about visiting some types of nodes and not others? Currently, I would need to implement a bunch of "no-op" methods because I'm forced to implement them by the `Visitor` interface. 

5. **Client**. With the above machinery in place, the client can kick off a visitor to perform some operation on the object structure.

```java
Visitor visitor = new XMLExportVisitor();
for (Element current : this.locations) {
    current.accept(visitor);
}
```

### A real-world example

A few years ago, I wrote code to traverse Java projects and emit some data for analysis. I used the Eclipse Java Development Tools (JDT) API to parse students' code into [Abstract Syntax Trees](https://astexplorer.net/) (ASTs), and then "visited" certain nodes of interest in these resulting trees.

Because I used the JDT API to create the object structure (the AST), I only had to write the Visiting code. See [this file](https://github.com/ayaankazerouni/incremental-testing/blob/master/src/visitors/ast/MethodASTVisitor.java) as an example of a visitor.

In summary, the code visits `MethodDeclarations` and `MethodInvocations`, i.e., all the places methods are defined or called in the codebase, because that's what I was interested in for that particular analysis. Some things to note:

* The `MethodASTVisitor` has state of its own. It is compound object in its own right that maintains its own state across visits.
* I am not overriding the `visit` method for _all_ possible types of nodes. That's because, unlike the example's we've talked about thus far, the `ASTVisitor` class I'm extending is not an interface, but an `abstract` class. It defines empty `visit` methods for all the different types of nodes available, and I only need to override the ones I want to use. (With the addition of `default` methods to interfaces, the `ASTVisitor` no longer needs to be an `abstract` class.)
* The `visit` methods are returning booleans; they are not `void` methods. The structure of an `ASTNode` is such that it can be broken down further into further `ASTNode`s (much like a composite tree). The return value basically tells the objects whether they should "go further" with this visitor or end the path at this node. If everything returns `true`, then the entire AST gets visited, which may be a waste.

### Visitor pattern using new, experimental Java features 

In my personal opinion, the Visitor pattern is one of the more clunky design patterns to implement in Java. This is mostly due to a lack of expressive language constructs.

Thankfully, _pattern matching_, a feature common in functional languages like OCaml, [is coming to Java soon](https://openjdk.org/jeps/441). The Visitor pattern can be expressed much more concisely using this language construct in addition to another new-ish feature in Java, _sealed types_.

I'll go over a detailed example in class, but this (bombastically named) blog post offers a good explanation for background reading: [_Visitor Pattern Considered Pointless — Use Pattern Switches Instead_](https://nipafx.dev/java-visitor-pattern-pointless/) by Nicolai Parlog (apologies for the hard-to-read page styling).

The use of pattern matching doesn't render the Visitor pattern "pointless": it just changes what it looks like in code. As we talk about more design patterns, remember that they can exist both within and without Java-specific features like interfaces and abstract classes. Design patterns are _ideas_, _templates_ to help solve or think about software problems, and they can often exist agnostic of the programming language.

 

