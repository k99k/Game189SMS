/**
 * 
 */
package cn.game189.test;

import cn.game189.sms.SMS;
import cn.game189.sms.SMSListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 直接使用匿名SMSListener的方式，不需要此Activity实现SMSListener接口
 * @author keel
 *
 */
public class SMSTest2 extends Activity {
	
	
	private Button bt3;
	private Button bt4;
	private Button bt5;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		setContentView(layout);
		ViewGroup.LayoutParams ww = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		
		bt3 = new Button(this);
		bt3.setLayoutParams(ww);
		bt3.setText("计费点C");
		layout.addView(bt3);
		
		bt4 = new Button(this);
		bt4.setLayoutParams(ww);
		bt4.setText("计费点D");
		layout.addView(bt4);
		
		bt5 = new Button(this);
		bt5.setLayoutParams(ww);
		bt5.setText("返回");
		layout.addView(bt5);
		
		
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String feeName = "mode_C";
				/*
				 * 验证计费点核心静态方法,自动完成全部短代过程,用法可样例源码
				 * @param feeName 计费点标识符,不可包含#号,不同计费点此值必须不同
				 * @param activity Activity 不能为null
				 * @param listener SMSListener接口,处理发送成功和失败的操作,不能为null
				 * @param feeCode 短代代码,请登录平台查询产品计费信息并完整复制对应的计费点!!费用按平台上此计费点的对应费用计!
				 * @param tip 短代提示语
				 * @param okInfo 短代发送成功的提示语
				 * @return 返回是否已计过费
				 */
				if (SMS.checkFee(
						feeName,
						SMSTest2.this,
						new SMSListener() {

							public void smsOK(String feeName) {
								// 短代发送成功
								Log.i("SMSListener", "模式" + feeName
										+ "已计费完成,关卡已打开.");
								Toast.makeText(SMSTest2.this,"第二个模式关卡" + feeName + "开启后的操作",Toast.LENGTH_SHORT).show();

							}

							public void smsFail(String feeName, int errorCode) {
								// 短代发送失败,不给道具或不放行关卡
								Log.e("SMSListener", "计费失败!模式:[" + feeName + "] 错误码:" + errorCode);
							}
						},
						"0111C001741102210071271102210070930115174000000000000000000000000000",
						"开启第二种方式" + feeName + ",点击确定将会发送一条1元短信,不含信息费.",
						"发送成功!已成功解锁!")) {
					// 已计过费
					Log.i("SMSListener", "模式" + feeName + "已计费完成,关卡已打开.");
				}
			}
		});
		
		
		
		bt4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String feeName = "mode_D";
				//直接使用匿名SMSListener
				if (SMS.checkFee(feeName, SMSTest2.this,new SMSListener() {
					
					public void smsOK(String feeName) {
						//短代发送成功
						Log.i("SMSListener", "第二个模式"+feeName+"已计费完成,关卡已打开.");
						Toast.makeText(SMSTest2.this, "第二个模式关卡"+feeName+"开启后的操作", Toast.LENGTH_SHORT).show();
					}
					
					public void smsFail(String feeName, int errorCode) {
						Log.e("SMSListener", "第二个计费失败!模式:["+feeName+"] 错误码:"+errorCode);
					}
				}, "0211C001741102210071271102210070940115174000000000000000000000000000", "开启第二种方式"+feeName+",点击确定将会发送一条2元短信,不含信息费.", "发送成功!已成功解锁!")) {
					//已计过费
					Log.i("SMSListener", "第二个模式"+feeName+"已计费完成,关卡已打开.");
				}
			}
		});
		
		
		bt5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	
	
}
