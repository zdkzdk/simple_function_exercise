package cn.dc.threadlearn;

public class Thread2ConcurrentUse {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable的run");
            }
        }){
            @Override
            public void run() {
                System.out.println("Thread的run");
            }
        }.start();
        new Thread(() -> System.out.println(Thread.currentThread().getName())).start();
    }
}
