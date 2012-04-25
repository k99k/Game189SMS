/**
 * 
 */
package cn.game189.test;

import cn.game189.sms.SMS;
import cn.game189.sms.SMSListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 爱游戏Android短代代码使用样例,解决了以下问题：
 * 
 * 防止过快点击造成重复计费;
 * 内存不足或机器状态不对导致无法存档造成重复计费;
 * 覆盖升级安装能保留原已计过费的计费点(卸载后会清除);
 * 防止单机游戏置换存档破解;
 * 防止修改短信中心破解;
 * 防止无卡或废卡破解;
 * 防止双卡双待手机使用移动卡破解;
 * 注意:使用最低需要Android1.6t系统,即<uses-sdk android:minSdkVersion="4" />
 * 
 * 
 * <pre>
 * 使用方法：
 * 1.引入sms.jar包;
 * 2.在AndroidManifest.xml中添加: 
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
 * 3.创建一个或多个SMSListener处理不同的计费点发送结果;
 * 4.调用静态方法SMS.checkFee,注意feeName参数为计费点标识(不可含有#号),每个计费点必须不同
 * 5.可调用SMS.getResult()随时查看当前错误码.
 * </pre>
 * 
 * <pre>
 * 错误码(可通过SMS.getResult()随时获取):
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
 * 
 * 
 * 
 * 
 */
public class SMSTest extends Activity implements SMSListener {
	//采用本Activity实现SMSListener接口，必须实现smsOK和smsFail接口

	
	private Button bt1;
	
	private Button bt2;
	
	private Button bt3;
	
	private Button bt5;
	
	private String reLifeFeeName = "reLife";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);
		ViewGroup.LayoutParams ww = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		bt1 = new Button(this);
		bt1.setLayoutParams(ww);
		bt1.setText("关卡A");
		layout.addView(bt1);
		
		
		bt2 = new Button(this);
		bt2.setLayoutParams(ww);
		bt2.setText("关卡B");
		layout.addView(bt2);
		
		bt3 = new Button(this);
		bt3.setLayoutParams(ww);
		bt3.setText("原地复活");
		layout.addView(bt3);
		
		bt5 = new Button(this);
		bt5.setLayoutParams(ww);
		bt5.setText("另一方式");
		layout.addView(bt5);
		
		
		
		
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
		
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//原地复活
				//复活每次计费标识都不同,所以加上毫秒数区别
				SMSTest.this.reLifeFeeName = SMSTest.this.reLifeFeeName+System.currentTimeMillis();
				//不需要判断是否已计费
				SMS.checkFee(SMSTest.this.reLifeFeeName, SMSTest.this, SMSTest.this, "0111C001741102210073711102210072550115174000000000000000000000000000", "使用“原地复活”功能,点击确定将会发送一条1元短信,不含信息费.", "发送成功!已成功复活!");
			}
		});
		
		
		//跳转到第二种调用方式
		bt5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SMSTest.this, SMSTest2.class);
				SMSTest.this.startActivity(intent);
			}
		});
	}
	
	

	/**
	 * 使用本Activity直接实现SMSListener接口的方式
	 */
	void checkFeeA() {
		String feeName = "mode_A";
		
		/*
		 * 验证计费点核心静态方法,自动完成全部短代过程
		 * @param feeName 计费点标识符,不可包含#号,不同计费点此值必须不同
		 * @param activity Activity 不能为null
		 * @param listener SMSListener接口,处理发送成功和失败的操作,不能为null
		 * @param feeCode 短代代码,请登录平台查询产品计费信息并完整复制对应的计费点!!费用按平台上此计费点的对应费用计!
		 * @param tip 短代提示语
		 * @param okInfo 短代发送成功的提示语
		 * @return 返回是否已计过费
		 */
		if (SMS.checkFee(feeName, this, this, "0111C001741102210071271102210070930115174000000000000000000000000000", "开启\"xxx-A\",点击确定将会发送一条1元短信,不含信息费.", "发送成功!已成功解锁!")) {
			//在这里处理该计费点已扣过费后的处理
			Toast.makeText(this, "已计过费，直接进入关卡"+feeName, Toast.LENGTH_SHORT).show();
		}
	}
	
	void checkFeeB() {
		String feeName = "mode_B";
		/*
		 * 验证计费点核心静态方法,自动完成全部短代过程
		 * @param feeName 计费点标识符,不可包含#号,不同计费点此值必须不同
		 * @param activity Activity 不能为null
		 * @param listener SMSListener接口,处理发送成功和失败的操作,不能为null
		 * @param feeCode 短代代码,请登录平台查询产品计费信息并完整复制对应的计费点!!费用按平台上此计费点的对应费用计!
		 * @param tip 短代提示语
		 * @param okInfo 短代发送成功的提示语
		 * @return 返回是否已计过费
		 */
		if (SMS.checkFee(feeName, this, this, "0211C001741102210071271102210070940115174000000000000000000000000000", "开启\"xxx-B\",点击确定将会发送一条2元短信,不含信息费.", "发送成功!已成功解锁!")) {
			//在这里处理该计费点已扣过费后的处理
			Toast.makeText(this, "已计过费，直接进入关卡"+feeName, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * 在弹出计费提供框后，用户点击确定并计费成功后的处理
	 * @param feeName 对应当计费标识值
	 * @see cn.game189.sms.SMSListener#smsOK(java.lang.String)
	 */
	public void smsOK(String feeName) {
		//关卡打开后续代码
		Log.i("SMSListener", "模式"+feeName+"已计费完成,关卡已打开.");
		if (feeName.equals("mode_A")) {
			//打开mode_A计费点后的操作
			Toast.makeText(this, "关卡"+feeName+"开启后的操作", Toast.LENGTH_SHORT).show();
		}else if(feeName.equals("mode_B")){
			//打开mode_B计费点后的操作
			Toast.makeText(this, "第二个关卡"+feeName+"开启后的操作", Toast.LENGTH_SHORT).show();
		}else if(feeName.equals(this.reLifeFeeName)){
			//原地复活
			Toast.makeText(this, "角色原地复活功能实现", Toast.LENGTH_SHORT).show();
		}
	}



	/**
	 * 短代发送失败,错误码含义如下:
	 * <pre>
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
	 * @param feeName 对应当前的SMS.STR_CHECK值
	 * @param errorCode 错误码
	 * @see cn.game189.sms.SMSListener#smsFail(int)
	 */
	public void smsFail(String feeName,int errorCode) {
		Log.e("SMSListener", "计费失败!计费点:"+feeName+" 错误码:"+errorCode);
		//其他错误处理操作,不给道具或不放行关卡
	}



	public void smsCancel(String feeName, int errorCode) {
		Log.e("SMSListener", "用户点击取消!计费点:"+feeName+" 错误码:"+errorCode);
		
	}


}
