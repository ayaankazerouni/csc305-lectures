# 10 Java Generics

We've all written this line of code before, which creates a new empty list of `String`s.

```java
List<String> list = new ArrayList<>();
```

We know that `list` is now a list of strings, and if we tried to add anything else to the list, our code wouldn't compile. But what do all those angle brackets mean? How come the same List and ArrayList types can be used and re-used for lists of different types like Integers and Strings?

That's what we'll find out in this lecture. But first, a bit of a detour.

## Parameterisation

Consider the following function:

```java
public static int sum() {
    return 3 + 10;
}
```

Is `sum` a useful function? Your first reaction is probably "No, of course not!". And, well...yeah, it doesn't do much. But it's useful for a _really specific_ purpose: adding `3` and `10`. If we wanted to sum up any other numbers, we'd need to write more functions like `sum1and2, sum10and20`, etc. Obviously nobody programs like that. Instead, we write functions so that they can be more generally useful (i.e., usable for a wider class of inputs than two specific numbers).

To make `sum` more generally useful, we would _parameterise_ the two values that are being summed up. That is:

```java
public static int sum(int a, int b) {
    return a + b;
}
```

Our `sum` function is now usable for _any_ two integers. Much more generally useful than the `sum` of old. This is quite a simple idea, something we learned when we first learn about functions, so this whole example may seem really obvious. But it's worth spelling out.

We realise that the functionality offered by `sum` doesn't really depend on _which_ two numbers are being summed. Whatever they are (as long as they're `int`s here), the rest function remains the same. We don't actually care that the values are `10` and `3`. So we can instead use the names `a` and `b` to refer to two numbers that will have values _at runtime_, i.e., when the `sum` function is invoked.

This process—parameterisation-is useful in other contexts as well.

Consider the following example: we have a list of integers (`List<Integer>`), and we want to filter it so that it only contains the even numbers.

```java
public static int filter(List<Integer> list) {
    List<Integer> result = new ArrayList<>();
    for (int current : list) {
        if (current % 2 == 0) {
            result.add(current);
        }
    }

    return result;
}
``` 

Our `filter` function achieves this task admirably. If we called `filter(List.of(4, 1, 3, 6, 19))`, we would get the list `List.of(4, 6)` in return as expected.
But the `filter` function suffers from the same lack of generality that our original `sum` displayed.
Specifically, `filter` will _only_ work for filtering out odd numbers from a list. If we wanted to, say filter out all negative numbers, or all non-prime numbers, we would need to write separate filter functions for each of these.

Or, we can parameterise the condition on which the list is being filtered.

```java
public static int filter(List<Integer> list, Predicate<Integer> condition) {
    List<Integer> result = new ArrayList<>();
    for (int current : list) {
        if (condition.test(current)) {
            result.add(current);
        }
    }

    return result;
}

```

Our `filter` now accepts an additional parameter, `condition`. `condition` is a _predicate_ (a function that returns a boolean value). The `filter` function now simply tests the predicate against each item in the input list, and collects all the ones that satisfy the predicate (i.e., the ones for which the predicate returns true).
This is possible because of the same observation that we made in the original `sum` function. The `filter` functionality doesn't really change except for the condition that is checked for each item. We can parameterise that, and our `filter` is suddenly more generally useful. 

> Recalling what we talked about in the previous lecture, this is one of the reasons that treating functions as values like any other is a huge benefit to a language. The types of _parameterisations_ you do can take on new levels of abstraction (e.g., instead of simply parameterising integers like the `10` and `3`, we can parameterise entire chunks of code like the condition in the original `filter`'s `if` condition).

## So what does this have to do with Generics in Java?

Just like we parameterised the values for `a` and `b` and the code for `condition` in the previous two examples, we can also parameterise _types_ in situations where that is needed.

For example, we can create lists of different types of elements.

```java
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();
```

There aren't different list implementations for different content types. We're using the same list implementation to support lists with different contents.
This is a common way of declaring and initialising various data structures (lists, maps, sets, stacks, queues, etc.).

You might say, well I can do that without specifying the type at all:

```java
List list = new ArrayList();
list.add("some string");
```

However, the `list` above is now a list of `Object`s. Because we didn't specify a type, the list defaulted to `Object` as a "catch all" since `Object` is the root of the type hierarchy. Remember, Java is statically typed. Everything _must_ have a type at compile time.

This leads us to issues like the following:

```java
List list = new ArrayList();
list.add("some string");

String first = (String) list.get(0);
```

Because contents of `list` are `Object`s at compile time, we need to cast the item to a `String` if we know it's a string and we want to do string things to it. This is bad. Type-casting is like us telling the compiler "This is String. Trust me, I know what I'm doing". This is a bad sign.

Whereas with generics:

```java
List<String> list = new ArrayList<>();
list.add("some string");

String first = list.get(0); // no type-casting needed
```

So that weird angle bracket syntax (`<String>`) allows us to specify that it is a list of strings (or integers, or whatever we want). The List's content type is _parameterised_, but we also don't lose any of the goodness of compile-time type safety.

## Writing our own generic classes

I went over the examples below in class. Below are several examples of generic class implementations.

### `Box.java`

In the example below, `T` is a type parameter. You can name the type parameter whatever you want, but the convention in most Java codebases is that type parameters are denoted by one capital letter. I don't know why this is the convention.

```java
/**
 * A simple object representing a box around a value.
 *
 * T is a generic type. We don't know what it is till someone
 * creates a new Box object.
 *
 * Inside this class, we can only do Object things with T,
 * because the only thing we know about T is that it is an
 * Object or inherits from Object.
 */
public class Box<T> { 
                     
    private T value;

    public Box(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
```

### `Pair.java`

You can also create classes with multiple type parameters. What each type parameter means is totally up to the class and what it's supposed to do.
For example:

* The `Function` interface has two type parameters. The first one represents the input to the function, and the second one represents the output from the function.
* The `Predicate` interface has only one type parameter, representing the input to the predicate. The output type of all predicates is `boolean`.
* The `Consumer` interface takes only one type parameter, representing the input to the consumer. Consumers do not return any values. 

```java
/**
 * A pair of values.
 *
 * K and V are type parameters representing a key and value
 * for this pair. A class can have as many type parameters
 * as it needs. What each type parameter means is totally up
 * to the needs of the class.
 */
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
```

### Bounded generics: `BinarySearchNode.java`

In the above two examples, the type parameters by themselves are good enough, because the `Box` and `Pair` classes don't really need to know anything about the types they hold in order to function.
But sometimes we need to add some constraints to our type parameters. For example, consider a binary search tree.
The data structure relies on a _total ordering property_ of its contents. Usually, the property is as follows:

_For each subtree in the BST, the value held by the root node is greater than or equal to the values in the left subtree, and less than the values in the right subtree._

This means that, when items are being added to the binary search tree, the value of the node to be added must be compared to various nodes in the binary search tree, so that we can determine whether we should go left or right as we traverse down the BST.

But if our BST is generic and the content type will be specified at runtime, how do we write the insertion operation?

In this case, we need to add an "upper bound" on how general the generic type can be.
That is, we need to specify that even if we don't care exactly what the type in this BST is, we _do_ that it is a type that can be compared (i.e., there's some notion of "less than" or "greater than" for the type).

