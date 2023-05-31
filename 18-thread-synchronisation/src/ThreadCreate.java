public class ThreadCreate implements Runnable {
    public static void main(String[] args) {
        ThreadCreate obj = new ThreadCreate();
        Thread t1 = new Thread(obj);
        t1.start();
        System.out.println("After the thread has finished executing.");
    }

    @Override
    public void run() {
        System.out.println("Inside the thread.");
    }
}
