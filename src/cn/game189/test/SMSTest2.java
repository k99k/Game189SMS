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
	
	
	
	private Button bt7;
	
	private String reLifeFeeName = "reLife";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);
		ViewGroup.LayoutParams ww = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		
		bt3 = new Button(this);
		bt3.setLayoutParams(ww);
		bt3.setText("关卡C");
		layout.addView(bt3);
		
		bt4 = new Button(this);
		bt4.setLayoutParams(ww);
		bt4.setText("关卡D");
		layout.addView(bt4);
		
		bt5 = new Button(this);
		bt5.setLayoutParams(ww);
		bt5.setText("原地复活");
		layout.addView(bt5);
		
		bt7 = new Button(this);
		bt7.setLayoutParams(ww);
		bt7.setText("返回");
		layout.addView(bt7);
		
		
		//关卡C
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String feeName = "mode_C";
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
				if (SMS.checkFee(feeName, SMSTest2.this,new SMSListener() {
					
					public void smsOK(String feeName) {
						//短代发送成功
						Log.i("SMSListener", "模式"+feeName+"已计费完成,关卡已打开.");
						Toast.makeText(SMSTest2.this, "第二个模式关卡"+feeName+"开启后的操作", Toast.LENGTH_SHORT).show();

					}
					
					public void smsFail(String feeName, int errorCode) {
						//短代发送失败,不给道具或不放行关卡
						Log.e("SMSListener", "计费失败!模式:["+feeName+"] 错误码:"+errorCode);
					}

					public void smsCancel(String feeName, int errorCode) {
						Log.e("SMSListener", "用户点击取消!计费点:"+feeName+" 错误码:"+errorCode);
					}
				},  "0111C001741102210071271102210070930115174000000000000000000000000000", "开启第二种方式"+feeName+",点击确定将会发送一条1元短信,不含信息费.", "发送成功!已成功解锁!")) {
					//已计过费
					Toast.makeText(SMSTest2.this, "已计过费,关卡"+feeName+"开启后的操作", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		//关卡D
		bt4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String feeName = "mode_D";
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
				if (SMS.checkFee(feeName, SMSTest2.this,new SMSListener() {
					
					public void smsOK(String feeName) {
						//短代发送成功
						Log.i("SMSListener", "第二个模式"+feeName+"已计费完成,关卡已打开.");
						Toast.makeText(SMSTest2.this, "第二个模式关卡"+feeName+"开启后的操作", Toast.LENGTH_SHORT).show();
					}
					
					public void smsFail(String feeName, int errorCode) {
						Log.e("SMSListener", "第二个计费失败!模式:["+feeName+"] 错误码:"+errorCode);
					}

					public void smsCancel(String feeName, int errorCode) {
						Log.e("SMSListener", "用户点击取消!计费点:"+feeName+" 错误码:"+errorCode);
						
					}
				}, "0211C001741102210071271102210070940115174000000000000000000000000000", "开启第二种方式"+feeName+",点击确定将会发送一条2元短信,不含信息费.", "发送成功!已成功解锁!")) {
					//已计过费
					Log.i("SMSListener", "第二个模式"+feeName+"已计费完成,关卡已打开.");
				}
			}
		});
		
		//原地复活
		bt5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//复活每次计费标识都不同,所以加上毫秒数区别
				SMSTest2.this.reLifeFeeName = SMSTest2.this.reLifeFeeName+System.currentTimeMillis();
				//直接使用匿名SMSListener,此处不需要判断是否已计过费
				SMS.checkFee(SMSTest2.this.reLifeFeeName, SMSTest2.this,new SMSListener() {
					
					public void smsOK(String feeName) {
						//短代发送成功
						Toast.makeText(SMSTest2.this, "角色原地复活功能实现", Toast.LENGTH_SHORT).show();
					}
					
					public void smsFail(String feeName, int errorCode) {
						Log.e("SMSListener", "原地复活计费失败!模式:["+feeName+"] 错误码:"+errorCode);
					}

					public void smsCancel(String feeName, int errorCode) {
						Log.e("SMSListener", "用户点击取消!计费点:"+feeName+" 错误码:"+errorCode);
					}
				}, "0111C001741102210073711102210072550115174000000000000000000000000000", "使用“原地复活”功能,点击确定将会发送一条1元短信,不含信息费.", "发送成功!已成功复活!");
			}
		});
				
		
		
		//返回
		bt7.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	
	
}
