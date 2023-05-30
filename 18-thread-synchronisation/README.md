# 18 Thread synchronisation

**Thread interference** occurs when two operations, running in different threads, but acting on the same data, interleave. These types of issues are unpredictable and difficult to predict.

Consider a simple function running in a thread:

```java
public void run() {
    count++;
}
```

That single statement (`count++`) can be decomposed into three steps:

* Retrieve the current value of `count`
* Increment the retrieved value by `1`
* Store the incremented value back in `count`

Suppose Thread `A` runs at about the same time as Thread `B`. If the initial value of `count` is 0, this sequence can happen:

* Thread A: Retrieve count.
* Thread B: Retrieve count.
* Thread A: Increment retrieved value; result is 1.
* Thread B: Increment retrieved value; result is 1.
* Thread A: Store result in count; count is now 1.
* Thread B: Store result in count; count is now 1.

Thread A's result is lost, overritten by Thread B. Under different circumstances, it might be Thread B's result that gets lost, or there could be no error at all.

### Critical section

In concurrent programming, concurrent thread accesses to shared data can lead to unexpected or erroneous behaviour.
The part of the program where the shared data is accessed needs to be protected in ways that avoid concurrent access. 

This protected section is the **critical section** or **critical region**.


