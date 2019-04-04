package cn.dc.client.thread;



import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReciveThread extends Thread {
    Socket socket;

    public ReciveThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        while (true){
            try {
                isr = new InputStreamReader(socket.getInputStream());
                char[] chars = new char[1024];
                isr.read(chars);
                System.out.println(new String(chars));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
