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

A good example is the regular expression example we saw last week. The `usePattern` function is closed to modification (i.e., we won't make changes to the function)



