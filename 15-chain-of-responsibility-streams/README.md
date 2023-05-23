# Builder pattern, Streams

**References**:

* [Refactoring guru content on the Builder pattern](https://refactoring.guru/design-patterns/builder) (though, like some of the previous patterns, their suggested structure involves some over-complication)
* [Builder Design Pattern](https://howtodoinjava.com/design-patterns/creational/builder-pattern-in-java/) by Lokesh Gupta.
* [Java Stream documentation](https://download.java.net/java/early_access/jdk20/docs/api/java.base/java/util/stream/package-summary.html)

## Pre-class stuff

* When should I use static methods?
* Project 3 plan activity

## Creating complex objects

Consider the following constructor for a `User` class:

```java
public User (String firstName, String lastName, int age, String phone, String address) {
	this.firstName = firstName;
	this.lastName = lastName;
	this.age = age;
	this.phone = phone;
	this.address = address;
}
```

Suppose we want all the parameters except `firstName` and `lastName`to be optional. That means we want to allow creation of a `User` without a specified age, phone number, address, or some combination of the those fields.

You can certainly support this using constructor overloading:
```java
public User (String firstName, String lastName, int age, String phone, String address) { ... }
public User (String firstName, String lastName, int age) { ... }
public User (String firstName, String lastName, String phone) { ... }
public User (String firstName, String lastName, String phone, String address) { ... }
public User (String firstName, String lastName, String address) { ... }
```

Hopefully you see that providing this large set of constructors is tedious and can be error-prone. If you add more fields to the `User`, like "salary" of "unitsEarned", things will become even more unmanageable.
Essentially, you have three options with this constructor approach, and each one has its issues:

1. You need to know beforehand what "archetypes" of `User` a client is likely to want to create, and provide only those constructors. This information is not always available.
2. Alternatively, you simply provide all possible constructors. This can get out of hand with even a small number of fields.
3. Finally, you could only provide one constructor that takes in *all* fields, and let the client enter `null` values for the fields they don't care about. This is icky, and also expects the client to know what to do about optional primitive fields. (For example, what's "null" for an `int`?)

If you don't like the constructor approaches above (and you shouldn't), another option is to only use the constructor for the required fields (`firstName`, `lastName`), and user setter (mutator) methods for all the other fields. Then we can create `User` objects, and use `setAge`, `setPhone`, `setAddress` etc. to flexibly set the values that we need while creating an object.

**PONDER** Is there a problem with what we've just described? By creating all the setter methods, we avoided the problem of the exploding constructors. But did we create a new problem?
<!-- By adding all the setter methods, we've made our `User` class mutable. Even worse, we only added them in order to *create* the `User`, but now there's all these public methods to mutate it. -->

### Enter the Builder pattern

The goal of the Builder design pattern is to separate the *construction* of complex objects (like our `User`) from their representation.
This allows us to allow, at runtime, the creation of different "forms" of the same object.
E.g., a `User` with a phone and an address, a `User` with an age, but no phone and no address, or a `User` with no age, no phone, and no address.

We accomplish this by introducing a new `Builder` object.
The `Builder`'s job is, like its name suggests, to build an instance of the object in question.

The `Builder` needs the following pieces:

* Private fields for all the instance variables in the object being built. In our example, that would be `firstName`, `lastName`, `age`, `phone`, and `address`.
* A constructor that takes in all the *required* params for the object to be built. So in our running example, that would be the `firstName` and `lastName`.
* Public functions for each of the other fields to be set.
* A `build` function that the client can use to put everything together and create an instance of the object. See the below code as an example.

<small>The code below is adapted from [this blog post](https://howtodoinjava.com/design-patterns/creational/builder-pattern-in-java/) about the Builder Pattern.</small>

```java
public class User {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String phone;
    private final String address;

    // Can be package-private or protected if your Builder is in a separate file.
    private User(String firstName, String lastName, int age, String phone, String address) {
        ...
    }

    // Alternatively, the User constructor can just take in a UserBuilder
    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        ...
    }

    public static class UserBuilder() {
        private final String firstName;
        private final String lastName;
        private int age;
        private String phone;
        private String address;

        // Initialise the required fields.
        public UserBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public UserBuilder age(int age) {
            this.age = age;
        }
        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }
        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }
        // Return the finally constructed User object
        public User build() {
            User user =  new User(this);
            validateUserObject(user);
            return user;
        }

        private void validateUserObject(User user) {
            //Do some basic validations to check
            //if user object does not break any assumption of system
        }
    }
}
```

Note that the Builder's methods for age, phone, and address all return the Builder *itself* after setting each value. This allows the Builder to be used "fluently" by chaining method calls to construct complex objects.

```java
User u1 = new UserBuilder("Frodo", "Baggins")
  .age(33)
  .phone("555-1234")
  .address("Bag End, the Shire, Hobbiton")
  .build();

User u2 = new UserBuilder("Gandalf", "the White")
  .age(2000)
  // no phone
  .address("Everywhere")
  .build();

User u3 = new UserBuilder("Bugs", "Bunny")
  // no age
  // no phone
  // no address
  .build();
```

The `UserBuilder` allows the creation of users with very different internal states.
The order in which fields are assigned doesn't matter.
This flexibility means users can be variably constructed at runtime, without knowing beforehand what type of `User` the client wants to create.

This last point---allowing for different types of objects to be created based on runtime choices---is a fundamental benefit of the builder pattern. We've see an example of it being applied when the "different states" of an object come from the object having various values set or unset.
But it can be applied in other scenarios as well.

For example, consider the [`Comparator`](https://github.com/openjdk/jdk20/blob/master/src/java.base/share/classes/java/util/Comparator.java) interface.
First you can create a Comparator using whatever method you see fit:

```java
// Using a lambda
Comparator<User> ageComp = (u1, u2) -> u1.age - u2.age;

// Using helper methods and method references
Comparator<User> ageComp2 = Integer.comparing(User::getAge);

// Using an anonymous inner class (don't do this)
Comparator<User> ageComp3 = new Comparator<>() {
    @Override
    public int compareTo(User u1, User u2) {
        return u1 - u2;
    }
}
```

However, a client may want to perform various kinds of comparisons on `User` objects, and we have no way of knowing which ones beforehand.
The `Comparator` interface is a good example of the Builder Pattern.
It provides methods like `thenComparing` and `reversed` which allow the client to flexibly construct Comparators. Both methods change the behaviour of the Comparator and return it, allowing one to chain calls together to create different Comparators.

```java
// Sort the list of users by age in descending order, and break ties
// by ordering based on phone number in ascending order.
Comparator<User> ageComp2 = Integer.comparing(User::getAge)
  .reversed()
  .thenComparing(User::getPhone);
```

Finally, another example is the [`StringBuilder`](https://github.com/openjdk/jdk20/blob/master/src/java.base/share/classes/java/util/StringBuilder.java) class. It lets you append text to itself to construct the underlying string, and eventually, you call `toString` to "terminate" the builder and get the constructed object.

## Streams and lambdas

* Refresher about lambdas
  * `Function, Predicate, Consumer`
* The addition of `Stream` was one of the major features added to Java 8.
* `Stream`s are wrappers around a data source, allowing us to operate with that data source and making bulk processing convenient and fast
*  A stream does not store data and, in that sense, is not a data structure. It also never modifies the underlying data source.
* This functionality supports functional-style operations on streams of elements, such as map-reduce transformations on collections. The operations can be composed into "stream pipelines"

### Creating streams

You can create empty streams, or streams from existing data sources like lists.

```java
Stream<String> s1 = Stream.empty(); // empty stream

// Stream of strings
Stream<String> s2 = Stream.of("these", "are", "stream", "contents");

// Stream of strings
List<String> myList = List.of("This", "is", "a", "list", "of", "Strings");
Stream<String> s3 = myList.stream();

// Using the builder pattern
Stream<String> s4 = Stream.<String>builder()
    .add("builder")
    .add("pattern")
    .add("in action")
    .build();
```

Note that if the Stream comes from an existing data source, it does NOT modify that data source, no matter what operations are performed in the Stream.

Beyond simply creating a stream from pre-existing data, you can generate streams by doing other transformations on data:

```java
Random random = new Random();
DoubleStream ds = random.doubles(3); // Stream of 3 doubles
IntStream intStream = IntStream.range(1, 3);
LongStream longStream = LongStream.rangeClosed(1, 3);
```

Finally, you can also create streams out of file contents:

```java
Files.lines(Path.of("file.txt"), Charset.defaultCharset())
    .forEach(System.out::println);
```

This is in contrast to `Files.readAllLines(Path.of("file.txt"))`, which would read all lines into a `List<String>`. This can be time and memory intensive.
The Stream solution loads lines "lazily" and processes them one-at-a-time.

### Stream pipelines 

In general, a stream pipeline contains of:

* **A source**, which can be an array, a collection, a generator function, an I/O channel, etc. We talked about this above.
* **Zero or more intermediate operations**, which transform the stream into another stream. Because these intermediate operations return streams themselves, they can be chained together to perform a number of operations.
* **Exactly one terminal operation**, which produces a result or a side effect. Since the terminal operation "exits" the pipeline, no further stream operations can be added to the pipeline.

Many stream operations take in a *behavioural parameter* (i.e., a function). This can be written inline as a lambda, referred to using a variable that points to a lambda, or using the method reference syntax (e.g., `System.out::println`).
These behavioural parameters represent functions that must be applied to *each item* in the stream.

Earlier in the quarter we talked about various "small patterns" we often perform with for loops in imperative languages, like transforming all items in a collection by applying some function to each item (`map`), or removing certain items in a collection based on some condition (`filter`), or summing up or aggregating in some way the values in a collection (`reduce`).

Streams allow us to define "pipelines" of these operations to be performed on collections.

For example, imagine that we have a giant file of strings, and we need to:

* Upper-case each line
* Keep only the lines that include the phrase "SECRET PHRASE"

If our data is in a file called "file.txt", this would look like

```java
Stream<String> result = Files.lines(Path.of("file.txt"), Charset.defaultCharset())
    .map(String::toUpperCase)
    .filter(l -> l.contains("SECRET PHRASE"));
```

You'll notice that we still only have a `Stream<String>` after the above code runs.
That's because all we've done is create a *pipeline* of operations to be run --- we haven't actually executed those operations yet.
The `map` and `filter` above are *intermediate* operations.
They are not actually kicked off until a *terminal* operation is added to the stream pipeline.

As I mentioned earlier, a *terminal* operation produces some result or side effect, thereby exiting the stream pipeline. 
Some examples of terminal operations are:

* Collecting the result of the Stream into a list (`.toList()`)
* Counting the elements left in the stream after the intermediate operations have been performed (`count()`)
* In the case of primitive streams like `IntStream`, `DoubleStream`, `LongStream`, you can perform numerical aggregations (`.sum(), .average()`, etc.)

Here are some examples:

Notice that the type of `result` is now `List<String>`. It is no longer a stream to which we can add further computations.

```java
List<String> result = Files.lines(Path.of("file.txt"), Charset.defaultCharset())
    .map(String::toUpperCase)
    .filter(l -> l.contains("SECRET PHRASE"))
    .toList()
```

You can specify that a `map` should `mapToInt` (i.e., map to an `IntStream`).
That allows stream operations 

```java
OptionalInt result = Files.lines(Path.of("file.txt"), Charset.defaultCharset())
    .map(String::toUpperCase)
    .filter(l -> l.contains("SECRET PHRASE"))
    .mapToInt(String::length)
    .max();
```

**PONDER** Why do you think we get an `OptionalInt` instead of a plain old `int` in return?

Finally, you can terminate streams with "side effects", i.e., functions that don't return a value, but have some other effect (e.g., they change the value of some other variable, or they write to some output stream).

```java
Files.lines(Path.of("file.txt"), Charset.defaultCharset())
    .map(String::toUpperCase)
    .filter(l -> l.contains("SECRET PHRASE"))
    .forEach(System.out::println);
```

In the code above, we are applying the `forEach` terminal operation the stream. In the terminal operation, we are passing each item to the `System.out::println` function that you know and love. Recall that the `::` is the method reference syntax --- we are "pointing to" the `println` function and saying "call this on each item in the stream".
If lambdas are more your thing, you can write that as `l -> System.out.println(l)`. But in general it's better to use method references for lambdas that are this simple.

### Stream pipelines are evaluated lazily

Streams are lazy; computation on the source data is only performed when the terminal operation is initiated, and source elements are consumed only as needed.

**This has important implications.**
For example, consider the following pipeline, where I've added print statements in each operation.

```java
OptionalInt result = Files.lines(Path.of("file.txt"), Charset.defaultCharset())
    .map(line -> {
        System.out.println("Upper-casing " + line);
        return line.toUpperCase();
    })
    .filter(line -> {
        System.out.println("\tChecking " + line + " for secret phrase");
        return line.contains("SECRET PHRASE")
    })
    .mapToInt(line -> {
        System.out.println("\t\tMapping " + line + " to charlength");
        return line.length();
    })
    .max();
```

Can you predict what the printed output would be with the following input?

**INPUT**

```
here
are
sOME
LINes SEcret phrASE
in
A
File
```

**OUTPUT**

```
Upper-casing here
        Checking HERE for secret phrase
Upper-casing are
        Checking ARE for secret phrase
Upper-casing sOME
        Checking SOME for secret phrase
Upper-casing LINes SEcret phrASE
        Checking LINES SECRET PHRASE for secret phrase
                Mapping LINES SECRET PHRASE to charlength
Upper-casing in
        Checking IN for secret phrase
Upper-casing A
        Checking A for secret phrase
Upper-casing File
        Checking FILE for secret phrase
```

The `mapToInt` step only applied to one item—the one that survived the previous filtering step.

### Rules for behavioural parameters

All behavioural parameters to streams must:

* **Be Non-interfering**: While a stream pipeline is executing (i.e., its terminal operation has been defined/invoked), its data source must not be modified. This is similar to how you will be get a `ConcurrentModificationException` if you modify a collection while using a `for-each` loop to iterate over it.
* **Be Stateless**: A *stateful* lambda or function is one whose result depends on any state (e.g., instance variables in a class) that might change during execution of the stream pipeline. 
* **Not have side-effects**: Recall that stream operations are *lazily applied*.

From the [Stream documentation](https://download.java.net/java/early_access/jdk20/docs/api/java.base/java/util/stream/Stream.html)

In short, you cannot rely on all stream operations being executed.


> A stream implementation is permitted significant latitude in optimizing the computation of the result. For example, a stream implementation is free to elide operations (or entire stages) from a stream pipeline -- and therefore elide invocation of behavioral parameters -- if it can prove that it would not affect the result of the computation. This means that side-effects of behavioral parameters may not always be executed and should not be relied upon, unless otherwise specified (such as by the terminal operations `forEach` and `forEachOrdered`).

