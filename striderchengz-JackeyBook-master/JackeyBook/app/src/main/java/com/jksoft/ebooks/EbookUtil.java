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

    //����Ŀ¼
    public static File createSDDir(String dirName) {
        File file = new File(SDPATH + dirName);
        if (file.exists()) {
            return file;
        }
        file.mkdir();
        return file;
    }

    //�����ļ�
    public static File createSDFile(String fileName) {
        File file = new File(SDPATH + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //�ļ��Ƿ��Ѿ�����
    public static boolean isFileExists(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    //д�ļ�������
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
        // �������Ŀ¼���Ǹ�ɶ
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

    /*����ԭ���ǽ��ַ��������еķǱ�׼�ַ���˫�ֽ��ַ����滻��������׼�ַ���**����������Ҳ���ԣ��������Ϳ���ֱ������length��������ַ������ֽڳ�����*/
    public static int getWordCountRegex(String s) {

        String t = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = t.length();
        return length;
    }
}
