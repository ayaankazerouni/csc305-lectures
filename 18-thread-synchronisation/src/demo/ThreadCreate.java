package demo;

public class ThreadCreate {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> System.out.println("Inside the thread"));
        t1.start();
        System.out.println("End of the main thread.");
    }
}
