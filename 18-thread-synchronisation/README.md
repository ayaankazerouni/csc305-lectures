# 18 Thread synchronisation

**Thread interference** occurs when two operations, running in different threads, but acting on the same data, interleave. These types of issues are unpredictable and difficult to predict.

In concurrent programming, concurrent thread accesses to shared data can lead to unexpected or erroneous behaviour.
The part of the program where the shared data is accessed needs to be protected in ways that avoid concurrent access. 

This protected section is the **critical section** or **critical region**.

An **atomic action** is required in a critical section where only one thread can execute its critical section at a time. All the other threads have to wait to access the critical section.

## Thread interference demo

Consider the following class `Counter` that simply keeps track of a count that can be incremented and decremented.

For the purposes of illustration, the increment and decrement are done in explicitly separated steps (as opposed to `count++` and `count--`), and they include some `Thread.sleep` calls so that there is a higher possibility of threads interleaving.

```java
public class Counter {
    private int count = 0;

    public void increment() {
        try {
            int val = this.count;

            Thread.sleep(1000);
            int newVal = val + 1;

            Thread.sleep(1000);
            this.count = newVal;
        } catch (InterruptedException e) {
            // no-op
        }
    }

    public void decrement() {
        try {
            int val = this.count;

            Thread.sleep(1000);
            int newVal = val - 1;

            Thread.sleep(1000);
            this.count = newVal;
        } catch (InterruptedException e) {
            // no-op
        }
    }
    
    public int getCount() {
        return this.count;
    }
}
```

As described above, if two threads both run the `increment` method on the same object, depending on how the threads get scheduled, there is a possibility of the work of one thread being overwritten by another thread.

```java
Counter counter = new Counter();

Thread t1 = new Thread(() -> counter.increment());
Thread t2 = new Thread(() -> counter.increment());

t1.start();
t2.start();

t1.join();
t2.join();

System.out.println(counter.getCount());
```

Suppose Thread `t1` runs at about the same time as Thread `t2`. If the initial value of `count` is 0, this sequence can happen:

* Thread A: Retrieve count.
* Thread B: Retrieve count.
* Thread A: Increment retrieved value; result is 1.
* Thread B: Increment retrieved value; result is 1.
* Thread A: Store result in count; count is now 1.
* Thread B: Store result in count; count is now 1.

Thread A's result is lost, overwritten by Thread B (the program will print `1`). Under different circumstances, it might be Thread B's result that gets lost (program prints `1`), or there could be no error at all (program prints `2`).

To prevent these kinds of "race conditions" (where the result depends on which thread wins a race), we need some way to "synchronize" the execution of the two threads, such that they access and modify shared data in correct ways.

## Synchronized methods and statements

Every Object in Java has an internal entity associated with it called its *intrinsic lock* or *monitor lock*.
When a thread needs exclusive access to an object's fields or methods, it must *acquire* the object's intrinsic lock before accessing them, and *release* the lock after it's finished with the object.

When one thread has an object's intrinsic lock, no other thread can acquire the same lock. The other thread will "block" or suspend its execution until the object's lock becomes available again.

This notion of an Object lock is extremely useful because it lets us synchronize access to data that is shared by multiple threads (e.g., mutable objects).
The *critical section* mentioned above is the part of the program that is executed between *acquiring* and *releasing* an object's intrinsic lock.

The Java language provides two basic idioms for synchronising access to critical sections: **`synchronized`** methods and **`synchronized`** statements.

### Synchronized methods

To make a method `synchronized`, simply add the `synchronized` keyword to its declaration.

```java
// rest of the Counter class stays the same

public synchronized void increment() {
    // ... method stays the same
}

public synchronized void decrement() {
    // ... methods stays the same
}
```

When one thread is executing a `synchronized` instance method for an object, all other threads that invoke `synchronized` methods for the same object will block (suspend execution) until the first thread is done with the object.

When a thread invokes a `synchronized` method, it automatically acquires the intrinsic lock for that method's object and releases it when the method returns. The lock release occurs even if the return was caused by an uncaught exception.

So in the `Counter` example, when Thread `t1` is executing on the `counter` object, it "owns" the `counter` object's intrinsic lock. So when Thread `t2` is kicked off, it requests access to the object, but cannot proceed until Thread `t1` releases the lock (i.e., finishes executing the `synchronized` method `increment`).

In Java synchronization, if a thread wants to enter a `synchronized` method it will acquire lock on *all* `synchronized` methods of that object, not just on one `synchronized` method that thread is using.

