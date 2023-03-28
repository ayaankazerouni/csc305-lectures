# Intro + Code style

## Course overview

What's this course about?

* **Program construction**: Practice building mid-sized production-quality software using a high-level programming language (Java). 
* **Program design**: A recurring theme in the course will be designing your software for *stability* in the face of *changing requirements*.
* **Design patterns**: We will talk about some design patterns. The goal is not to get you to memorise design patterns, but rather to gain an appreciation for the idea of design patterns as way to talk and think about program design. Unlike most books on "design patterns" we will try to think about patterns beyond the context of Object-oriented software.

"Production-quality" means that, in addition to functional correctness (your program does what we expect it to do), you will be expected to attend to the following aspects:
- Code style (e.g., using a linter) and writing code for readability and maintainability
- Testing
- Documentation (i.e., Javadoc comments)

### Syllabus overview

**Grading and assessments**

* Projects — some will build on others — design your programs with this maintainability in mind!
* Labs — we'll practice what we talk about in lecture — in the early-to-middle part of the quarter, these will be things like code review discussions, design documents — in the later weeks, these will be "labs" in the sense you're used to (programming assignments) where you'll practice new syntax/concepts that we've talked about
* Quizzes — these will be weekly timed quizzes taken during the lab session — you must be present to take these — these will include a mix of open-ended design questions, multiple-choice questions, and programming questions 
* Participation — there'll occasionally be in-class small-group activities done in class — you'll get credit for turning them in


**Notes about all programming assignments**

- Code style — all programming assignments will be done using Java 19, using Checkstyle to enforce coding standards — we'll use the Google style guide (Checkstyle configuration file provided in Canvas)
- Programming environment — you're encouraged to work using IntelliJ IDEA — we'll set it up with the Checkstyle plugin and walk through enabling test quality checks during the lab session

**Academic honesty**

- **Labs** are meant to be worked on individually, but you're free to discuss and collaborate with your classmates. But be aware that the labs are meant for your practice — you're shortchanging yourself if you don't let yourself be challenged.
- **Projects** are individual endeavours. Normally, you can discuss high-level issues with your classmates, but note that this is a *design course*. By-and-large, you all know how to program already. The high-level issues are in large part what the projects are meant to test.
- **Quizzes** are completely individual endeavours. Any evidence of illicit collaboration will be taken extremely seriously.

## Code style, programming practices

_Effective Java, Sonarlint_

**Code critique**

```
public static String delimit(String delimiter, String[] str) {
    String result = "";
    int i;
    for (i = 0; i < str.length; i++) {
        if (i != 0) {
            result += delimiter;
        }

        result += str[i];
    }

    return result;
}
```

What do you think of this code? Is its purpose **clear**?Is the function **maintainable**?

**EJ57: Minimise the scope of local variables.**

The variable `i` is now only available inside the `for` loop.

```
public static String delimit(String delimiter, String[] str) {
    String result = "";
    if (str.length > 1) {
        result += str[0];
    }
    
    for (int i = 1; i < str.length; i++) {
        result += delimiter;
        result += str[i];
    }

    return result;
}
```

**EJ63: Beware performance of String concatenation**

- `+` is a convenient way to concatenate strings
- but string are _immutable_, that means each time we "append" to the string, a `new` string is created.
 the `new` keyword is expensive to use.
- use `StringBuilder` instead

```
public static String delimit(String delimiter, String[] str) {
    StringBuilder result = new StringBuilder();
    if (str.length > 1) {
        result.append(str[0]);
    }
    
    for (int i = 1; i < str.length; i++) {
        result.append(delimiter);
        result.append(str[i]);
    }

    return result.toString();
}
```

**EJ58: Prefer for-each loops to traditional for loops where possible.**

- You may sometimes need the index variable; in those cases a regular `for` loop is fine.

```
public static String delimit(String delimiter, String[] str) {
    StringBuilder result = new StringBuilder();
    if (str.length > 1) {
        result.append(str[0]);
    }
    
    for (String current : str) {
        result.append(delimiter);
        result.append(current);
    }

    return result.toString();
}
```

**EJ59 Know and use the standard libraries.**

- java platform has been developed over years
- benefit from the experts where you can
- it's also really well documented

```
public static String delimit(String delimiter, List<String> str) {
    return String.join(delimiter, str);
}

public static String delimit(String delimiter, String[] str) {
    return String.join(delimiter, str);
}
```
