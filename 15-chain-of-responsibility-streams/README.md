# Builder pattern, Streams

**References**:

* [Refactoring guru content on the Builder pattern](https://refactoring.guru/design-patterns/builder) (though, like some of the previous patterns, their suggested structure involves some over-complication)
* [Builder Design Pattern](https://howtodoinjava.com/design-patterns/creational/builder-pattern-in-java/) by Lokesh Gupta.
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

* The addition of Stream was one of the major features added to Java 8.
* Streams are wrappers around a data source, allowing us to operate with that data source and making bulk processing convenient and fast
*  A stream does not store data and, in that sense, is not a data structure. It also never modifies the underlying data source.
* This functionality supports functional-style operations on streams of elements, such as map-reduce transformations on collections