See the example below. There's some interesting things going on, so please be sure to read the comments.

```java
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A node in a binary search tree. A binary search node has a value
 * and two children (left and right), each of which are also binary
 * search nodes.
 */
public class BinarySearchNode<T extends Comparable<T>> {

    private T value;
    private BinarySearchNode<T> left;
    private BinarySearchNode<T> right;

    public BinarySearchNode(T value, BinarySearchNode<T> left, BinarySearchNode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public T getValue() {
        return this.value;
    }

    public BinarySearchNode<T> getLeft() {
        return this.left;
    }

    public BinarySearchNode<T> getRight() {
        return this.right;
    }

    private StringBuilder stringRep(int level) {
        return new StringBuilder("_".repeat(level) + this.value + "\n")
                .append(this.left == null ?
                        "_".repeat(level + 1) + "\n" :
                        this.left.stringRep(level + 1))
                .append(this.right == null ?
                        "_".repeat(level + 1) + "\n" :
                        this.right.stringRep(level + 1));
    }

    @Override
    public String toString() {
        return this.stringRep(0).toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        // Since other is an Object, we cannot cast the other object to
        // BinarySearchNode<T>. So we use the <?> wildcard type parameter
        // instead. <?> means "this can be anything".
        if (other instanceof BinarySearchNode<?> otherNode) {
            return otherNode.value.equals(otherNode.getValue())
                    && Objects.equals(this.left, otherNode.getLeft())
                    && Objects.equals(this.right, otherNode.getRight());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.left, this.right);
    }

    /**
     * Insert the given node into the subtree rooted
     * by this BinarySearchNode<T>.
     *
     * @param node The node to be inserted
     * @return The subtree resulting from this insert operation
     */
    public BinarySearchNode<T> insert(BinarySearchNode<T> node) {
        if (this.value.compareTo(node.value) >= 0) {
            this.left = this.left == null ?
                    node : this.left.insert(node);
        } else {
            this.right = this.right == null ?
                    node : this.right.insert(node);
        }

        return this;
    }

    public BinarySearchNode(List<T> values) {
        if (values != null && !values.isEmpty()) {
            Collections.sort(values);
            BinarySearchNode<T> constructed = buildTree(values, 0, values.size());
            if (constructed != null) {
                this.value = constructed.value;
                this.left = constructed.left;
                this.right = constructed.right;
            }
        }
    }

    /*
     * Static methods have slightly different rules about generic type parameters. For generic classes,
     * the type parameter is "resolved" when an object of the class is instantiated. But we don't need
     * to create an object to call static methods. So the generic type parameters of the class
     * don't really apply to static methods.
     *
     * Notice that we're using <U> here instead of <T>. We could have used any letter we wanted (including
     * <T> again if we so choose). This type parameter is not the same as the type parameter <T> that is used
     * by instances of this class.
     *
     * This method can be invoked as follows (e.g., for a list of integers):
     *      List<Integer> someList = Arrays.asList(4, 3, 6, 7, 12);
     *      BinarySearchNode.<Integer>buildTree(someList, 0, someList.size());
     */
    private static <U extends Comparable<U>> BinarySearchNode<U> buildTree(List<U> values, int low, int high) {
        if (low >= high) {
            return null;
        }

        int mid = (low + high) / 2;

        BinarySearchNode<U> left = buildTree(values, low, mid);
        BinarySearchNode<U> right = buildTree(values, mid + 1, high);
        return new BinarySearchNode<>(values.get(mid), left, right);
    }
}
```