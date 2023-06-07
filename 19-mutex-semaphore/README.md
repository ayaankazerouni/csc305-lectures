# 19 Mutex, semaphore, thread pools

These notes give a very high-level overview of some additional topics related to concurrency in Java.

# Mutex

So far, we have talked about using the `synchronized` keyword to "lock" access to an Object. Code that is marked as `synchronized` (either a `synchronized` method or a `synchronized` code block) can only be executed by the single thread that currently holds the lock.

This ensures that execution of these critical sections is *mutually exclusive*. If one thread is executing a critical section, all other threads must wait for the thread to release that lock.

This, in short, describes a "mutual exclusion lock", commonly known as a *mutex*.

The `Lock` interface in Java, and its implementations like `ReentrantLock` enable you to use mutexes at a higher level of abstraction than the `synchronized` keyword and the `wait`/`notify` mechanism.

# Semaphore

Another related concurrency concept is the *semaphore*.

A semaphore is, at the simplest level, an integer. The integer represents the number of "permits" the semaphore has available.
The semaphore comes with two basic operations:

* *acquire* (or "down", or "decrement") — this reduces the integer by 1
* *release* (or "up", or "increment") — this increases the integer by 1

Typically, when a currently running thread calls `acquire` on a semaphore, it will either take a permit and return immediately (thus decrementing the number of permits available), or it will "block" or wait until a permit becomes available.
Similarly, when a thread calls `release` on a semaphore, it immediately releases a permit (thus incrementing the number of permits available).

The [`Semaphore` API](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/Semaphore.html) in Java is quite simple. It provides the following methods, among others:

* constructor (`Semaphore(int permits)`)
* `acquire`
* `boolean tryAcquire()` — This method will try to acquire a permit, but if isn't available, it returns `false` instead of waiting for a permit. This can help avoid deadlock situations.
* `release` 

## Binary semaphore

A *binary semaphore* is a semaphore with 1 available permit. In effect, it has two possible states: `available` or `unavailable`.

A binary semaphore can be used instead of a mutex, and things would more-or-less work the same. I.e., the thread should `acquire` the semaphore before beginning execution of the critical section, and `release` the semaphore when it's done.

**Difference between binary semaphore and a mutex** An important difference between the binary semaphore and the mutex lock is that there is no notion of "ownership" with a semaphore. It can be "released" by a thread other than the one that currently has the permit. This is certainly true in the Java implementation.

## Counting semaphore

The *counting semaphore* maintains an initial number of "permits" that's greater than 1. The specified number of threads can `acquire` the semaphore, and any threads wanting access after that must wait for a permit to become available (i.e., must wait for a thread to `release` a permit). This is useful if you want to allow a fixed number of threads to be able to access a shared resource simultaneously.

## Thread pool

The Java `Executor` interfaces provide access to a *thread pool*.

* A thread pool is a pool of threads that can be "reused" to execute tasks, so that each thread may execute more than one task.
* A thread pool is an alternative to creating a new thread for each task you need to execute.
* Creating a new thread comes with a performance overhead compared to reusing a thread that is already created.
* Additionally, using a thread pool can make it easier to control how many threads are active at a time.

More details here: https://docs.oracle.com/javase/tutorial/essential/concurrency/exinter.html


