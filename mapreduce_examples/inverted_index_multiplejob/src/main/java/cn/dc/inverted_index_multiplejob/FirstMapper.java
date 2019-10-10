package cn.dc.inverted_index_multiplejob;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.Arrays;

public class FirstMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    String fileName;
    Text keyOut = new Text();
    IntWritable valueOut = new IntWritable(1);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        /*
        从切片信息中获取文件的名字。切片信息从context中获取
         */
        FileSplit split = (FileSplit) context.getInputSplit();
        this.fileName = split.getPath().getName();
    }
    /*
    把一个单词数据转为 单词--文件名 1 的形式
         note：
            keyOut.set返回值为void，所以set语句不能直接写在write方法中
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] words = line.split(" ");
        Arrays.asList(words).forEach(word -> {
            keyOut.set(word + "--" + fileName);
            try {
                context.write(keyOut, valueOut);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
