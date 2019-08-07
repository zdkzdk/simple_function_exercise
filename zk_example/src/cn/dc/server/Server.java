package cn.dc.server;

import org.apache.zookeeper.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * 启动后，
 * 向zk注册url，
 * 设置监视点用来？？？
 * 接收客户端请求并向客户端返回端口数据
 */
public class Server {
    private static String ip = "localhost";
    private static int port = 10002;
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ServerSocket server = new ServerSocket(port);
        /*
        创建zk连接
         */
        ZooKeeper zooKeeper = new ZooKeeper("node002:2181,node003:2181,node004:2181", 10000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            }
        });
        latch.await();
        /*
        向zk注册服务，写入url
         */
        zooKeeper.create("/myprovider/server", (ip + ":" + port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        for (; ; ) {
            Socket socket = server.accept();
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            br.write("远程服务的端口：" + port);
            br.newLine();
            br.flush();
        }
    }
}
