# 06 Unit testing and test adequacy

## Topics for today

- What is software testing
- Unit testing libraries
  - JUnit 5, which you may be familiar with from previous classes
  - AssertJ, a popular library for "fluent" assertions (still uses JUnit as a test engine)
- Test adequacy criteria
- If we have time, property-based testing

## Systematic software testing 

Software failures can be costly.
They can cause damage to people, industries, and society.
As soon-to-be software engineers, it behooves you to be responsible for the quality of the software you create.

The most common way to do this is to test your software as you write it. All software is prone to failures. We want to
move our discovery of these failures to "our side" of the software delivery. That is, we'd like to discover failures
before software is shipped to clients.

You can think of software testing as "simulating usage" of the software, and checking that the software did what was
expected. 

An easy way to test your software is to just use it yourself and show that it does what you expect. This may be fine for
the smallest and simplest of programs—after all, it's easy to write a few print statements and be satisfied that a program
is working.

But when the software you're writing becomes larger, when it becomes complex, and importantly, when it starts to involve
_stakeholders_ (other modules, users, etc.), this kind of ad-hoc testing is insufficient and irresponsible.

Even the best programmers are prone to cognitive biases. That is, if you've just written a piece of code, it's difficult
to envision situations in which that code will fail. After all, you just wrote it! If you could think of inputs that would
cause your program to fail, you would account for those inputs in your program itself.

As our software gets larger and more difficult to reason about (as more and more modules start to interact with each
other), it becomes more difficult for us to think about possible scenarios in which our code would fail.

That is why we need a _systematic approach_ to testing. We want to systematically "probe" our software until we are
confident that our software meets its requirements.

For example, for functions that operate on numbers, we might make sure to test with positive numbers, negative numbers, 
and zero. For programs that operate on lists, we would make sure to test with empty lists, lists of size 1, and perhaps
`null` pointers. Following this systematic approach to choosing test inputs helps us to circumvent our cognitive bias that
tries to convince us that the code we just wrote _must_ be correct.

### A systematic approach to choosing test inputs 

Sources: chapters from [_Effective Software Testing_](https://www.manning.com/books/effective-software-testing) by Maurício Aniche.

* Chapter 2 _Specification-based testing_
* Chapter 3 _Structural testing_

Aniche proposes the following steps for devising test case inputs.

#### 1. Understand the requirements, inputs, and outputs.

Read the requirements carefully "Requirements" here may be at
varying levels of detail. It may be a thorough formal specification, or it may be an email from a client describing what
it is they want you to build. What should the program do? What should it not do? What are some "simple" inputs you can 
use to demonstrate to yourself that the program does what you want it to do? (Note that the program doesn't need to exist
yet when you perform this step.)

This step is arguably the most important step. You cannot test the program effectively if you don't have a solid understanding of what it _should_ do.

#### 2. Explore the program

This is a particularly useful step if you're testing a program that you haven't written yourself 
(or perhaps that you wrote a long time ago). A useful activity in this case is to trace the program yourself with specific
inputs. Your goal is to explore the program's current behaviour, and perhaps compare its behaviour with your understanding
of its requirements. Always remember that the requirements beget the program, not the other way around.

#### 3. Judiciously explore possible inputs and outputs, and identify partitions

What are the _classes_ or _partitions_ of inputs the program is expected to handle?

This includes but goes beyond simply looking at the inputs' data types. For example, suppose we have a function that's supposed to compute
compound interest for a given amount of money, for a given number of years:

```
compoundInterest(principleAmount, interestRate, numYears);
```

Naturally, we're going to want all three inputs to be numerical values (we have further preconditions that will come soon).
In a statically typed language like Java, you don't need to say "this program should only take integers and nothing else";
the compiler will handle that for you. In this respect, it's kind of like a test case itself.

So beyond the data type, what other attributes of the inputs should we think about? What is the range of values each input
can accept? If it's numerical, does it allow negative numbers? Floats? Integers greater than 1? For example, in the function
above, our inputs have the following preconditions and sets of possible inputs:

* `principleAmount`: whole numbers >= 0
  - 0
  - 1
  - whole numbers > 1
* `interestRate`: floating point numbers between 0 and 1
  - 0
  - 1
  - floating point numbers > 0 and < 1 
* `numYears`: numbers >= 0 (or >0? check your requirements!)
  - 0
  - 1
  - 100
  - 7.5

For the `principleAmount`, the input values 0 and 1 might produce "interestingly different behaviours", and perhaps even the
inputs 1 and 2. But the inputs 2 and 5 and 7 start to produce more-or-less the same logical behaviours. We call these
_equivalence partitions_. You are _partitioning_ the input space into subsets of inputs, each of would result in logically distinct 
behaviours. 

This is a notoriously difficult thing to do!

#### 4. Identify the boundaries

For the partitions you identified in the previous step, identify the _boundaries_.

With numerical data, for example,  many often make the mistake of simply checking boundaries such as negative numbers, 0,
and positive numbers. However, what counts as "boundary" for an input is highly dependent on the problem domain. 

For example, consider this super simple program:

```java
public static String hipHooray(int input) {
  if (input < 10) {
    return "hiphip";
  } else {
    return "hooray";
  }
}
```

In the example above, the boundary at which the result is logically different for different inputs is the number 10. Positive
or negative doesn't matter here. Don't fall into the trap of over-prioritising boundary values for the _data type_, and not
focusing enough on the _problem domain_.

#### 5. Devise test cases

This was kind of implied throughout, but at this point you should start to note down test cases based on your analysis.

When you've identified boundary values, you should attack those boundary points with your tests. Test the "on point" (the last
point in one partition) and the "off point" (the first point in the next partition). Also test "happy path" inputs: input points
that are squarely in one partition or another, nowhere near those pesky boundaries. Little victories are important!

