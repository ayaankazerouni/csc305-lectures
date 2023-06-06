# 19 Mutexes and Semaphores

# Mutex

* mutual exclusion lock
* controls access to shared data by only allowing only a single thread at a time to access the data
* to access the data, a thread must *acquire* the lock, and must *release* the lock when they are finished with the data

# Semaphore

## Binary semaphore

* this is similar to the mutex, but has a different use case
* with a mutex, you have a lock that ensures mutually exclusive access to shared data
* so Threads A and B access the data, but there's no ordering between them, and no logic that controls transfer of control between the two threads
* sometimes you want a situation where a Thread needs to *signal* to another thread that some event has occurred
* with a Semaphore, the thread that holds the Semaphore can "give" the Semaphore to any other thread that is waiting to "take" the Semaphore
  * like the `wait`/`notify` mechanism and 


Differences between binary semaphore and mutex lock

* the semaphore has no notion of ownership. it can be "released" by a thread other than the one that currently "has" the semaphore

## Counting semaphore

The counting semaphore maintains an initial number of "permits". The specified number of threads can `acquire` the semaphore, and any threads wanting access after that must wait for a permit to become available (i.e., must wait for a thread to `release` a permit). This is useful if you want to allow a fixed number of threads to be able to access a shared resource simultaneously.

## Thread pool

* A thread pool is a pool of threads that can be "reused" to execute tasks, so that each thread may execute more than one task.
* A thread pool is an alternative to creating a new thread for each task you need to execute.
* Creating a new thread comes with a performance overhead compared to reusing a thread that is already created.
* Additionally, using a thread pool can make it easier to control how many threads are active at a time.
