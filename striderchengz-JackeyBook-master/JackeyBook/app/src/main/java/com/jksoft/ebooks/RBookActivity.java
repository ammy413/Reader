package com.jksoft.ebooks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

//TODO:�������ǣ����һ�����ֳ���2����Ļ��ô����
public class RBookActivity extends Activity {
    RandomAccessFile rbook_buffer;
    // ����
    String bookname;
    String filename;
    //���ָ߶ȡ����
    int fontHeight;
    int fontWidth;
    // ��Ļ�߶ȡ����
    int screenHeight;
    int screenWidth;
    // ��Ļ������ÿ������
    int screenLines;
    int lineFontNums;

    // ��Ļ���ָ���
    int screenFontNums;
    int nextPageQswz = 0;
    //
    InputStream rbook_in;
    BufferedReader ebook_buffer;
    int nextPageLines = 0;
    String nextPageInfo = "";
    int nextPageLeftFrontPageLines = 0;

    HashMap<Integer, Integer> marks = new HashMap<Integer, Integer>();

    // ��ǰ ҳ��
    int currentPageNo = 1;
    int totalPageNo = 0;

    // ��ʾ���ݵ��ı���
    TextView rtv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbook);

        // ��ȡ��������Ŀ¼�д������ġ�
        Intent intent = getIntent();
        filename = intent.getStringExtra("filename");
        bookname = intent.getStringExtra("bookname");

        rtv_content = (TextView) findViewById(R.id.rtv_bookcontent);
        float textsize = rtv_content.getTextSize();

        rtv_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float x = event.getX();

                float midLine = screenWidth / 2;
                System.out.println("x : " + x);
                System.out.println("midLine : " + midLine);
                System.out.println("currentPageNo : " + currentPageNo);
                System.out.println("TotalPageNo : " + totalPageNo);

                if (x < midLine) {
                    currentPageNo--;
                    if (currentPageNo < 1) {
                        // ��ǰ�ǵ�һҳ���Ͳ���ǰ����
                        currentPageNo = 1;
                        return false;
                    }
                } else if (x > midLine) {
                    currentPageNo++;
                    if (currentPageNo > totalPageNo) {
                        currentPageNo = totalPageNo;
                        return false;
                    }
                }

                String s = readAPageContent(rbook_buffer, currentPageNo);

                rtv_content.setText(s);
                return false;
            }
        });
        //
        fontHeight = getFontHeight(textsize);
        fontWidth = getFontWidth(textsize);

        System.out.println("���ָ߶ȣ�" + fontHeight + "�����ֿ�ȣ�" + fontWidth);


        DisplayMetrics dm = new DisplayMetrics();
        //��ȡ��Ļ��Ϣ
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        System.out.println("��Ļ�߶ȣ�" + screenHeight + "����Ļ��ȣ�" + screenWidth);

        int textWidth = rtv_content.getMeasuredWidth();
        int textHeight = rtv_content.getMeasuredHeight();

        System.out.println("������߶ȣ�" + textHeight + "���������ȣ�" + textWidth);

        textWidth = rtv_content.getWidth();
        textHeight = rtv_content.getHeight();
        System.out.println("������߶ȣ�" + textHeight + "���������ȣ�" + textWidth);

        screenLines = (int) Math.floor(screenHeight * 1.0 / fontHeight);
        lineFontNums = (int) Math.floor(screenWidth * 1.0 / fontWidth);

        System.out.println("��Ļ����ʾ�ı�������" + screenLines + "��ÿ��������" + lineFontNums);

        String a = "��";
        System.out.println("getTextViewLength:" + getTextWidth("��ã���ᣡ") + "��fontWidth��" +
                (fontWidth * 6));

        // ���ļ���ʼ��
        // �Ȱ��ļ�д��sd���ϣ�����
        initBook(bookname);

        try {
//          ��ֻ���ķ�ʽ�����ļ���SD��������
            rbook_buffer = new RandomAccessFile(new File(Environment.getExternalStorageDirectory() + "/ebooks/" + bookname), "r");
            String s = readFirstPage(rbook_buffer);
            rtv_content.setText(s);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread markBooksThread = new Thread(readPageNo);

        marks.put(1, 0);
        markBooksThread.start();

    }

    public void initBook(String bookname) {
        // ����Ļ�м���ʾһ��������
        ProgressBar pgb = new ProgressBar(this);

        //����Activity���ֵ����ո�����,�μ��ο�����
        FrameLayout rootFrameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        pgb.setVisibility(View.VISIBLE);
        this.addContentView(pgb, layoutParams);
        //д��sd����
        try {
            System.out.println("д���");
            InputStream in = getResources().getAssets().open("ebooks/" + bookname);
            EbookUtil.createSDDir("ebooks");
            boolean isExists = EbookUtil.isFileExists("ebooks/" + bookname);
            System.out.println("isExists : " + isExists);
            if (isExists)
                return;
            // д��sd����
            EbookUtil.write2SDCard("ebooks/", bookname, in);
            System.out.println("д����");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pgb.setVisibility(View.GONE);
        }

    }

    public String readFirstPage(RandomAccessFile book_buffer) {
        return readAPageContent(book_buffer, currentPageNo);
    }

    public String readAPageContent(RandomAccessFile book_buffer, int pageNo) {
        StringBuffer sb = new StringBuffer();
        int startNo = 0;
        // ����pageNo��ȡstartNo
        // �����ݿ���ȡ�����ݿ���洢��bookname,pageno,startno,fontsize�����壬�ڳ�ʼ��֮�������޸ģ�
        if (pageNo == 0) {
            pageNo = 1;
        }
        if (marks.isEmpty()) {
            startNo = 0;
        } else {
            startNo = marks.get(pageNo);
        }


        try {
            int currentPageLines = 0;
            String line;
            int lineNumber, lineLength;

            book_buffer.seek(startNo);

            while ((line = book_buffer.readLine()) != null) {
                // һ���еĶ���ת
                line = new String(line.getBytes("ISO-8859-1"), "GBK");
                // һ���ܶ�Ӧ��Ļ����
                lineLength = line.length();
                // һ�����ֵĿ��
                float lineWidth = getTextWidth(line);
                // �ı���ȳ�����Ļ��ȣ���ȡ��Ϊռ����Ļ������
                lineNumber = (int) Math.ceil(lineWidth * 1.0 / screenWidth);

                if (lineNumber == 0) {
                    lineNumber = 1;
                }
                currentPageLines += lineNumber;

                if (currentPageLines >= screenLines) {
                    System.out.println("page_last_line : *" + line);
                    if (currentPageLines == screenLines) {
                        sb.append(line);
                    } else {
                        String canLeftWords;
                        // lineWidthת��Ϊlength
                        float avgFontWidth = (float) ((lineWidth * 1.0) / lineLength);

                        int leftIndex = 0;
                        if (avgFontWidth > 0) {
                            leftIndex = (int) Math.floor(screenWidth * 1.0 / avgFontWidth);
                        }
                        if (leftIndex > 0) {
                            canLeftWords = line.substring(0, leftIndex - 1);
                        } else {
                            canLeftWords = "";
                        }

                        sb.append(canLeftWords);
                        System.out.println("page_left_words : *" + canLeftWords);
                    }

                    break;
                } else {
                    sb.append(line);
                    sb.append("\r\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = sb.toString();
        return result;
    }

    public String readAPageContent_Bak(RandomAccessFile book_buffer, int pageNo) {
        StringBuffer sb = new StringBuffer();
        int pageEndNums;
        int startNo = 0;
        // ����pageNo��ȡstartNo
        // �����ݿ���ȡ�����ݿ���洢��bookname,pageno,startno,fontsize�����壬�ڳ�ʼ��֮�������޸ģ�

        if (marks.isEmpty()) {
            startNo = 0;
        } else {
            startNo = marks.get(pageNo);
        }

        System.out.println("��ǰҳ��" + pageNo + "����ʼλ�ã�" + startNo);

        try {
            int currentPageLines = 0;
            String line;
            int lineNumber, lineLength;


            if (nextPageLeftFrontPageLines > 0) {
                System.out.println("ok, I get the next page content first show : " + nextPageLeftFrontPageLines + ", content : " + nextPageInfo);
                sb.append(nextPageInfo);
                sb.append("\r\n");
                sb.append("\r\n");
                currentPageLines += nextPageLeftFrontPageLines;
                currentPageLines += 2;
            }

            nextPageInfo = "";
            nextPageLeftFrontPageLines = 0;

            book_buffer.seek(startNo);
            while ((line = book_buffer.readLine()) != null) {
                // һ���еĶ���ת
                line = new String(line.getBytes("ISO-8859-1"), "GBK");
                // һ���ܶ�Ӧ��Ļ����
                lineLength = line.length();
                // һ�����ֵĿ��
                float lineWidth = getTextWidth(line);
                // �ı���ȳ�����Ļ��ȣ���ȡ��Ϊռ����Ļ������
                lineNumber = (int) Math.ceil(lineWidth * 1.0 / screenWidth);

                // lineNumber = (int) Math.ceil(lineLength * 1.0 / lineFontNums);

                if (lineNumber == 0) {
                    lineNumber = 1;
                }
                currentPageLines += lineNumber;
                if (currentPageLines >= screenLines) {
                    if (currentPageLines == screenLines) {
                        nextPageInfo = "";
                        sb.append(line);
                    } else {

                        String canLeftWords;
                        // lineWidthת��Ϊlength
                        float avgFontWidth = lineWidth / lineLength;

                        int leftIndex = 0;
                        if (avgFontWidth > 0) {
                            leftIndex = (int) Math.floor(screenWidth * 1.0 / avgFontWidth);
                        }
                        if (leftIndex > 0) {
                            canLeftWords = line.substring(0, leftIndex);
                        } else {
                            canLeftWords = "";
                        }

                        nextPageInfo = line.replace(canLeftWords, "");
                        nextPageLeftFrontPageLines = (int) Math.ceil(getTextWidth(nextPageInfo) * 1.0 / screenWidth);
                        sb.append(canLeftWords);
                        System.out.println("avgFontWidth:" + avgFontWidth);
                        System.out.println("lineWidth:" + lineWidth);
                        System.out.println("ScreenWidth:" + screenWidth);
                        System.out.println("lineLength:" + lineLength);
                        System.out.println("leftIndex : " + leftIndex);
                        System.out.println("canLeftWords:" + canLeftWords);
                        System.out.println("nextPageLeftFrontPageLines:" + nextPageLeftFrontPageLines);
                        System.out.println("nextPageInfo:" + nextPageInfo);
                        /*
                        // ֻȡline��һ��
                        nextPageLeftFrontPageLines = currentPageLines - screenLines;
                        System.out.println("nextPageLeftFrontPageLines:" + nextPageLeftFrontPageLines);
                        canLeftWords = line.substring(0, lineFontNums);
                        System.out.println("canLeftWords:" + canLeftWords);
                        nextPageInfo = line.replace(canLeftWords, "");
                        sb.append(canLeftWords);
                        System.out.print("nextPageInfo:" + nextPageInfo);
                        */
                    }
                    break;
                } else {
                    sb.append(line);
                    sb.append("\r\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = sb.toString();
        System.out.println("pageEndBytes: " + (EbookUtil.getWordCountRegex(result)));
        return result;
    }

    //��˵��׼ȷ
    public int getFontWidth(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();

        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    public int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();

        return (int) Math.ceil(fm.descent - fm.top);
    }

    public float getTextWidth(String text) {
        Paint pFont = new Paint();
        pFont.setTextSize(rtv_content.getTextSize());
        return pFont.measureText(text);
    }

    public float getTextHeight(String text) {
        Paint pFont = new Paint();
        pFont.setTextSize(rtv_content.getTextSize());
        System.out.println("Paint.measureText: " + pFont.measureText(text));
        return pFont.measureText(text);
    }

    private void createProgressBar() {

    }

    // �����̣߳����ļ�������¼ÿһҳ��ҳ�롢��ʼλ�á���ֹλ�õ����ݿ��У����ԣ��ŵ�HashMap����
    Runnable readPageNo = new Runnable() {
        @Override
        public void run() {
            // �ȶ�һ�����ݿ⣬��û�е����һҳ
            RandomAccessFile book_buffer;

            StringBuffer sb = new StringBuffer();

            try {
                book_buffer = new RandomAccessFile(new File(Environment.getExternalStorageDirectory() + "/ebooks/" + bookname), "r");
                // �ȴ洢һ��ҳ��Ϊ1����ʼλ��Ϊ0������
                // �ı���һ������ռ�õ����ָ���
                int lineLength = 0;
                // �ı���һ�����ֶ�Ӧ����Ļ�ϵ�����
                int lineNumber = 0;
                // ��ǰҳ������Ļ�е��ı�������
                int currentPageLines = 0;
                // ��ǰҳ��
                int currentPageNo = 1;
                // ��ǰҳ����ʼ�ֽ�λ��
                int startNums = 0;
                // �ı��е�һ������
                String line = null;
                // ����N���ֽڣ����ﱾҳ��ʼλ��
                while (true) {
                    book_buffer.seek(startNums);
                    // ��ʼ��ȡ�ı�����

                    // ���while��һ��һҳ
                    while ((line = book_buffer.readLine()) != null) {
                        if (line == null) {
                            break;
                        }
                        // �����ҳ
                        // ��һ���ı���תΪ����
                        line = new String(line.getBytes("ISO-8859-1"), "GBK");

                        // �����ı��е����ָ���
                        lineLength = line.length();

                        // ����һ�����ֵ�ռ�õ���Ļ���
                        float lineWidth = getTextWidth(line);

                        // �ı���ȳ�����Ļ��ȣ���ȡ��Ϊռ����Ļ����
                        lineNumber = (int) Math.ceil(lineWidth * 1.0 / screenWidth);
                        // ���ռ����Ļ0�У�˵���ǻ��з���Ҳռ��һ��
                        if (lineNumber == 0) {
                            lineNumber = 1;
                        }
                        // �ۼƵ�ǰҳ�ı�����
                        currentPageLines += lineNumber;

                        // ��ǰҳ�ı�������������Ļ�������ı����������ɽ�����ҳ���ݣ�������ҳ������ҳ���ı�
                        if (currentPageLines >= screenLines) {

                            // ��ҳ��ɣ�����һЩ����
                            if (currentPageLines == screenLines) {
                                // �����ȣ���ô�������ɸ�������
                                sb.append(line);
                                // �ٶ�2�У����Ƿ�Ϊ�հ�
                            } else {
                                // �����ǰҳ���Ѿ�������Ļ����ҳ�룬��ô��ȡ�����µ��ı����Լ��㵱ǰҳ�������ֽ���
                                // �����������ʾ���ı�
                                String canLeftWords;
                                // lineWidthת��Ϊlength
                                // ����һ�����ֵ�ƽ�����
                                float avgFontWidth = (float) ((lineWidth * 1.0) / lineLength);

                                int leftIndex = 0;
                                if (avgFontWidth > 0) {
                                    // ��Ļ��ȳ������ֿ�ȣ���ȡ���Խ����ַ���
                                    leftIndex = (int) Math.floor(screenWidth * 1.0 / avgFontWidth);
                                }
                                if (leftIndex > 0) {
                                    canLeftWords = line.substring(0, leftIndex - 1);
                                } else {
                                    canLeftWords = "";
                                }

                                sb.append(canLeftWords);
                            }
                            // �����һҳ���ɼ�¼��2ҳ�Ĳ����������2ҳ���ɼ�¼��3ҳ�Ĳ�������������

                            // ҳ���1
                            currentPageNo++;
                            totalPageNo = currentPageNo;
                            // ҳ��ʼλ�����
                            int currentPageByteLength = EbookUtil.getWordCountRegex(sb.toString());

                            startNums += currentPageByteLength;
                            // �����ַ������ȣ�����startNums��Ϊ��һҳ����ʼλ��
                            // savePageNos(bookname, currentPageNo, startNo, fontsize);

                            // ��һҳ����׼��
                            // ��ǰҳ��������
                            currentPageLines = 0;

                            sb.setLength(0);
                            marks.put(currentPageNo, startNums);
                            break;
                        } else {
                            sb.append(line);
                            sb.append("\r\n");
                        }

                    }
                    // ���ļ�����������ѭ��
                    if (line == null) {
                        System.out.println("over");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable readPageNo_Bak = new Runnable() {
        @Override
        public void run() {
            // �ȶ�һ�����ݿ⣬��û�е����һҳ
            String line = null;
            RandomAccessFile rbook_buffer2;

            StringBuffer sb = new StringBuffer();

            try {
                rbook_buffer2 = new RandomAccessFile(new File(Environment.getExternalStorageDirectory() + "/ebooks/" + bookname), "r");
                // �ȴ洢һ��ҳ��Ϊ1����ʼλ��Ϊ0������

                // �ı��е�һ�����ֵ����ָ���
                int lineLength = 0;
                // �ı��е�һ�����֣���Ӧ����Ļ�ϵ�����
                int lineNumber = 0;
                // ��ǰҳ��������
                int currentPageLines = 0;
                // ��ǰҳ��
                int currentPageNo = 1;
                // ��ǰҳ��ʼ�ֽ�λ��
                int startNums = 0;

                String nextPageInfo = "";
                int nextPageLeftFrontPageLines = 0;

                rbook_buffer2.seek(startNums);
                while ((line = rbook_buffer2.readLine()) != null) {
                    // �����ҳ

                    if (nextPageLeftFrontPageLines > 0) {
                        sb.append(nextPageInfo);
                        sb.append("\r\n");
                        sb.append("\r\n");

                        currentPageLines += nextPageLeftFrontPageLines + 2;

                        nextPageLeftFrontPageLines = 0;
                        nextPageInfo = "";
                    }

                    line = new String(line.getBytes("ISO-8859-1"), "GBK");
                    // һ���ܶ�Ӧ��Ļ����
                    lineLength = line.length();
                    // һ�����ֵĿ��
                    float lineWidth = getTextWidth(line);
                    // �ı���ȳ�����Ļ��ȣ���ȡ��Ϊռ����Ļ������
                    lineNumber = (int) Math.ceil(lineWidth * 1.0 / screenWidth);
                    // lineNumber = (int) Math.ceil(lineLength * 1.0 / lineFontNums);
                    if (lineNumber == 0) {
                        lineNumber = 1;
                    }
                    currentPageLines += lineNumber;

                    if (currentPageLines >= screenLines) {

                        // ��ҳ��ɣ�����һЩ����
                        if (currentPageLines == screenLines) {
                            nextPageInfo = "";
                            nextPageLeftFrontPageLines = 0;
                            sb.append(line);
                        } else {
                            // ֻȡline��һ��
//                            nextPageLeftFrontPageLines = currentPageLines - screenLines;
                            // �����������ʾ���ı�
                            String canLeftWords;
                            // lineWidthת��Ϊlength
                            float avgFontWidth = lineWidth / lineLength;

                            int leftIndex = 0;
                            if (avgFontWidth > 0) {
                                leftIndex = (int) Math.floor(screenWidth * 1.0 / avgFontWidth);
                            }
                            if (leftIndex > 0) {
                                canLeftWords = line.substring(0, leftIndex);
                            } else {
                                canLeftWords = "";
                            }

                            nextPageInfo = line.replace(canLeftWords, "");
                            nextPageLeftFrontPageLines = (int) Math.ceil(getTextWidth(nextPageInfo) * 1.0 / screenWidth);
                            sb.append(canLeftWords);

                            startNums -= 4;// ��һҳ���ƴ���������з���ռ4���ֽ�
                        }
//                        sb.append(line);
                        // �����һҳ���ɼ�¼��2ҳ�Ĳ����������2ҳ���ɼ�¼��3ҳ�Ĳ�������������
                        // ���ݳ�ʼ��
                        // ҳ���1
                        currentPageNo++;
                        // ҳ��ʼλ�����
                        int currentPageByteLength = EbookUtil.getWordCountRegex(sb.toString());
                        startNums += currentPageByteLength;

                        // �����ַ������ȣ�����startNums��Ϊ��һҳ����ʼλ��
                        // savePageNos(bookname, currentPageNo, startNo, fontsize);

                        // ��ǰҳ��������
                        currentPageLines = 0;

                        sb.setLength(0);

                        marks.put(currentPageNo, startNums);
                    } else {
                        sb.append(line);
                        sb.append("\r\n");
                    }
                }
                System.out.println(marks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