Doing this for all combinations of inputs result in a huge number of test cases, not all of which will pay off in the long run. You need to think pragmatically about which types of tests are okay to eliminate (perhaps even at the cost of reduced code coverage). This is a good time to remind you that writing functions with fewer parameters makes your life easier.

## Automated software tests 

Ok, fine, we agree that we should use a principled approach to choosing test inputs. We've gotten a few steps down for
coming up with useful test inputs. Can't we just keep using good old print statements to confirm that the code does what
we want for all these inputs we painstakingly devised?

Why do we need to write _test suites_? Why do we use libraries like JUnit to write collections of test cases that can all be
run with a click of a button?

Like the answer to so many questions about software engineering, it's because software changes. You might manually run
your program through a rigorous gauntlet of test inputs, but anytime your software changes, you would need to manually
go through that gauntlet of test inputs again. Isn't it nicer if you could run through that entire suite of inputs with
a single button click?

In short, an automated test suite is many good things. But best of all, it's a _safety net_. It captures the _current_
(presumably) correct functionality offered by the codebase.

This makes it easier and safer to do a number of things in your program:

* **Refactoring**: You've made a bunch of changes to the structure of your code, but you're not sure if you accidentally
changed its behaviour in the process. No matter! Just run the tests.
* **Debugging**: You've just spent days tracking down a bug in your code that occurred because you had made an incorrect assumption about the problem you were solving. You need to make sure the bug doesn't show up again because someone else
makes the same assumption. Write a test case! If someone makes the same faulty assumption you did, they'll now have a
failing test case on their hands.
* **Comprehension**: You've been handed a code base and you want to understand what it does in various situations. Instead
of manually running the program umpteen times with different inputs, write a few tests and check your understanding
automatically.
* **Adding new features**: You're working on a large project with many interacting modules. You don't know if some feature
you added has had some unintended effects in other parts of the module, or worse, in other modules entirely. Without a test
suite, you're kind of out of luck, unless you're super-good at reasoning about a large codebase. With a test suite, you
can simply run it and rest assured that nothing broke (or know _exactly broke_ and go about fixing it).

## Test adequacy

Of course, all the good consequences of having a test suite depend on the tests actually being "good". What does "good"
mean here?

This is the notion of "test adequacy"—i.e., how do you measure the goodness of your tests? By now you're familiar with
at least one family of test adequacy measurements: code coverage.

Another type of measurements we'll talk about is called _mutation analysis_, which is more reliable, but also more costly
to measure than code coverage.

I won't dive into too many details about these criteria in these notes: a lot has been said about them already.

