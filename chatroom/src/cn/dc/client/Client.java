package cn.dc.client;

import cn.dc.client.thread.ReciveThread;
import cn.dc.client.thread.SendThread;

import javax.net.ssl.SSLContext;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1",8888);
            /*
             * 发送和接收同时进行，要同时开2个线程
             * sendThread获取用户输入并发送
             * reciveThread获取服务器响应的数据并打印到用户界面
             */
            new SendThread(socket).start();
            new ReciveThread(socket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
