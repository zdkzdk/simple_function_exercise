package cn.dc.server;

import cn.dc.server.thread.ServerThead;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8888);
            ArrayList<ServerThead> list = new ArrayList<>();
            for (;;){
                Socket socket = ss.accept();
                ServerThead serverThead = new ServerThead(socket,list);
                serverThead.start();
                list.add(serverThead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
