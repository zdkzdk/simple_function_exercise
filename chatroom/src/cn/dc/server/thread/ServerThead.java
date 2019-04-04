package cn.dc.server.thread;

import cn.dc.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThead extends Thread {
    Socket socket;
    ArrayList<ServerThead> list;
    InputStreamReader isr;

    public ServerThead(Socket socket,ArrayList<ServerThead> list) {
        this.socket = socket;
        this.list = list;
        try {
            this.isr = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (;;){
            sendOther();
        }

    }

    /*
     * 接收到消息后转发到其他client上
     * */
    //接收消息
    private String getMsg() {
        char[] chars = new char[1024];
        int i = 0;
        try {
            isr.read(chars);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String string = new String(chars);
        System.out.println(string);
        return string;
    }
    //将指定的String消息写入到输出流中
    private void send(String msg){
        try {
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            osw.write(msg);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //将接收的消息发到除当前客户端之外的客户端上
    //也就是写入到除当前serverThread之外的输出流中
    private void sendOther(){
        for (ServerThead st : list){
            if(st == this) continue;
            st.send(getMsg());
        }
    }
}
