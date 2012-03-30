/**
 * 
 */
package cn.game189.sms;

import com.k99k.tools.encrypter.Encrypter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 短代计费代码.
 * 
 * <pre>
 * 使用方法：
 * 1.在AndroidManifest.xml中添加: 
 * {@code 
 * <!-- android:screenOrientation指定是否横屏,删除即为自适应，
 * android:theme="@android:style/Theme.Dialog"
 * 设定Activity为弹窗(Dialog)方式,不设置则为全屏方式(部分游戏只适用全屏方式)；-->
 * <activity android:name="cn.game189.sms.SMS"  android:theme="@android:style/Theme.Dialog" ></activity> 
 * 
 * <!-- 声明权限 -->
 * <uses-permission android:name="android.permission.SEND_SMS" /> 
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" /> 
 * }
 * 
 * 2.在需要时调用静态方法SMS.toSMS(Activity a)即可弹出短信发送窗口;
 * 3.saveIni和getIniXXX方法可存储和更新短信发送后的效果;
 * 4.可修改sendOK,sendErr,sendCancel方法;
 * 5.使用前需要修改DEST_NUM,TXT_SMS等信息;
 * </pre>
 * 
 * @author keel
 *
 */
public class SMS  extends Activity{

	/**
	 * 目的号码
	 */
	private static String DEST_NUM = "[请使用规范对应费用的短代目的号码]";
	/**
	 * 短信内容
	 */
	private static String TXT_SMS = "[请使用平台短代串码]";
	/**
	 * 提示语
	 */
	private static String TXT_TIP = "[请说明计费点内容和购买效果],此操作将发送一条计费短信,收取您x元费用,是否确认?";
	/**
	 * 短代是否已发的记录标识
	 */
	private static String STR_CHECK = "CHECK_SMS";
	/**
	 * 确定发送短信按钮文字 
	 */
	private static  String TXT_BT1 = "确定";
	/**
	 * 取消发送短信按钮文字
	 */
	private static  String TXT_BT2 = "取消";
	/**
	 * 发送中提示
	 */
	private static  String TXT_SENDING = "短信发送中,请稍侯......";
	/**
	 * 发送成功提示
	 */
	private static  String TXT_SENT = "发送成功!";
	/**
	 * 发送失败提示
	 */
	private static  String TXT_ERR = "发送失败!请确认手机短信功能正常,内存空间足够.";
	
	
	private static final String TAG = "SMS";
	
	/**
	 * 回传发送结果的SMSListener
	 */
	private static SMSListener smsListener;
	
	private static Activity actv;
	
	private static SharedPreferences ini;
	private Button bt1;
	private Button bt2;
	private TextView txt1;
	private SmsManager smsm=SmsManager.getDefault();
	private final static String SENT = "KEEL_SMS_SENT";
	private final static int SMS_CANCEL = 102;
	private final static int SMS_SENT_OK = 103;
	private final static int SMS_SENT_ERR = 104;
	private final static int SMS_END = 105;
	private static int SMS_CLOSE = SMS_CANCEL;
	private final int WARP = FrameLayout.LayoutParams.WRAP_CONTENT;
	private final int FILL = FrameLayout.LayoutParams.FILL_PARENT;
	private boolean isReg = false;
	/**
	 * <pre>
	 * 错误码:
	 * 0.初始值或取消发送
	 * 1.已计过费
	 * 2.发送短信成功
	 * -1.发送失败
	 * -2.无卡
	 * -3.非电信卡
	 * -4.获取终端码失败
	 * -5.保存计费点失败
	 * -6.获取存档数据有误
	 * -7.获取存档时读终端码失败
	 * -8.获取存档时feeName不匹配
	 * -9.获取存档时终端码不匹配
	 * -10.获取存档发生异常 
	 * -11.feeName不合法
	 * </pre>
	 */
	private static int result = 0;
	public final static int RE_ALREADY_FEE = 1;
	public final static int RE_SMS_SENT = 2;
	public final static int RE_INIT = 0;
	public final static int RE_SEND_ERR = -1;
	public final static int RE_NO_CARD = -2;
	public final static int RE_NO_TELECOM = -3;
	public final static int RE_ERR_NO_IMEI = -4;
	public final static int RE_ERR_SAVE = -5;
	public final static int RE_ERR_READ_DATA = -6;
	public final static int RE_ERR_READ_NO_IMEI = -7;
	public final static int RE_ERR_READ_FEENAME = -8;
	public final static int RE_ERR_READ_IMEI = -9;
	public final static int RE_ERR_READ = -10;
	public final static int RE_ERR_SAVE_FEENAME = -11;
	
