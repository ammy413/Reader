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

//TODO:后续考虑，如果一行文字超过2个屏幕怎么处理
public class RBookActivity extends Activity {
    RandomAccessFile rbook_buffer;
    // 书名
    String bookname;
    String filename;
    //文字高度、宽度
    int fontHeight;
    int fontWidth;
    // 屏幕高度、宽度
    int screenHeight;
    int screenWidth;
    // 屏幕行数、每行字数
    int screenLines;
    int lineFontNums;

    // 屏幕文字个数
    int screenFontNums;
    int nextPageQswz = 0;
    //
    InputStream rbook_in;
    BufferedReader ebook_buffer;
    int nextPageLines = 0;
    String nextPageInfo = "";
    int nextPageLeftFrontPageLines = 0;

    HashMap<Integer, Integer> marks = new HashMap<Integer, Integer>();

    // 当前 页码
    int currentPageNo = 1;
    int totalPageNo = 0;

    // 显示内容的文本框
    TextView rtv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbook);

        // 获取书名，从目录中传过来的。
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
                        // 当前是第一页，就不往前翻了
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

        System.out.println("文字高度：" + fontHeight + "；文字宽度：" + fontWidth);


        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        System.out.println("屏幕高度：" + screenHeight + "；屏幕宽度：" + screenWidth);

        int textWidth = rtv_content.getMeasuredWidth();
        int textHeight = rtv_content.getMeasuredHeight();

        System.out.println("电子书高度：" + textHeight + "；电子书宽度：" + textWidth);

        textWidth = rtv_content.getWidth();
        textHeight = rtv_content.getHeight();
        System.out.println("电子书高度：" + textHeight + "；电子书宽度：" + textWidth);

        screenLines = (int) Math.floor(screenHeight * 1.0 / fontHeight);
        lineFontNums = (int) Math.floor(screenWidth * 1.0 / fontWidth);

        System.out.println("屏幕可显示文本行数：" + screenLines + "；每行字数：" + lineFontNums);

        String a = "啊";
        System.out.println("getTextViewLength:" + getTextWidth("你好，社会！") + "：fontWidth：" +
                (fontWidth * 6));

        // 读文件初始化
        // 先把文件写到sd卡上，可以
        initBook(bookname);

        try {
//          以只读的方式，将文件从SD卡读出来
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
        // 在屏幕中间显示一个进度条
        ProgressBar pgb = new ProgressBar(this);

        //整个Activity布局的最终父布局,参见参考资料
        FrameLayout rootFrameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        pgb.setVisibility(View.VISIBLE);
        this.addContentView(pgb, layoutParams);
        //写到sd卡上
        try {
            System.out.println("写书吧");
            InputStream in = getResources().getAssets().open("ebooks/" + bookname);
            EbookUtil.createSDDir("ebooks");
            boolean isExists = EbookUtil.isFileExists("ebooks/" + bookname);
            System.out.println("isExists : " + isExists);
            if (isExists)
                return;
            // 写到sd卡上
            EbookUtil.write2SDCard("ebooks/", bookname, in);
            System.out.println("写完了");
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
        // 根据pageNo，取startNo
        // 从数据库里取，数据库里存储：bookname,pageno,startno,fontsize（字体，在初始化之后不允许修改）
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
                // 一行行的读，转
                line = new String(line.getBytes("ISO-8859-1"), "GBK");
                // 一行能对应屏幕几行
                lineLength = line.length();
                // 一行文字的宽度
                float lineWidth = getTextWidth(line);
                // 文本宽度除以屏幕宽度，上取整为占用屏幕文字数
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
                        // lineWidth转换为length
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
        // 根据pageNo，取startNo
        // 从数据库里取，数据库里存储：bookname,pageno,startno,fontsize（字体，在初始化之后不允许修改）

        if (marks.isEmpty()) {
            startNo = 0;
        } else {
            startNo = marks.get(pageNo);
        }

        System.out.println("当前页：" + pageNo + "，起始位置：" + startNo);

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
                // 一行行的读，转
                line = new String(line.getBytes("ISO-8859-1"), "GBK");
                // 一行能对应屏幕几行
                lineLength = line.length();
                // 一行文字的宽度
                float lineWidth = getTextWidth(line);
                // 文本宽度除以屏幕宽度，上取整为占用屏幕文字数
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
                        // lineWidth转换为length
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
                        // 只取line的一行
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

    //据说更准确
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

    // 独立线程，读文件，并记录每一页的页码、起始位置、终止位置到数据库中（测试，放到HashMap）中
    Runnable readPageNo = new Runnable() {
        @Override
        public void run() {
            // 先读一下数据库，有没有到最后一页
            RandomAccessFile book_buffer;

            StringBuffer sb = new StringBuffer();

            try {
                book_buffer = new RandomAccessFile(new File(Environment.getExternalStorageDirectory() + "/ebooks/" + bookname), "r");
                // 先存储一条页码为1，起始位置为0的数据
                // 文本中一行文字占用的文字个数
                int lineLength = 0;
                // 文本中一行文字对应到屏幕上的行数
                int lineNumber = 0;
                // 当前页的在屏幕中的文本总行数
                int currentPageLines = 0;
                // 当前页码
                int currentPageNo = 1;
                // 当前页的起始字节位置
                int startNums = 0;
                // 文本中的一行文字
                String line = null;
                // 跳过N个字节，到达本页起始位置
                while (true) {
                    book_buffer.seek(startNums);
                    // 开始读取文本数据

                    // 这个while，一次一页
                    while ((line = book_buffer.readLine()) != null) {
                        if (line == null) {
                            break;
                        }
                        // 读完该页
                        // 读一行文本，转为汉字
                        line = new String(line.getBytes("ISO-8859-1"), "GBK");

                        // 计算文本中的文字个数
                        lineLength = line.length();

                        // 计算一行文字的占用的屏幕宽度
                        float lineWidth = getTextWidth(line);

                        // 文本宽度除以屏幕宽度，上取整为占用屏幕行数
                        lineNumber = (int) Math.ceil(lineWidth * 1.0 / screenWidth);
                        // 如果占用屏幕0行，说明是换行符，也占用一行
                        if (lineNumber == 0) {
                            lineNumber = 1;
                        }
                        // 累计当前页文本行数
                        currentPageLines += lineNumber;

                        // 当前页文本行数，超过屏幕可容纳文本行数，即可结束本页内容，进入下页。处理本页的文本
                        if (currentPageLines >= screenLines) {

                            // 该页完成，该做一些事情
                            if (currentPageLines == screenLines) {
                                // 如果相等，那么正好容纳该行文字
                                sb.append(line);
                                // 再读2行，看是否为空白
                            } else {
                                // 如果当前页码已经大于屏幕允许页码，那么截取能留下的文本，以计算当前页可容纳字节数
                                // 计算该行能显示的文本
                                String canLeftWords;
                                // lineWidth转换为length
                                // 计算一个文字的平均宽度
                                float avgFontWidth = (float) ((lineWidth * 1.0) / lineLength);

                                int leftIndex = 0;
                                if (avgFontWidth > 0) {
                                    // 屏幕宽度除以文字宽度，下取整以截留字符串
                                    leftIndex = (int) Math.floor(screenWidth * 1.0 / avgFontWidth);
                                }
                                if (leftIndex > 0) {
                                    canLeftWords = line.substring(0, leftIndex - 1);
                                } else {
                                    canLeftWords = "";
                                }

                                sb.append(canLeftWords);
                            }
                            // 读完第一页，可记录第2页的参数；读完第2页，可记录第3页的参数，依次类推

                            // 页码加1
                            currentPageNo++;
                            totalPageNo = currentPageNo;
                            // 页起始位置落地
                            int currentPageByteLength = EbookUtil.getWordCountRegex(sb.toString());

                            startNums += currentPageByteLength;
                            // 计算字符串长度，加上startNums即为下一页的起始位置
                            // savePageNos(bookname, currentPageNo, startNo, fontsize);

                            // 下一页数据准备
                            // 当前页行数清零
                            currentPageLines = 0;

                            sb.setLength(0);
                            marks.put(currentPageNo, startNums);
                            break;
                        } else {
                            sb.append(line);
                            sb.append("\r\n");
                        }

                    }
                    // 读文件结束，跳出循环
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
            // 先读一下数据库，有没有到最后一页
            String line = null;
            RandomAccessFile rbook_buffer2;

            StringBuffer sb = new StringBuffer();

            try {
                rbook_buffer2 = new RandomAccessFile(new File(Environment.getExternalStorageDirectory() + "/ebooks/" + bookname), "r");
                // 先存储一条页码为1，起始位置为0的数据

                // 文本中的一行文字的文字个数
                int lineLength = 0;
                // 文本中的一行文字，对应到屏幕上的行数
                int lineNumber = 0;
                // 当前页文字行数
                int currentPageLines = 0;
                // 当前页码
                int currentPageNo = 1;
                // 当前页起始字节位置
                int startNums = 0;

                String nextPageInfo = "";
                int nextPageLeftFrontPageLines = 0;

                rbook_buffer2.seek(startNums);
                while ((line = rbook_buffer2.readLine()) != null) {
                    // 读完该页

                    if (nextPageLeftFrontPageLines > 0) {
                        sb.append(nextPageInfo);
                        sb.append("\r\n");
                        sb.append("\r\n");

                        currentPageLines += nextPageLeftFrontPageLines + 2;

                        nextPageLeftFrontPageLines = 0;
                        nextPageInfo = "";
                    }

                    line = new String(line.getBytes("ISO-8859-1"), "GBK");
                    // 一行能对应屏幕几行
                    lineLength = line.length();
                    // 一行文字的宽度
                    float lineWidth = getTextWidth(line);
                    // 文本宽度除以屏幕宽度，上取整为占用屏幕文字数
                    lineNumber = (int) Math.ceil(lineWidth * 1.0 / screenWidth);
                    // lineNumber = (int) Math.ceil(lineLength * 1.0 / lineFontNums);
                    if (lineNumber == 0) {
                        lineNumber = 1;
                    }
                    currentPageLines += lineNumber;

                    if (currentPageLines >= screenLines) {

                        // 该页完成，该做一些事情
                        if (currentPageLines == screenLines) {
                            nextPageInfo = "";
                            nextPageLeftFrontPageLines = 0;
                            sb.append(line);
                        } else {
                            // 只取line的一行
//                            nextPageLeftFrontPageLines = currentPageLines - screenLines;
                            // 计算该行能显示的文本
                            String canLeftWords;
                            // lineWidth转换为length
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

                            startNums -= 4;// 下一页会多拼上两个换行符，占4个字节
                        }
//                        sb.append(line);
                        // 读完第一页，可记录第2页的参数；读完第2页，可记录第3页的参数，依次类推
                        // 数据初始化
                        // 页码加1
                        currentPageNo++;
                        // 页起始位置落地
                        int currentPageByteLength = EbookUtil.getWordCountRegex(sb.toString());
                        startNums += currentPageByteLength;

                        // 计算字符串长度，加上startNums即为下一页的起始位置
                        // savePageNos(bookname, currentPageNo, startNo, fontsize);

                        // 当前页行数清零
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
