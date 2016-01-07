package com.twituji.ebook;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.twituji.ebook.helper.MarkHelper;
import com.twituji.ebook.mydialog.MarkDialog;
import com.twituji.ebook.vo.MarkVo;

public class Read extends Activity implements OnClickListener, OnSeekBarChangeListener {

	private static final String TAG = "Read2";
	private static int begin = 0;// ��¼���鼮��ʼλ��
	public static Canvas mCurPageCanvas, mNextPageCanvas;
	private static String word = "";// ��¼��ǰҳ�������
	private int a = 0, b = 0;// ��¼toolpop��λ��
	private TextView bookBtn1, bookBtn2, bookBtn3, bookBtn4;
	private String bookPath;// ��¼�������·��
	private String ccc = null;// ��¼�Ƿ�Ϊ��ݷ�ʽ����
	protected long count = 1;
	private SharedPreferences.Editor editor;
	private ImageButton imageBtn2, imageBtn3_1, imageBtn3_2;
	private ImageButton imageBtn4_1, imageBtn4_2;
	private Boolean isNight; // ����ģʽ,���������
	protected int jumpPage;// ��¼��ת������
	private int light; // ����ֵ
	private WindowManager.LayoutParams lp;
	private TextView markEdit4;
	private MarkHelper markhelper;
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private MarkDialog mDialog = null;
	private Context mContext = null;
	private PageWidget mPageWidget;
	private PopupWindow mPopupWindow, mToolpop, mToolpop1, mToolpop2, mToolpop3, mToolpop4;
	protected int PAGE = 1;
	private BookPageFactory pagefactory;
	private View popupwindwow, toolpop, toolpop1, toolpop2, toolpop3, toolpop4;
	int screenHeight;
	int readHeight; // ��������ʾ�߶�
	int screenWidth;
	private SeekBar seekBar1, seekBar2, seekBar4;
	private Boolean show = false;// popwindow�Ƿ���ʾ
	private int size = 30; // �����С
	private SharedPreferences sp;
	int defaultSize = 0;
	// ʵ����Handler
	public Handler mHandler = new Handler() {
		// �������̷߳�������Ϣ��ͬʱ����UI
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				begin = msg.arg1;
				pagefactory.setM_mbBufBegin(begin);
				pagefactory.setM_mbBufEnd(begin);
				postInvalidateUI();
				break;
			case 1:
				pagefactory.setM_mbBufBegin(begin);
				pagefactory.setM_mbBufEnd(begin);
				postInvalidateUI();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * ��¼���� �����popupwindow
	 */
	private void clear() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		show = false;
		mPopupWindow.dismiss();
		popDismiss();
	}

	/**
	 * ��ȡ�����ļ�������ֵ
	 */
	private void getLight() {
		light = sp.getInt("light", 5);
		isNight = sp.getBoolean("night", false);
	}