	/**
	 * 回传短信发送结果的标记
	 */
	private static final int RE = 10; 
	
	/**
	 * 避免高速点击的锁
	 */
	private static boolean lock = false;
	
	/**
	 * @param a 发起调用的Activity
	 */
	private static void toSMS(Activity a){
    	Intent intent = new Intent();
		intent.setClass(a, SMS.class);
		a.startActivityForResult(intent,SMS.RE);
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout layout = new LinearLayout(this);
		setContentView(layout);
		
		//短信提示界面样式
		layout.setOrientation(LinearLayout.VERTICAL);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FILL,WARP);
		//params.setMargins(20, 20, 20, 20);
		params.gravity = Gravity.CENTER;
		layout.setLayoutParams(params);
		layout.setPadding(10, 10, 10, 10);
		layout.setBackgroundColor(Color.argb(100, 80, 80, 80));
		
		LinearLayout up = new LinearLayout(this);
		FrameLayout.LayoutParams p_up = new FrameLayout.LayoutParams(FILL,WARP);
		up.setLayoutParams(p_up);
		up.setBackgroundColor(Color.argb(255, 36, 36, 36));
		up.setPadding(15, 15, 15, 15);
		txt1 = new TextView(this);
		ViewGroup.LayoutParams ww = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		txt1.setLayoutParams(ww);
		txt1.setText(TXT_TIP);
		txt1.setTextColor(Color.argb(255, 255, 255, 255));
		up.addView(txt1);
		layout.addView(up);
		
		LinearLayout down = new LinearLayout(this);
		down.setLayoutParams(p_up);
		down.setBackgroundColor(Color.argb(255, 36, 36, 36));
		bt1 = new Button(this);
		bt1.setLayoutParams(ww);
		bt1.setPadding(10, 10, 10, 10);
		bt1.setText(TXT_BT1);
		bt1.setTextColor(Color.argb(255, 0, 0, 0));
		bt1.setBackgroundColor(Color.argb(255, 255, 255, 255));
		bt1.setWidth(100);
		down.addView(bt1);
		LinearLayout empty = new LinearLayout(this);
		LinearLayout.LayoutParams p_empty = new LinearLayout.LayoutParams(WARP,WARP,1);
		empty.setLayoutParams(p_empty);
		empty.setWeightSum(1);
		down.addView(empty);
		bt2 = new Button(this);
		bt2.setLayoutParams(ww);
		bt2.setPadding(10, 10, 10, 10);
		bt2.setText(TXT_BT2);
		bt1.setTextColor(Color.argb(255, 0, 0, 0));
		bt2.setBackgroundColor(Color.argb(255, 255, 255, 255));
		bt2.setWidth(90);
		down.addView(bt2);
		layout.addView(down);
		