So in the `Counter` example, if Thread `t1` was calling `increment`, and Thread `t2` wanted to call `decrement`, it still would NOT be able to do so, because it's a common lock on the `counter` object that controls access to *all* `synchronized` methods.

`static synchronized` methods do not use an intrinsic lock associated with any instance of the class. Instead, they use lock associated to the `Class` object loaded by the JVM. So, one lock for the class, and individual locks for individual instances of the class.

### Synchronized statements

Deciding which methods to mark as `synchronized` or not can be challenging. In general, you need to identify the **critical section**, and that critical section needs to take place as an **atomic action**. That is, it must run through the entire sequence of instructions without another thread being interleaved that accesses the same data.

In many cases, this critical section might be an entire method. However, overly stringent locking can start to erode the benefit you would get from concurrent programming. More often you want finer-grained control over object locking.

Java therefore lets you lock *statements* or *blocks of code* instead of entire methods.

`synchronized` methods inherently have objects on which to lock (the `this` object in the case of instance methods, and the `Class` object in the case of `static` methods). Byt `synchronized` statements have no such object.

Therefore, you must manually provide a `synchronized` statement an object on which to lock.

The syntax is similar:

```java
synchronized(this) {
    count++;
}
```

The code above, instead of locking an entire method, locks only the code within the curly braces. In the example above the synchronization is done on the basis of the `this` object, just like `synchronized` instance methods.

Any object can be used for the lock. For example, within the same class, you may have multiple bits of shared data (`c1` and `c2` in the example below) that need to be synchronized across threads. 

However, we only care that two threads don't access `c1` at the same time, or `c2` at the same time. But we don't care if one thread accesses `c1` while another accesses `c2` in parallel.

```java
public class MsLunch {
    private long c1 = 0;
    private long c2 = 0;
    private Object lock1 = new Object();
    private Object lock2 = new Object();

    public void inc1() {
        synchronized(lock1) {
            c1++;
        }
    }

    public void inc2() {
        synchronized(lock2) {
            c2++;
        }
    }
}
```

Use this idiom (of multiple locks) with extreme care. You must be absolutely sure that `c1` and `c2` are totally separate and don't need to be synchronized together.

## Deadlock

Sometimes you can get into a situation where two threads are both waiting for each other to terminate.
This results in a situation called *deadlock*, where neither thread makes any progress.

Consider this example from [the Java tutorials on concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/deadlock.html).

```java
public class Deadlock {
  static class Friend {
      private final String name;
      public Friend(String name) {
          this.name = name;
      }
      public String getName() {
          return this.name;
      }
      public synchronized void bow(Friend bower) {
          System.out.format("%s: %s"
              + "  has bowed to me!%n", 
              this.name, bower.getName());
          bower.bowBack(this);
      }
      public synchronized void bowBack(Friend bower) {
          System.out.format("%s: %s"
              + " has bowed back to me!%n",
              this.name, bower.getName());
      }
  }

  public static void main(String[] args) {
      final Friend alphonse =
          new Friend("Alphonse");
      final Friend gaston =
          new Friend("Gaston");
      new Thread(new Runnable() {
          public void run() { alphonse.bow(gaston); }
      }).start();
      new Thread(new Runnable() {
          public void run() { gaston.bow(alphonse); }
      }).start();
  }
}
```

Alphonse and Gaston are two super polite friends who bow to each other when they meet. They remain bowed until the other has bowed back to them. In most cases, this works out fine. However, if both Alphonse and Gaston bow to each other at the same time, they can never exit their bow, because they are both waiting for the other one to bow back.

Each `Friend` object calls its `synchronized` `bow` method to bow to the other `Friend` object. The `bow` method implementation calls another `synchronized` method, `bowBack` on the object that is passed in.

So when Alphonse bows to Gaston, the lock on Alphonse is acquired by the first thread. When Gaston bows to Alphonse, the lock on Gaston is acquired by the second thread. Now Alphonse's thread needs access to Gaston's lock in order to make Gaston `bowBack`, and Gaston's thread needs Alphonse's lock to make Alphonse `bowBack`. Neither can progress. Legend has it they are still bowed to each other and must be fed and changed by passers by.

The lesson here is: **be careful about calling other objects' `synchronized` methods from a `synchronized` method.** `synchronized` blocks of statements can help you limit which parts of a method must be `synchronized` instead of synchronizing the whole method.

**Avoiding deadlock**

