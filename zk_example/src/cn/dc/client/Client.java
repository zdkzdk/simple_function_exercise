package cn.dc.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ������
 * ���ӵ�zk�����ü��ӵ㣬��ȡ���µ�urlList
 * ʹ����ѭ��������urlList�е�url
 */
public class Client {
    private static CountDownLatch latch = new CountDownLatch(1);
    private static List<String> urlList = new ArrayList<>();
    private static ZooKeeper zooKeeper;
    private static int index = 0;
    public Client() throws IOException, InterruptedException, KeeperException {

    }
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        while (true) {
            /*
                ����zk����
             */
            zooKeeper = new ZooKeeper("node002:2181,node003:2181,node004:2181", 10000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
            getUrlList();
            if (urlList.size() != 0) {
                String string = urlList.get(index % urlList.size());
                String stringDo = string.substring(string.indexOf(":") + 1);
                Socket socket = new Socket("localhost", Integer.parseInt(stringDo));
                InputStream in = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                System.out.println(br.readLine());
                index++;
            }
            Thread.sleep(2000);
        }
    }

    private static void getUrlList() throws KeeperException, InterruptedException {
         /*
            ��zk��ȡurlList
             */
        List<String> list = zooKeeper.getChildren("/myprovider", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        getUrlList();
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        for (String url : list) {
            urlList.add(new String(zooKeeper.getData("/myprovider/" + url,null,null)));
        }
    }
}
