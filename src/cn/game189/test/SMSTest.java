/**
 * 
 */
package cn.game189.test;

import cn.game189.sms.SMS;
import cn.game189.sms.SMSListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 短代代码使用样例,解决了以下问题：
 * 单机游戏置换存档破解;
 * 修改短信中心破解;
 * 无卡或废卡破解;
 * 双卡双待手机使用移动卡破解;
 * 过快点击造成重复计费;
 * 内存不足或机器状态不对导致无法存档造成重复计费;
 * 注意:使用最低需要android1.6,即<uses-sdk android:minSdkVersion="4" />
 * 
 * <pre>
 * 使用方法：
 * 1.在AndroidManifest.xml中添加: 
 * {@code 
 * 
 * <!-- android:screenOrientation指定是否横屏,删除即为自适应，
 * android:theme="@android:style/Theme.Dialog"
 * 设定Activity为弹窗(Dialog)方式,不设置则为全屏方式(部分游戏只适用全屏方式)；-->
 * <activity android:name="cn.game189.sms.SMS"  android:theme="@android:style/Theme.Dialog" ></activity> 
 * 
 * <!-- 声明权限 -->
 * <uses-permission android:name="android.permission.SEND_SMS" /> 
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" /> 
 * 
 * }
 * 
 * 2.创建一个或多个SMSListener处理不同的计费点发送结果;
 * 3.调表静态方法SMS.checkFee,注意feeName参数为计费点标识,每个计费点必须不同
 * 4.可调用SMS.getResult()随时查看当前错误码.
 * </pre>
 * 
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
 * @author keel
 * 
 */
public class SMSTest extends Activity implements SMSListener {

	/**
	 * 
	 */
	public SMSTest() {
		
	}
	
	private Button bt1;
	
	private Button bt2;
	
	/**
	 * 游戏计费存档
	 */
	SharedPreferences saveLoad;
	/**
	 * 计费存档名称
	 */
	private static final String SAVE_LOAD_NAME = "EGAME_SMS";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		setContentView(layout);
		ViewGroup.LayoutParams ww = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		bt1 = new Button(this);
		bt1.setLayoutParams(ww);
		bt1.setText("计费点A");
		layout.addView(bt1);
		
		
		bt2 = new Button(this);
		bt2.setLayoutParams(ww);
		bt2.setText("计费点B");
		layout.addView(bt2);
		
		
		//初始化计费存档
		saveLoad = this.getSharedPreferences(SAVE_LOAD_NAME,0);
		
		
		//两个计费点测试按钮
		bt1.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				checkFeeA();
			}
        }); 
		
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkFeeB();
			}
		});
	}
	
	

	void checkFeeA() {
		String feeName = "mode_A";
		/*
		 * 验证计费点
		 * @param feeName 计费点标识符,不可含有#号,每个计费点必须不同
		 * @param activity Activity 不能为null
		 * @param listener SMSListener接口,处理发送成功和失败的操作,不能为null
		 * @param fee 费用，单位元
		 * @param feeCode 短代代码
		 * @param tip 短代提示语
		 * @param okInfo 短代发送成功的提示语
		 * @return 是否计过费
		 */
		if (SMS.checkFee(feeName, this, this, 3, "032xxxxxxxxxxxxxxxxxxxxxxxxxxxxx00000000000", "开启\"xxx-A\",点击确定将会发送一条3元短信,不含信息费.", "发送成功!已成功解锁!")) {
			this.smsOK(feeName);
		}
	}
	
	void checkFeeB() {
		String feeName = "mode_B";
		if (SMS.checkFee(feeName, this, this, 2, "032xxxxxxxxxxxxxxxxxxxxxxxxxxxxx00000000000", "开启\"xxx-B\",点击确定将会发送一条2元短信,不含信息费.", "发送成功!已成功解锁!")) {
			this.smsOK(feeName);
		}
	}



	/**
	 * 已计费成功的处理
	 * @param feeName 对应当前的SMS.STR_CHECK值
	 * @see cn.game189.sms.SMSListener#smsOK(java.lang.String)
	 */
	public void smsOK(String feeName) {
		//关卡打开后续代码
		Log.i("SMSListener", "模式"+feeName+"已计费完成,关卡已打开.");
	}



	/**
	 * 发送失败,错误码含义如下:
	 * <pre>
	 * 1 - 无卡
	 * 2
	 * 
	 * </pre>
	 * @param feeName 对应当前的SMS.STR_CHECK值
	 * @see cn.game189.sms.SMSListener#smsFail(int)
	 */
	public void smsFail(String feeName,int errorCode) {
		Log.e("SMSListener", "计费失败!模式:"+feeName+" 错误码:"+errorCode);
		//其他错误处理操作
	}


}