		bt1.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bt1.setVisibility(View.GONE);
				txt1.setText(TXT_SENDING);
				bt2.setVisibility(View.GONE);
				new SendThread().start();
			}
        }); 
		
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mHandler.sendEmptyMessage(SMS_CLOSE);
			}
		});
		//Log.i(TAG, System.currentTimeMillis()+"SMS started.");
	}
	
	private boolean checkIMSI(){
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  
		/** 获取SIM卡的IMSI码 
		 * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志， 
		 * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成， 
		 * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成， 
		 * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。 
		 * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可 
		 */
		String imsi = telManager.getSubscriberId();
		if (imsi != null) {
			if (imsi.startsWith("46003")) {
				// 中国电信
				return true;
			}else{
				result = RE_NO_TELECOM;
			}
//			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
//				// 中国移动
//			} else if (imsi.startsWith("46001")) {
//				// 中国联通
//			} else if (imsi.startsWith("46003")) {
//				// 中国电信
//			}
		}else{
			result = RE_NO_CARD;
		}
		return false;
	}
	
	private BroadcastReceiver smsCheck = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				result = RE_SMS_SENT;
				Log.i(TAG, "SMS send ok");
				SMS_CLOSE = SMS_END;
				mHandler.sendEmptyMessage(SMS_SENT_OK);
				break;
			default:
				result = RE_SEND_ERR;
				Log.e(TAG, "SMS send err:" + getResultCode());
				mHandler.sendEmptyMessage(SMS_SENT_ERR);
				break;
			}
		}
	};
	
	private class SendThread extends Thread{

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			//是否电信卡
			if (checkIMSI() && saveFee("test_save_egame")) {
				// 发短信
				PendingIntent sentPI = PendingIntent.getBroadcast(SMS.this, 0,
						new Intent(SENT), 0);
				registerReceiver(smsCheck, new IntentFilter(SENT));
				isReg = true;
				smsm.sendTextMessage(DEST_NUM, null,TXT_SMS, sentPI, null);
			}else{
				mHandler.sendEmptyMessage(SMS_SENT_ERR);
			}
		}

	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation 
	            == Configuration.ORIENTATION_LANDSCAPE) {
	        //当前为横屏
	    }
	    else if (this.getResources().getConfiguration().orientation 
	            == Configuration.ORIENTATION_PORTRAIT) {
	        //当前为竖屏
	    }
	    //检测实体键盘的状态：推出或者合上    
	    if (newConfig.hardKeyboardHidden 
	            == Configuration.HARDKEYBOARDHIDDEN_NO){ 
	        //实体键盘处于推出状态
	    } 
	    else if (newConfig.hardKeyboardHidden
	            == Configuration.HARDKEYBOARDHIDDEN_YES){ 
	        //实体键盘处于合上状态
	    }
	}
	
	/**
	 * 发送成功
	 */
	private void sendOK(){
//		Log.i(TAG, "sent OK.");
//		SMS.saveIni(STR_CHECK, true);
		//"test_save_egame"
		if((!saveFee("remove_test_save_egame")) || (!saveFee(STR_CHECK))){
			smsListener.smsFail(STR_CHECK,result);
			return;
		}
		Intent i = new Intent();
		i.putExtra("re", "sent");
		SMS.this.setResult(SMS.RE,i);
		bt1.setVisibility(View.INVISIBLE);
		bt2.setVisibility(View.VISIBLE);
		txt1.setText(TXT_SENT);
		bt1.setClickable(false);
		bt2.setText("关闭");
		smsListener.smsOK(STR_CHECK);
	}
	
	/**
	 * 发送失败
	 */
	private void sendErr(){
		Intent i2 = new Intent();
		i2.putExtra("re", "err");
		SMS.this.setResult(SMS.RE,i2);
		bt1.setVisibility(View.INVISIBLE);
		bt2.setVisibility(View.VISIBLE);
		txt1.setText(TXT_ERR);
		bt1.setClickable(false);
		bt2.setText("关闭");
		smsListener.smsFail(STR_CHECK,result);
	}
	
	/**
	 * 取消发送
	 */
	private void sendCancel(){
		Intent i3 = new Intent();
		i3.putExtra("re", "cancel");
		setResult(RE,i3);
		lock = false;
		finish();
	}
	
	private void end(){
		Intent i3 = new Intent();
		i3.putExtra("re", "end");
		setResult(RE,i3);
		lock = false;
		finish();
	}
	
	/**
	 * 消息处理Handler,用于更新界面
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SMS_SENT_OK:
				sendOK();
				break;
			case SMS_SENT_ERR:
				sendErr();
				break;
			case SMS_CANCEL:
				sendCancel();
				break;
			case SMS_END:
				end();
				break;
			}
			
		}
	};
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.KEYCODE_BACK && e.getAction() == KeyEvent.ACTION_UP) {
			mHandler.sendEmptyMessage(SMS_CANCEL);
			return true;
		}
		return false;
	}
	
	public static int getResult(){
		return result;
	}
	
	private static String getIMEI(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	
	/**
	 * 验证计费点
	 * @param feeName 计费点标识符
	 * @param activity Activity 不能为null
	 * @param listener SMSListener接口,处理发送成功和失败的操作,不能为null
	 * @param feeCode 短代代码
	 * @param tip 短代提示语
	 * @param okInfo 短代发送成功的提示语
	 * @return 是否计过费
	 */
	public static boolean checkFee(String feeName,Activity activity,SMSListener sListener,String feeCode,String tip,String okInfo){
		if (lock) {
			return true;
		}else{
			lock = true;
		}
		//初始化状态
		result = RE_INIT;
		actv = activity;
		if (isFee(feeName)) {
			result = RE_ALREADY_FEE;
			lock = false;
			return true;
		}
		// 根据费用大小确定目的号码
		String fee = feeCode.substring(0,2);
		DEST_NUM = "106598110"+fee;
		// 短代位置标识字符串，用于区分不同的计费点,其他的计费点需要修改此标识
		STR_CHECK = feeName;
		// 短代串
		TXT_SMS = feeCode;
		// 短代计费前的提示语
		TXT_TIP = tip;
		// 计费成功的提示语
		TXT_SENT = okInfo;
		//smsListener获取结果
		smsListener = sListener;
		toSMS(actv);
		return false;
	}

	/**
	 * 保存计费成功结果,注意feeName不能包含#号,否则直接返回false
	 * @param feeName
	 * @param context Context
	 */
	private static boolean saveFee(String feeName){
		if (feeName.indexOf("#")>=0) {
			result = RE_ERR_SAVE_FEENAME;
			return false;
		}
		
		try {
			if (ini==null) {
				ini = actv.getSharedPreferences("EGAME_SMS",0);
			}
			SharedPreferences.Editor editor = ini.edit();
			//判断是否为去除测试保存
			if (feeName.equals("remove_test_save_egame")) {
				editor.remove("test_save_egame");
				editor.commit();
				return true;
			}
			String imei = getIMEI(actv);
			if (imei == null) {
				result = RE_ERR_NO_IMEI;
				return false;
			}
			String save = feeName+"#"+imei+"#"+System.currentTimeMillis();
			String enc = Encrypter.encrypt(save);
			editor.putString(feeName, enc);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, "saveFee error.",e);
			result = RE_ERR_SAVE;
			return false;
		}
		return true;
	}
	
	/**
	 * 判断是否已扣费
	 * @param feeName
	 * @param context Context
	 */
	private static boolean isFee(String feeName){
		try {
			if (ini==null) {
				ini = actv.getSharedPreferences("EGAME_SMS",0);
			}
			String imei = getIMEI(actv);
			if (imei == null) {
				result = RE_ERR_READ_NO_IMEI;
				return false;
			}
			String fee = ini.getString(feeName, "");
			if (fee.equals("")) {
				return false;
			}
			String des = Encrypter.decrypt(fee);
			String[] sa = des.split("#");
			if (sa.length != 3) {
				result = RE_ERR_READ_DATA;
				return false;
			}
			if (!feeName.equals(sa[0])) {
				result = RE_ERR_READ_FEENAME;
				return false;
			}
			if (!imei.equals(sa[1])) {
				result = RE_ERR_READ_IMEI;
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, "isFee error.",e);
			result = RE_ERR_READ;
			return false;
		}
		return true;
	}
	/*
	public static void saveIni(String key,String value){
		SharedPreferences.Editor editor = ini.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void saveIni(String key,int value){
		SharedPreferences.Editor editor = ini.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void saveIni(String key,long value){
		SharedPreferences.Editor editor = ini.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static void saveIni(String key,boolean value){
		SharedPreferences.Editor editor = ini.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void saveIni(String[] key,String[] value){
		SharedPreferences.Editor editor = ini.edit();
		for (int i = 0; i < value.length; i++) {
			editor.putString(key[i], value[i]);
		}
		editor.commit();
	}

	public static void saveIni(String[] key,int[] value){
		SharedPreferences.Editor editor = ini.edit();
		for (int i = 0; i < value.length; i++) {
			editor.putInt(key[i], value[i]);
		}
		editor.commit();
	}

	public static void saveIni(String[] key,long[] value){
		SharedPreferences.Editor editor = ini.edit();
		for (int i = 0; i < value.length; i++) {
			editor.putLong(key[i], value[i]);
		}
		editor.commit();
	}

	public static void saveIni(String[] key,boolean[] value){
		SharedPreferences.Editor editor = ini.edit();
		for (int i = 0; i < value.length; i++) {
			editor.putBoolean(key[i], value[i]);
		}
		editor.commit();
	}

	public static String getIniString(String key,String defValue,Context context){
		if (ini==null) {
			ini = context.getSharedPreferences("SMS",0);//PreferenceManager.getDefaultSharedPreferences(context);
		}
		return ini.getString(key, defValue);
	}

	public static boolean getIniBoolean(String key,boolean defValue,Context context){
		if (ini==null) {
			ini = context.getSharedPreferences("SMS",0);//PreferenceManager.getDefaultSharedPreferences(context);
		}
		return ini.getBoolean(key, defValue);
	}

	public static int getIniInt(String key,int defValue,Context context){
		if (ini==null) {
			ini = context.getSharedPreferences("SMS",0);//PreferenceManager.getDefaultSharedPreferences(context);
		}
		return ini.getInt(key, defValue);
	}

	public static long getIniLong(String key,long defValue,Context context){
		if (ini==null) {
			ini = context.getSharedPreferences("SMS",0);//PreferenceManager.getDefaultSharedPreferences(context);
		}
		return ini.getLong(key, defValue);
	}*/
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}


//	/**
//	 * @param listener the listener to set
//	 */
//	public final void setListener(SMSListener listener) {
//		this.listener = listener;
//	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (isReg) {
			unregisterReceiver(smsCheck);
		}
		super.onDestroy();
	}
}
