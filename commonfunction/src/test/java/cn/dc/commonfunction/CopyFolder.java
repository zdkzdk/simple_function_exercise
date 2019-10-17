package cn.dc.commonfunction;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

public class CopyFolder {
    /**
     * 复制文件夹
     */
    @Test
    public void testCopyFolder() {
        File folder = new File("D:\\hwi - 副本");
        String outPath = "C:\\Users\\Administrator\\Desktop\\1223";
        copyFolder(folder, outPath);
    }

    private void copyFolder(File folder, String outPath) {
        /*
        使用recursion
            创建复制一个文件夹和里面文件的方法，然后判断里面的文件是否是文件夹，如果是文件夹，递归调用自己
         */
        //坑！！！先把根目录创建了，因为createNewFile这个方法不会自动创建自己的父目录，跟mkdirs不一样。
        File outFileRoot = new File(outPath);
        if (!outFileRoot.exists()) outFileRoot.mkdirs();
        //获取文件夹下所有文件
        File[] files = folder.listFiles();
        //遍历files，如果是普通文件，直接复制，文件夹调用自己
        Arrays.stream(files).forEach((file -> {
            if (file.isDirectory()) {
                File outFile = new File(outPath + "/" + file.getName());
                if (!outFile.exists()) outFile.mkdirs();

                copyFolder(file, outPath + "/" + file.getName());
            } else if (file.isFile()) {
                File outFile = new File(outPath + "/" + file.getName());
                if (!outFile.exists()) {
                    try {
                        outFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                copySingleFile(file.getPath(), outPath + "/" + file.getName());
            }
        }));
    }

    private void copySingleFile(String sourcePath, String outPath) {
        try (OutputStream osFile = new FileOutputStream(outPath);
             InputStream isFile = new FileInputStream(sourcePath);) {
            byte[] bytes = new byte[2048];
            int flag = -1;
            while ((flag = isFile.read(bytes)) != -1) {
                osFile.write(bytes, 0, flag);
                osFile.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
