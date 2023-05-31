package demo;

public class ThreadSleep {
    public static void main(String[] args) throws InterruptedException {
        Thread secondThread = new Thread(() -> {
            System.out.println("Inside the second thread.");
        });
        secondThread.start();
        Thread.sleep(3000);
        System.out.println("Inside the main thread.");
    } 
}