	/**
	 * ��ȡ�����ļ��������С
	 */
	private void getSize() {
		size = sp.getInt("size", defaultSize);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// ���尴ť
		case R.id.bookBtn1:
			a = 1;
			setToolPop(a);
			break;
		// ���Ȱ�ť
		case R.id.bookBtn2:
			a = 2;
			setToolPop(a);
			break;
		// ��ǩ��ť
		case R.id.bookBtn3:
			a = 3;
			setToolPop(a);
			break;
		// ��ת��ť
		case R.id.bookBtn4:
			a = 4;
			setToolPop(a);
			break;

		// ҹ��ģʽ��ť
		case R.id.imageBtn2:
			if (isNight) {
				pagefactory.setM_textColor(Color.rgb(28, 28, 28));
				imageBtn2.setImageResource(R.drawable.reader_switch_off);
				isNight = false;
				pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg));
			} else {
				pagefactory.setM_textColor(Color.rgb(128, 128, 128));
				imageBtn2.setImageResource(R.drawable.reader_switch_on);
				isNight = true;
				pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.main_bg));
			}
			setLight();
			pagefactory.setM_mbBufBegin(begin);
			pagefactory.setM_mbBufEnd(begin);
			postInvalidateUI();
			break;
		// ������ǩ��ť
		case R.id.imageBtn3_1:
			SQLiteDatabase db = markhelper.getWritableDatabase();
			try {
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm ss");
				String time = sf.format(new Date());
				db.execSQL("insert into markhelper (path ,begin,word,time) values (?,?,?,?)", new String[] { bookPath, begin + "", word, time });
				db.close();
				Toast.makeText(Read.this, "��ǩ���ӳɹ�", Toast.LENGTH_SHORT).show();
			} catch (SQLException e) {
				Toast.makeText(Read.this, "����ǩ�Ѵ���", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(Read.this, "������ǩʧ��", Toast.LENGTH_SHORT).show();
			}
			mToolpop.dismiss();
			mToolpop3.dismiss();
			break;
		// �ҵ���ǩ��ť
		case R.id.imageBtn3_2:
			SQLiteDatabase dbSelect = markhelper.getReadableDatabase();
			String col[] = { "begin", "word", "time" };
			Cursor cur = dbSelect.query("markhelper", col, "path = '" + bookPath + "'", null, null, null, null);
			Integer num = cur.getCount();
			if (num == 0) {
				Toast.makeText(Read.this, "����û����ǩ", Toast.LENGTH_SHORT).show();
			} else {
				ArrayList<MarkVo> markList = new ArrayList<MarkVo>();
				while (cur.moveToNext()) {
					String s1 = cur.getString(cur.getColumnIndex("word"));
					String s2 = cur.getString(cur.getColumnIndex("time"));
					int b1 = cur.getInt(cur.getColumnIndex("begin"));
					int p = 0;
					int count = 10;
					MarkVo mv = new MarkVo(s1, p, count, b1, s2, bookPath);
					markList.add(mv);
				}
				mDialog = new MarkDialog(this, markList, mHandler, R.style.FullHeightDialog);

				mDialog.setCancelable(false);

				mDialog.setTitle("�ҵ���ǩ");
				mDialog.show();
			}
			dbSelect.close();
			cur.close();
			mToolpop.dismiss();
			mToolpop3.dismiss();
			break;
		case R.id.imageBtn4_1:
			clear();
			pagefactory.setM_mbBufBegin(begin);
			pagefactory.setM_mbBufEnd(begin);
			postInvalidateUI();
			break;
		case R.id.imageBtn4_2:
			clear();
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mContext = getBaseContext();

		WindowManager manage = getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		defaultSize = (screenWidth * 20) / 320;
		readHeight = screenHeight - (50 * screenWidth) / 320;

		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);

		mPageWidget = new PageWidget(this, screenWidth, readHeight);// ҳ��
		setContentView(R.layout.read);
		RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.readlayout);
		rlayout.addView(mPageWidget);

		Intent intent = getIntent();
		bookPath = intent.getStringExtra("aaa");
		ccc = intent.getStringExtra("ccc");

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		mPageWidget.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				boolean ret = false;
				if (v == mPageWidget) {
					if (!show) {

						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							if (e.getY() > readHeight) {// ������Χ�ˣ���ʾ�������������������ҳ
								return false;
							}
							mPageWidget.abortAnimation();
							mPageWidget.calcCornerXY(e.getX(), e.getY());
							pagefactory.onDraw(mCurPageCanvas);
							if (mPageWidget.DragToRight()) {// ��
								try {
									pagefactory.prePage();
									begin = pagefactory.getM_mbBufBegin();// ��ȡ��ǰ�Ķ�λ��
									word = pagefactory.getFirstLineText();// ��ȡ��ǰ�Ķ�λ�õ���������
								} catch (IOException e1) {
									Log.e(TAG, "onTouch->prePage error", e1);
								}
								if (pagefactory.isfirstPage()) {
									Toast.makeText(mContext, "��ǰ�ǵ�һҳ", Toast.LENGTH_SHORT).show();
									return false;
								}
								pagefactory.onDraw(mNextPageCanvas);
							} else {// �ҷ�
								try {
									pagefactory.nextPage();
									begin = pagefactory.getM_mbBufBegin();// ��ȡ��ǰ�Ķ�λ��
									word = pagefactory.getFirstLineText();// ��ȡ��ǰ�Ķ�λ�õ���������
								} catch (IOException e1) {
									Log.e(TAG, "onTouch->nextPage error", e1);
								}
								if (pagefactory.islastPage()) {
									Toast.makeText(mContext, "�Ѿ������һҳ��", Toast.LENGTH_SHORT).show();
									return false;
								}
								pagefactory.onDraw(mNextPageCanvas);
							}
							mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
						}
						editor.putInt(bookPath + "begin", begin).commit();
						ret = mPageWidget.doTouchEvent(e);
						return ret;
					}
				}
				return false;
			}
		});

		setPop();

		// ��ȡ��¼��sharedpreferences�ĸ���״̬
		sp = getSharedPreferences("config", MODE_PRIVATE);
		editor = sp.edit();
		getSize();// ��ȡ�����ļ��е�size��С
		getLight();// ��ȡ�����ļ��е�lightֵ
		count = sp.getLong(bookPath + "count", 1);

		lp = getWindow().getAttributes();
		lp.screenBrightness = light / 10.0f < 0.01f ? 0.01f : light / 10.0f;
		getWindow().setAttributes(lp);
		pagefactory = new BookPageFactory(screenWidth, readHeight);// �鹤��
		if (isNight) {
			pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.main_bg));
			pagefactory.setM_textColor(Color.rgb(128, 128, 128));
		} else {
			pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg));
			pagefactory.setM_textColor(Color.rgb(28, 28, 28));
		}
		begin = sp.getInt(bookPath + "begin", 0);
		try {
			pagefactory.openbook(bookPath, begin);// ��ָ��λ�ô��鼮��Ĭ�ϴӿ�ʼ��
			pagefactory.setM_fontSize(size);
			pagefactory.onDraw(mCurPageCanvas);
		} catch (IOException e1) {
			Log.e(TAG, "�򿪵�����ʧ��", e1);
			Toast.makeText(this, "�򿪵�����ʧ��", Toast.LENGTH_SHORT).show();
		}

		markhelper = new MarkHelper(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pagefactory = null;
		mPageWidget = null;
		finish();
	}

	/**
	 * �ж��Ǵ��ĸ���������READ
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (ccc == null) {
				if (show) {// ���popwindow������ʾ
					popDismiss();
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
					show = false;
					mPopupWindow.dismiss();
				} else {
					Read.this.finish();
				}
			} else {
				if (!ccc.equals("ccc")) {
					if (show) {// ���popwindow������ʾ
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
						show = false;
						mPopupWindow.dismiss();
						popDismiss();
					} else {
						this.finish();
					}
				} else {
					this.finish();
				}
			}
		}
		return true;
	}

	/**
	 * ���Ӷ�menu��ť�ļ���
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (show) {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				show = false;
				mPopupWindow.dismiss();
				popDismiss();

			} else {

				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				show = true;

				pop();
			}
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		switch (seekBar.getId()) {
		// ���������
		case R.id.seekBar1:
			size = seekBar1.getProgress() + 15;
			setSize();
			pagefactory.setM_fontSize(size);
			pagefactory.setM_mbBufBegin(begin);
			pagefactory.setM_mbBufEnd(begin);
			postInvalidateUI();
			break;
		// ���Ƚ�����
		case R.id.seekBar2:
			light = seekBar2.getProgress();
			setLight();
			lp.screenBrightness = light / 10.0f < 0.01f ? 0.01f : light / 10.0f;
			getWindow().setAttributes(lp);
			break;
		// ��ת������
		case R.id.seekBar4:
			int s = seekBar4.getProgress();
			markEdit4.setText(s + "%");
			begin = (pagefactory.getM_mbBufLen() * s) / 100;
			editor.putInt(bookPath + "begin", begin).commit();
			pagefactory.setM_mbBufBegin(begin);
			pagefactory.setM_mbBufEnd(begin);
			try {
				if (s == 100) {
					pagefactory.prePage();
					pagefactory.getM_mbBufBegin();
					begin = pagefactory.getM_mbBufEnd();
					pagefactory.setM_mbBufBegin(begin);
					pagefactory.setM_mbBufBegin(begin);
				}
			} catch (IOException e) {
				Log.e(TAG, "onProgressChanged seekBar4-> IOException error", e);
			}
			postInvalidateUI();
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	/**
	 * popupwindow�ĵ���,������
	 */
	public void pop() {

		mPopupWindow.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 0);
		bookBtn1 = (TextView) popupwindwow.findViewById(R.id.bookBtn1);
		bookBtn2 = (TextView) popupwindwow.findViewById(R.id.bookBtn2);
		bookBtn3 = (TextView) popupwindwow.findViewById(R.id.bookBtn3);
		bookBtn4 = (TextView) popupwindwow.findViewById(R.id.bookBtn4);
		bookBtn1.setOnClickListener(this);
		bookBtn2.setOnClickListener(this);
		bookBtn3.setOnClickListener(this);
		bookBtn4.setOnClickListener(this);
	}

	/**
	 * �ر�55������pop
	 */
	public void popDismiss() {
		mToolpop.dismiss();
		mToolpop1.dismiss();
		mToolpop2.dismiss();
		mToolpop3.dismiss();
		mToolpop4.dismiss();
	}

	/**
	 * ��¼�����ļ�������ֵ�ͺ�����
	 */
	private void setLight() {
		try {
			light = seekBar2.getProgress();
			editor.putInt("light", light);
			if (isNight) {
				editor.putBoolean("night", true);
			} else {
				editor.putBoolean("night", false);
			}
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, "setLight-> Exception error", e);
		}
	}

	/**
	 * ��ʼ������POPUPWINDOW
	 */
	private void setPop() {
		popupwindwow = this.getLayoutInflater().inflate(R.layout.bookpop, null);
		toolpop = this.getLayoutInflater().inflate(R.layout.toolpop, null);
		mPopupWindow = new PopupWindow(popupwindwow, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mToolpop = new PopupWindow(toolpop, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		toolpop1 = this.getLayoutInflater().inflate(R.layout.tool11, null);
		mToolpop1 = new PopupWindow(toolpop1, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		toolpop2 = this.getLayoutInflater().inflate(R.layout.tool22, null);
		mToolpop2 = new PopupWindow(toolpop2, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		toolpop3 = this.getLayoutInflater().inflate(R.layout.tool33, null);
		mToolpop3 = new PopupWindow(toolpop3, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		toolpop4 = this.getLayoutInflater().inflate(R.layout.tool44, null);
		mToolpop4 = new PopupWindow(toolpop4, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * ��¼�����ļ��������С
	 */
	private void setSize() {
		try {
			size = seekBar1.getProgress() + 15;
			editor.putInt("size", size);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, "setSize-> Exception error", e);
		}
	}

	/**
	 * ����popupwindow����ʾ������
	 * 
	 * @param a
	 */
	public void setToolPop(int a) {
		if (a == b && a != 0) {
			if (mToolpop.isShowing()) {
				popDismiss();
			} else {
				mToolpop.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
				// ��������尴ť
				if (a == 1) {
					mToolpop1.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
					seekBar1 = (SeekBar) toolpop1.findViewById(R.id.seekBar1);
					size = sp.getInt("size", 20);
					seekBar1.setProgress((size - 15));
					seekBar1.setOnSeekBarChangeListener(this);
				}
				// ��������Ȱ�ť
				if (a == 2) {
					mToolpop2.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
					seekBar2 = (SeekBar) toolpop2.findViewById(R.id.seekBar2);
					imageBtn2 = (ImageButton) toolpop2.findViewById(R.id.imageBtn2);
					getLight();

					seekBar2.setProgress(light);
					if (isNight) {
						imageBtn2.setImageResource(R.drawable.reader_switch_on);
					} else {
						imageBtn2.setImageResource(R.drawable.reader_switch_off);
					}
					imageBtn2.setOnClickListener(this);
					seekBar2.setOnSeekBarChangeListener(this);
				}
				// �������ǩ��ť
				if (a == 3) {
					mToolpop3.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, toolpop.getHeight());
					imageBtn3_1 = (ImageButton) toolpop3.findViewById(R.id.imageBtn3_1);
					imageBtn3_2 = (ImageButton) toolpop3.findViewById(R.id.imageBtn3_2);
					imageBtn3_1.setOnClickListener(this);
					imageBtn3_2.setOnClickListener(this);
				}
				// �������ת��ť
				if (a == 4) {
					mToolpop4.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
					imageBtn4_1 = (ImageButton) toolpop4.findViewById(R.id.imageBtn4_1);
					imageBtn4_2 = (ImageButton) toolpop4.findViewById(R.id.imageBtn4_2);
					seekBar4 = (SeekBar) toolpop4.findViewById(R.id.seekBar4);
					markEdit4 = (TextView) toolpop4.findViewById(R.id.markEdit4);
					// begin = sp.getInt(bookPath + "begin", 1);
					float fPercent = (float) (begin * 1.0 / pagefactory.getM_mbBufLen());
					DecimalFormat df = new DecimalFormat("#0");
					String strPercent = df.format(fPercent * 100) + "%";
					markEdit4.setText(strPercent);
					seekBar4.setProgress(Integer.parseInt(df.format(fPercent * 100)));
					seekBar4.setOnSeekBarChangeListener(this);
					imageBtn4_1.setOnClickListener(this);
					imageBtn4_2.setOnClickListener(this);
				}
			}
		} else {
			if (mToolpop.isShowing()) {
				// �����ݵļ�¼
				popDismiss();
			}
			mToolpop.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
			// ������尴ť
			if (a == 1) {
				mToolpop1.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
				seekBar1 = (SeekBar) toolpop1.findViewById(R.id.seekBar1);
				size = sp.getInt("size", 20);
				seekBar1.setProgress(size - 15);
				seekBar1.setOnSeekBarChangeListener(this);
			}
			// ������Ȱ�ť
			if (a == 2) {
				mToolpop2.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
				seekBar2 = (SeekBar) toolpop2.findViewById(R.id.seekBar2);
				imageBtn2 = (ImageButton) toolpop2.findViewById(R.id.imageBtn2);
				getLight();
				seekBar2.setProgress(light);

				if (isNight) {
					pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.main_bg));
				} else {
					pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg));
				}

				if (isNight) {
					imageBtn2.setImageResource(R.drawable.reader_switch_on);
				} else {
					imageBtn2.setImageResource(R.drawable.reader_switch_off);
				}
				imageBtn2.setOnClickListener(this);
				seekBar2.setOnSeekBarChangeListener(this);
			}
			// �����ǩ��ť
			if (a == 3) {
				mToolpop3.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
				imageBtn3_1 = (ImageButton) toolpop3.findViewById(R.id.imageBtn3_1);
				imageBtn3_2 = (ImageButton) toolpop3.findViewById(R.id.imageBtn3_2);
				imageBtn3_1.setOnClickListener(this);
				imageBtn3_2.setOnClickListener(this);
			}
			// �����ת��ť
			if (a == 4) {
				mToolpop4.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
				imageBtn4_1 = (ImageButton) toolpop4.findViewById(R.id.imageBtn4_1);
				imageBtn4_2 = (ImageButton) toolpop4.findViewById(R.id.imageBtn4_2);
				seekBar4 = (SeekBar) toolpop4.findViewById(R.id.seekBar4);
				markEdit4 = (TextView) toolpop4.findViewById(R.id.markEdit4);
				// jumpPage = sp.getInt(bookPath + "jumpPage", 1);
				float fPercent = (float) (begin * 1.0 / pagefactory.getM_mbBufLen());
				DecimalFormat df = new DecimalFormat("#0");
				String strPercent = df.format(fPercent * 100) + "%";
				markEdit4.setText(strPercent);
				seekBar4.setProgress(Integer.parseInt(df.format(fPercent * 100)));
				seekBar4.setOnSeekBarChangeListener(this);
				imageBtn4_1.setOnClickListener(this);
				imageBtn4_2.setOnClickListener(this);
			}
		}
		// ��¼�ϴε��������һ��
		b = a;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * ˢ�½���
	 */
	public void postInvalidateUI() {
		mPageWidget.abortAnimation();
		pagefactory.onDraw(mCurPageCanvas);
		try {
			pagefactory.currentPage();
			begin = pagefactory.getM_mbBufBegin();// ��ȡ��ǰ�Ķ�λ��
			word = pagefactory.getFirstLineText();// ��ȡ��ǰ�Ķ�λ�õ���������
		} catch (IOException e1) {
			Log.e(TAG, "postInvalidateUI->IOException error", e1);
		}

		pagefactory.onDraw(mNextPageCanvas);

		mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
		mPageWidget.postInvalidate();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}