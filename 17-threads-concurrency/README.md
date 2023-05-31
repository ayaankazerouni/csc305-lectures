# 17 Processes and threads 

## Processes

A **program** needs computer resources to run. Allocation of these resources is managed by the operating system.

A **process** is a computer program in execution, i.e., a program that's actually running. Each process has its own memory space. Communication between different processes is possible through *Inter-process communication* (IPC) resources like pipes and sockets.

### Process control block (PCB)

Each process is represented in the OS by a *process control block* (PCB). The PCB contains some information about the process. This is an incomplete list, but here are some important bits of information the PCB keeps track of.

**Process ID (PID)** — Each process has a unique ID.

**Process State**

* **New** — process is being created
* **Ready** — Ready to be assigned to a processor to begin running
* **Running** — It's running
* **Waiting** – It's waiting for some event or I/O (e.g., `Scanner` waiting for user input, waiting to `poll` a blocking queue)
* **Terminated** — The process is ended

**CPU Scheduling information**

Your CPU has a number of processes that need to be executed. It needs to determine the order in which processes will run, and which ones need priority. Moreoever, since processes may be long-running, processes will be allocated some "running time" before running time is given to some other process. The PCB keeps track of the information needed for the CPU to control scheduling of a process.

**Memory** — As mentioned above, each process has its own memory space and limits. If you are working on ths Streams lab, you likely ran into these limits.

**I/O information** — What I/O devices is the process accessing?

## Threads

A **thread** is the basic unit of CPU utilisation within a process. A process will typically have a "main thread" that handles its computations, and it may spin up additional threads as needed.

Threads are sometimes called "lightweight processes". Threads within the same process can share the process's resources, including memory and open files. This makes for efficient communication between multiple threads, but can potentially lead to errors when multiple threads make conflicting requests to the same resources.

### Why multi-threading?

A "traditional" process has just a single thread of control. This means it can perform a single task at a time.

While this certainly simplifies issues around synchronisation of multiple threads, this doesn't take advantage of the fact that most modern computers have multi-core processors. This means our machines have the ability to run do multiple things "at once".
This can increase the *throughput* of our application (i.e., the amount of data our application can process in a given amount of time).

In addition to just increasing throughput by parallelising processing of some data, some applications are simply too complex to run in a single thread.

For example, your web browser manages many tasks at once — downloading a large file in the background, streaming a YouTube video in one tab, keeping Canvas open in another tab (to say nothing of the invisible requests it makes to various entities paying to place ads in front of you). The browser is, ultimately, a running program (a *process*). If the browser could handle just one thing at a time, our browsing experience would be very different. We'd have to wait for a file to finish downloading before doing anything else, or only use one tab at a time, for example.

Another example is your IDE — it performs syntax highlighting, compilation, SonarLint checking, static analysis, and auto-completion seemingly all at once.

### Memory management in threads

Java programs are executed by the *Java Virtual Machine* (JVM). It's a virtual machine is able to execute Java bytecode (i.e., the format that your Java source code compiles down to). "The JVM" is really a specification detailing what a JVM implementation should do.
Different vendors might implement their JVMs separately—that's okay, as long as they adhere to the specification.

In most implementations, the JVM is run as a single process. You can see the process's PID by doing the following:

```java
System.out.println(ProcessHandle.current().pid());
``` 

If it is easier to think about, you can more-or-less think about "the JVM" and "your running Java program" interchangeably for the purposes of this discussion. So we will talk about how, within that process, memory is managed and shared among multiple threads owned by that process.

There are three main memory areas in the JVM:

1. **The method area** is shared among all threads. There is usually one method area per JVM. It stores class-level data like:

* Constants pool — including numeric constants, static constants
* Method data — method signature, modifiers
* Method code — bytecodes, info about how much space its local variables take, exceptions, etc.

2. **The heap area** is also shared among all threads. (This is different from the Heap data structure you're working on for Project 3.)

This area stores information about **Objects** and their instance variables and arrays.

**The method area and heap area are not thread-safe**, since they are shared by all threads.

3. Finally, you have the **stack area** (related to the Stack data structure that you're familiar with.) The **stack area** is not shared; it's a per-thread resource.

For each thread, when it begins running, a separate runtime Stack is created. The runtime Stack's job is to store method calls. Each time a method is called, a new entry (called a **stack frame**) is pushed onto the stack. When the method terminates, that Stack frame is removed from the runtime stack and destroyed.

Each stack frame stores information about the method's local variables and some space for performing operations on those local variables. Primitive variables are stored in the stack frame. For object types, a reference is stored that points to the object's location in the heap area.

(Unlike the **heap area**, the **stack area**'s name is meaningfully related to the data structure that you know of with the same name.)

That's why when you go into infinite recursion, you get a `StackOverflowError` — the runtime stack for that thread overflowed with too many stack frames, because there were too many method calls. It's possible to get a `StackOverflowError` without recursion.

**The stack area is thread-safe**, since it belongs to a single thread.

## Thread creation

As we've described, each Java program will begin running with a single thread (a "main" thread). You can create additional threads using the `Runnable` interface.

In the code below, the `Demo` class is an instance of `Runnable`, which means it must implement the `run` method.

The `run` method simply prints the string `"Inside the thread."`.

In the main thread, we create a new `Demo` object, and assign it to a new `Thread`. Then, we `start` the thread.

**Can you predict the order in which the two print statements will execute?**

```java
public class Demo implements Runnable {
    public static void main(String[] args) {
        Demo obj = new Demo();
        Thread t1 = new Thread(obj);
        t1.start();
        System.out.println("After the thread has finished executing.");
    }

    @Override
    public void run() {
        System.out.println("Inside the thread.");
    }
}
```

Instead of creating a `Runnable` object, you can also create a class and directly extend the `Thread` class. You'll still need to extend the `run` method.

## The `Thread` API

* `Thread::start` — an instance method that you call on a `Thread` object. It starts the given thread — when it runs is up to the OS scheduler. A thread can be started at most once. You can't call `start` on a thread that has already been started.
* `Thread.sleep` — a static method that tells the current thread (whether it is the main thread or some other thread that was started later) to sleep for a specified amount of time.
  * This is a way of making processor time available to the other threads of an application that might be running on the same system
  * The sleep method can also be used for pacing of threads
* `Thread::join` — an instance method that you all on a `Thread` object. If a currently running thread calls `t.join()` (where `t` is a started `Thread` object), the currently running thread is made to pause execution until `t` terminates. It's a way of pacing threads without using `sleep`.

In the next class, we'll talk about synchronising threads so they work with shared data safely and correctly.

