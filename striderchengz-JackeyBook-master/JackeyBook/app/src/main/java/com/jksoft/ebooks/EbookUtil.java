package com.jksoft.ebooks;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/11.
 */
public class EbookUtil {


    static String SDPATH = "";

    static {
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    public EbookUtil() {
    }

    //创建目录
    public static File createSDDir(String dirName) {
        File file = new File(SDPATH + dirName);
        if (file.exists()) {
            return file;
        }
        file.mkdir();
        return file;
    }

    //创建文件
    public static File createSDFile(String fileName) {
        File file = new File(SDPATH + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //文件是否已经存在
    public static boolean isFileExists(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    //写文件到卡上
    public static File write2SDCard(String path, String fileName, InputStream inputStream) {
        OutputStream outputStream = null;
        File file = null;
        try {
            createSDDir(path);
            file = createSDFile(path + fileName);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while ((inputStream.read(buffer)) != -1) {
                outputStream.write(buffer);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static List<String> getFileNames(String dir) {
        List<String> fileList = new ArrayList<String>();

        File file = new File(dir);
        String fileName;
        // 如果不是目录，那干啥
        if (!file.isDirectory()) {
            fileName = file.getName();
            if (!fileName.toLowerCase().endsWith(".txt")) {
                return fileList;
            }
            fileList.add(fileName);
            return fileList;
        }

        if (file.isDirectory()) {
            addFileNames(file, fileList);
        }

        return fileList;
    }

    public static void addFileNames(File dir, List<String> fileList) {
        if (dir.isFile()) {
            fileList.add(dir.getName());
            return;
        }
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                addFileNames(files[i], fileList);
            }
        }
    }

    /*基本原理是将字符串中所有的非标准字符（双字节字符）替换成两个标准字符（**，或其他的也可以）。这样就可以直接例用length方法获得字符串的字节长度了*/
    public static int getWordCountRegex(String s) {

        String t = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = t.length();
        return length;
    }
}
