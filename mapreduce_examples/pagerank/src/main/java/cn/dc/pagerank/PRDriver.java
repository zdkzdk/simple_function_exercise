package cn.dc.pagerank;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Arrays;

public class PRDriver {
    /*
    计数器的名字，用枚举封装
     */
    public static enum MyCounter {
        my
    }

    public static void main(String[] args) {
        Configuration conf = new Configuration();
        /*
        如果分布式运行，必须打成jar包
         */
        //是否跨平台。是hadoop2.5后出现的，默认为false，如果想运行在window系统中，需要设置为true
        conf.set("mapreduce.app-submission.corss-paltform", "true");
        //这个配置只用于切换分布式到本地单进程模拟运行的配置。
        conf.set("mapreduce.framework.name", "local");
        /*
        收敛标准
         */
        double d = 0.01;
        int i = 0;
        while (true) {
            i++;
            try {
                /*
                计数器，context下所有类都可以取到
                初始值为1，表示循环的顺序号
                 */
                conf.setInt("runCount", i);
                FileSystem fs = FileSystem.get(conf);
                Job job = Job.getInstance(conf);
                job.setJarByClass(PRDriver.class);
                job.setJobName("pr" + i);
                job.setMapperClass(PageRankMapper.class);
                job.setReducerClass(PageRankReduce.class);
                job.setMapOutputKeyClass(Text.class);
                job.setMapOutputValueClass(Text.class);
                //默认按制表符，制表符前面的就是key，后面的是value
                job.setInputFormatClass(KeyValueTextInputFormat.class);

                /*
                数据的输入分两种，第一次是原始数据，从input拿，后面都是从上一次输出拿，即上一次的输出是下一次的输入
                 */
                Path input = new Path("mapreduce_examples/pagerank/data/input");
                if (i > 1) {
                    input = new Path("mapreduce_examples/pagerank/data/input/pr" + (i - 1));
                }
                FileInputFormat.addInputPath(job, input);

                //输出
                Path output = new Path("mapreduce_examples/pagerank/data/input/pr" + i);
                if (fs.exists(output)) {
                    fs.delete(output, true);
                }
                FileOutputFormat.setOutputPath(job, output);
                /*
                提交
                 */
                boolean flag = job.waitForCompletion(true);
                if (flag) {
                    //数据处理成功后拿被my标识的值，实际就是pr的差值
                    long sum = job.getCounters().findCounter(MyCounter.my).getValue();
                    double avgd = sum / 4000.0;
                    if (avgd < d) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
    将源数据中的一行转换为
        元节点 pr值 出链节点
        出链节点1 从元节点获得的增量pr值
        出链节点2 从元节点获得的增量pr值
        ......
     */
    private static class PageRankMapper extends Mapper<Text, Text, Text, Text> {
        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            //从配置文件中取runCount的值，在客户端中已经配过，是i的值，这里如果取不到，就等于1返回
            int runCount = context.getConfiguration().getInt("runCount", 1);

            String page = key.toString();//KEY就是A这个位置的值
            Node node = null;
            if (runCount == 1) {
                //如果是第一次，给一个1+'\t'+B D
                node = Node.fromMR("1.0", value.toString());
            } else {
                node = Node.fromMR(value.toString());
            }
            /*
            toString的结果是1.0+'\t'+B+'\t'+D
            最终输出结果为A 1.0 B D
             */
            context.write(new Text(page), new Text(node.toString()));
            /*
            将一行数据中
            假如adjacentNodeNames数组不为空且长度不为零时结果为真，说明有出链
             */
            if (node.containAdjacentNodes()) {
                //票面值
                double outValue = node.getPageRank() / node.getAdjacentNodeNames().length;
                for (int i = 0; i < node.getAdjacentNodeNames().length; i++) {
                    //节点的名字
                    String outPage = node.getAdjacentNodeNames()[i];
                    /*
                    输出结果：
                     B 0.5
                     D 0.5
                     */
                    context.write(new Text(outPage), new Text(outValue + ""));
                }
            }
        }
    }
    /*
    把mapper提供的数据分成2类，分别处理
    执行一次的输入数据为
        key：节点名
        value：
            A 1.0 B D
            A 0.5
            A 0.2
    ==>
    输入的数据有2种
         A 1.0 B D  带有出链信息的记录
         A 0.5      带有入链节点给A投票的票面值的记录
         A 0.2
    出链信息封装到node中
    票面值累加，存到node中然后计算相邻pr的差值存到计数器中
     */
    private static class PageRankReduce extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            /*
            将输入的2种数据分别处理
                带有出链信息的记录封装到sourceNode
                带有入链节点给A投票的票面值的记录进行累加计算
             */
            double sum = 0d;
            Node sourceNode = null;
            for (Text i : values) {
                Node node = Node.fromMR(i.toString());
                if (node.containAdjacentNodes()) {
                    sourceNode = node;
                } else {
                    sum = sum + node.getPageRank();
                }
            }
            /*
            利用谷歌公式计算新的PR值
            套用公式  (1-d)/n+d*到该页面的入链值和  d为0.85，谷歌给的值，n是页面数
             */
            double newPR = ((1 - 0.85) / 4.0) + (0.85 * sum);
            System.out.println("*********** new pageRank value is " + newPR);
            /*
            计算新旧pr的差值
            将差值存放到计数器中，用来判断是否达到收敛标准
             */
            double d = newPR - sourceNode.getPageRank();

            int j = (int) (d * 1000.0);

            j = Math.abs(j);//相差的值可能是负数，这里给它取了绝对值
            System.out.println(j + "-------------");

            context.getCounter(MyCounter.my).increment(j);

            sourceNode.setPageRank(newPR);
            /*
            输出结果：
                元节点 新pr值 出链节点
             */
            context.write(key, new Text(sourceNode.toString()));
        }
    }
}
/*
元节点的封装  封装了pr值和出链节点
    初始pr=1
    adjacentNodeNames将出链以字符串数组的方式封装
 */
class Node {
    private double pageRank = 1.0;
    private String[] adjacentNodeNames;

