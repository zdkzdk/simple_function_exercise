package cn.dc.threadlearn;

public class StopThread {
    public static void main(String[] args) throws InterruptedException {
        oneSleepCase();
    }

    /*
       没有阻塞的情况下
       用interrupted
    */
    private static void commonCase() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if (i % 1000 == 0 && !Thread.currentThread().isInterrupted()) {
                        System.out.println("可以被1000整除:" + i);
                    }
                }
            }
        });
        thread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }

    /*
    单次阻塞的情况
     */
    private static void oneSleepCase() throws InterruptedException {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        if (i % 1000000 == 0 && !Thread.currentThread().isInterrupted() ) {
                            System.out.println("可以被100000整除:" + i);
                        }
                        add();
                    }

                }
            });
            thread.start();

            Thread.sleep(500);

        thread.interrupt();
    }
    private static void add(){
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
