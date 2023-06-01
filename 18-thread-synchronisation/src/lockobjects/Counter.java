package lockobjects;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private Lock lock = new ReentrantLock();

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

    public synchronized void decrement() {
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
