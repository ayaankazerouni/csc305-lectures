# Builder pattern, Streams

* Refresher on the builder pattern
* Streams and lambdas

**References**:

* [Refactoring guru content on the Builder pattern](https://refactoring.guru/design-patterns/builder) (though, like some of the previous patterns, their suggested structure involves some over-complication)
* [Builder Design Pattern](https://howtodoinjava.com/design-patterns/creational/builder-pattern-in-java/) by Lokesh Gupta.
* [Java Stream documentation](https://download.java.net/java/early_access/jdk20/docs/api/java.base/java/util/stream/package-summary.html)

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

