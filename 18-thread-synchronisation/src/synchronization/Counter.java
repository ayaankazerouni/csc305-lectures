package synchronization;

public class Counter {
    private int count = 0;

    public synchronized void increment() {
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
