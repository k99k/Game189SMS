/**
 * 
 */
package cn.game189.sms;

/**
 * 短代发送结果接口
 * @author keel
 *
 */
public interface SMSListener {

	/**
	 * 计费已成功的操作
	 * @param feeName 计费点标识
	 */
	public void smsOK(String feeName);
	
	/**
	 * 发送失败的操作
	 * @param feeName 计费点标识
	 * @param errorCode 错误码
	 */
	public void smsFail(String feeName,int errorCode);
	
//	/**
//	 * 发送取消
//	 * @param feeName
//	 * @param errorCode
//	 */
//	public void smsCancel(String feeName,int errorCode);
	
	
	
}