* Avoid Nested Locks – this is the main reason for a deadlock condition.
* Avoid Unnecessary Locks – The locks should be given to the important threads. Giving locks to unnecessary threads can cause the deadlock condition.
* Use `Lock` objects to break deadlocks — The Java `Lock` interface represents a concurrent lock which can be used to guard against race conditions inside critical sections. I'll explain this more after a quick detour.

## wait, notify, notifyAll

Sometimes, we need more fine-grained conditional control over how we enter and exit critical sections.

The Object class in Java contains three final methods that allows threads to communicate about the lock status of a resource.

* `wait` method waits indefinitely for any other thread to call `notify` or `notifyAll` method on the object to wake up the current thread
* `notify` method wakes up only one thread waiting on the object and that thread starts execution. The choice of the thread to wake depends on the OS implementation of thread management.
* `notifyAll` method wakes up all the threads waiting on the object, although which one will process first depends on the OS implementation.

The above methods can be called on an object to cause the current thread to pause or to wake up. They are commonly used when you want a thread to conditionally wait or execute some code.

For example, consider the `BlockingQueue` data structure. It functions more or less like a regular queue, except that:

* When adding something to the queue, it *waits* for the queue to be below capacity, so that the new item can be accepted
* When something is removed from the queue, it *waits* for there to be at least one item in the queue before removing and returning it
* When something is removed from the queue, it *notifies* the object that it's ready to accept new items (freeing up the `add` method from its waiting)

An example add and remove might look like this (some of the surrounding code has been elided):

```java
public synchronized void add(T element) {
    while (this.queue.size() == capacity) {
        wait(); // while the queue is at capacity, this request will wait
    }

    this.queue.add(element); // once the wait is over, add the element
    notify(); // signal to the object; use notifyAll to notify all waiting threads
}


public synchronized T remove() {
    while (this.queue.isEmpty()) {
        wait(); // while the queue is empty, wait
    }

    T item = this.queue.remove(); // when the wait is over, remove the first item
    notify(); // notifyAll to notify all waiting threads
    return item;
}
```

In the `add` method, the `wait` is waiting for the queue to become below capacity, so something can be added. And the `notify` is signalling that there is something in the queue (so the `remove` method can do its thing).

In the `remove` method, the `wait` is waiting for the queue to not be empty, so something can be removed. And the `notify` is signalling that something was removed, so that the `add` method can do its thing.

So there are TWO "waiting conditions" and TWO signals being sent, but all of it uses the same primitive `wait` and `notify` mechanism. It's up to the programmer to manage the different states that need to wait and notify according to various conditions. Using `wait` and `notify` correctly in a complex module is notoriously difficult for this reason.

## Java Lock objects

The `synchronized` keyword allows a really simple kind of lock, where access to critical sections of an object's code can be restricted to one thread at a time. And the `wait`/`notify` mechanism allows conditional locking and unlocking of certain portions of the synchronized object's code.

More recent Java versions include higher-level abstractions over these constructs in the form of the [`Lock`](https://download.java.net/java/early_access/valhalla/docs/api/java.base/java/util/concurrent/locks/Lock.html) interface. The simplest implementation of this interface (and the one that we'll talk about) is the [`ReentrantLock`](https://download.java.net/java/early_access/valhalla/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html). The `ReentrantLock` provides the same basic behaviour as the `synchronized` keyword, with some extended capabilities.

Considering the `Counter` example above, we can rewrite the `increment` method using the `ReentrantLock` like below. Notice that we don't need to declare the method as `synchronized`; we're kind of doing the equivalent of a `synchronized` block here. 

```java
public void increment() {
    lock.lock();
    try {
        int val = this.count;

        Thread.sleep(1000);
        int newVal = val + 1;

        Thread.sleep(1000);
        this.count = newVal;
    } catch (InterruptedException e) {
        // no-op
    }
    lock.unlock();
}
```

Ok, so the `ReentrantLock` provides the same functionality as the `synchronized` block. What's the big deal?

The `ReentrantLock` also has the `trylock` method, which has the following signature: `boolean trylock(long timeout, TimeUnit timeUnit)`

It attempts to obtain a lock, but gives up after a specified amount of time. It returns a `boolean` value telling us if the lock was successfully obtained or not. This ability to "back out" of trying to obtain a lock helps us to avoid deadlock situations. 

Go back to our Alphonse and Gaston example, and consider how you would use this newfound ability to break them out of their lifelong bows. ([Or, read the implemented example here.](https://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html))
