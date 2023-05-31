package demo;

public class ThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        Thread secondThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Inside the second thread.");
        });
        secondThread.start();
        secondThread.join();
        System.out.println("Inside the main thread.");
    }
}
