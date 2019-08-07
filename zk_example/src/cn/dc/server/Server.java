package cn.dc.server;

import org.apache.zookeeper.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * ������
 * ��zkע��url��
 * ���ü��ӵ�����������
 * ���տͻ���������ͻ��˷��ض˿�����
 */
public class Server {
    private static String ip = "localhost";
    private static int port = 10002;
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ServerSocket server = new ServerSocket(port);
        /*
        ����zk����
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
        ��zkע�����д��url
         */
        zooKeeper.create("/myprovider/server", (ip + ":" + port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        for (; ; ) {
            Socket socket = server.accept();
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            br.write("Զ�̷���Ķ˿ڣ�" + port);
            br.newLine();
            br.flush();
        }
    }
}
