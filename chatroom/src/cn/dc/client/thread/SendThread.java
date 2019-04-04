package cn.dc.client.thread;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class SendThread extends Thread {
    Socket socket;
    Scanner sc = new Scanner(System.in);
    public SendThread(Socket socket) {

        this.socket = socket;
    }

    @Override
    public void run() {

        OutputStreamWriter osw = null;
        while (true){
            try {
                String string = sc.next();
                osw = new OutputStreamWriter(socket.getOutputStream());
                osw.write(string);
                osw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