    public static final char fieldSeparator = '\t';

    public boolean containAdjacentNodes() {
        return adjacentNodeNames != null && adjacentNodeNames.length > 0;
    }

    /**
     * @param value 格式   pr值 出链节点
     */
    public static Node fromMR(String value) throws IOException {
        /*
        将value按制表符进行切割
        value为空将报异常
         */
        String[] parts = StringUtils.splitPreserveAllTokens(value, fieldSeparator);
        if (parts.length < 1) {
            throw new IOException("Expected 1 or more parts but received" + parts.length);
        }
        /*
        将pr值赋给pageRank属性并创建对象
        将出链节点做成一个数组放到对象中
         */
        Node node = new Node().setPageRank(Double.valueOf(parts[0]));
        if (parts.length > 1) {//大于1说明有出链节点
            node.setAdjacentNodeNames(Arrays.copyOfRange(parts, 1, parts.length));
        }
        return node;
    }

    public static Node fromMR(String v1, String v2) throws IOException {
        return fromMR(v1 + fieldSeparator + v2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(pageRank);

        if (getAdjacentNodeNames() != null) {
            sb.append(fieldSeparator).append(StringUtils.join(getAdjacentNodeNames(), fieldSeparator));
        }
        return sb.toString();
    }
    public double getPageRank() {
        return pageRank;
    }

    public Node setPageRank(double pageRank) {
        this.pageRank = pageRank;
        return this;
    }

    public String[] getAdjacentNodeNames() {
        return adjacentNodeNames;
    }

    public Node setAdjacentNodeNames(String[] adjacentNodeNames) {
        this.adjacentNodeNames = adjacentNodeNames;
        return this;
    }
}