* [Code coverage](https://en.wikipedia.org/wiki/Code_coverage)
* [Mutation analysis](https://en.wikipedia.org/wiki/Mutation_testing)

We'll do an in-class demonstration and a brief activity to get the low down on these criteria.

### Implementation-based adequacy criteria

What I _will_ say about these criteria is that they are NOT the final determination of your code's quality or reliability.

At the end of the day both code coverage and mutation analysis are measured based entirely on your current implementation.
If your implementation itself is incorrect (e.g., because you misunderstood the requirements or made some false assumptions), and your tests pass with full coverage, all that means is that your tests are also labouring under the same misunderstanding as your program.

## The third artefact: the specification

Many developers use coverage criteria as the _primary testing goal_ while writing software tests.
That is, they are thinking about two artefacts:

* the code that must be tested
* the test suite that must adequately exercise the code-under-test and make sure it does what is expected 

The "what is expected" phrase above is doing a lot of work. It is hiding a _third_ artefact that we don't often think
about explicitly: the _specification_. The specification is the (ideally) unambiguous description of what your working
program is expected to do. Unless your team uses [formal methods](https://en.wikipedia.org/wiki/Formal_methods), your
"specification" is usually derived from textual documents, conversations between you and your client, etc. that describe
what the software should do.

This third artefact is the "real" measure of test adequacy that you should hold as your primary goal. Your test suite
should "cover" all the requirements, and your program should pass your test suite. _After this process_ you can feel free
to shore up any gaps in the test suite that are revealed by implementation-based criteria like code coverage and mutation
analysis.

## Examples

We'll do a couple of in-class activities based on the programs below.

**Activity 1**. Write and test a function that searchers a String for substrings delimited by a start and an end tag,
returning all matching substrings in an array.

* `null` input String returns `null`
* `null` open/close returns `null` (no match).
* Empty (`""`) open/close returns `null` (no match).

([From the Apache commons-lang `StringUtils` class.](https://github.com/apache/commons-lang/blob/e0b474c0d015f89a52c4cf8866fa157dd89e7d1c/src/main/java/org/apache/commons/lang3/StringUtils.java#L8810)

**Partitions**:

Each of these are further broken in terms of relationships between `str`, `open`, and `close`.

* Exceptional cases (`null`, `empty`) 
* `str` length = 1
* `str, open, close` length = 1 (mix of inputs where `str` contains `open`, `close`, both, or none)
* `str, open, close` length > 1 (mix of inputs where `str` contains `open`, `close`, both, or none)
* `str`
 
```java
public static String[] substringsBetween(final String str, final String open, final String close) {
    if (str == null || open == null || open.isEmpty() || close == null || close.isEmpty()) {
        return null;
    }

    int strLen = str.length();
    if (strLen == 0) {
        return new String[0];
    }

    int closeLen = close.length();
    int openLen = open.length();
    List<String> list = new ArrayList<>();
    int pos = 0;

    while (pos < strLen - closeLen) {
        int start = str.indexOf(open, pos);

        if (start < 0) {
            break;
        }

        start += openLen;
        int end = str.indexOf(close, start);
        if (end < 0) {
            break;
        }

        list.add(str.substring(start, end));
        pos = end + closeLen;
    }

    if (list.isEmpty()) {
        return null;
    }

    return list.toArray(new String[0]);
}
```

**Activity 2**: Use automated tests to check whether the following function is correct. There may or may not be a bug in this code. If the function is correct, say so and
submit a screenshot of your complete branch or mutation coverage as evidence that I should believe you.
If it's not correct, submit the test input, the expected output, and the function's _actual_ output in Canvas, along with an English description of the bug you uncovered.

**Partitions**:

* `left` is 0, `right` is 0, both are 0
* `left` / `right` have single digits with no carryover
* `left` / `right` have single digits with carryover
* `left` has more digits than `right`
* `right` has more digits than `left`
* number of digits in result is more than length of max(`left`, `right`)

```java
public static List<Integer> plus(List<Integer> left, List<Integer> right) {
    if (left == null || right == null)  {
        return null;
    }

    Collections.reverse(left);
    Collections.reverse(right);
    LinkedList<Integer> result = new LinkedList<>();

    int carry = 0;
    for (int i = 0; i < Math.max(left.size(), right.size()); i++) {
        int leftDigit = left.size() > i ? left.get(i) : 0;
        int rightDigit = right.size() > i ? right.get(i) : 0;

        // Throw an exception if the precondition doesn't hold
        if (leftDigit < 0 || leftDigit > 9 || rightDigit < 0 || rightDigit > 9) {
            throw new IllegalArgumentException();
        }

        int sum = leftDigit + rightDigit + carry;

        result.addFirst(sum % 10);
        carry = sum / 10;
    }

    return result;
}
```

