package com.k99k.tools.encrypter;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
import javax.crypto.NoSuchPaddingException;


/**
 * 
 * 使用AES加密与解密(使用非标准的base64返回string),可对String类型进行加密与解密,密文可使用String存储.
 * 注意:密钥key存在默认写死的默认值,需要根据实际情况修改!
 * 
 */
public class Encrypter {
	
	private static Cipher ecipher;

	private static Cipher dcipher;
	
	public static void main(String[] args) {
		String content = "992328328#32413241341234141324142938";
		String key = "weoivI38f_d#$`aZ";
		System.out.println("content:"+content);
		System.out.println("key:"+key);
		if(!Encrypter.setNewKey(key.getBytes())){
			System.out.println("set key error!");
			return;
		}
		String des = Encrypter.encrypt(content);
		System.out.println("des:"+des);
		String src = Encrypter.decrypt(des);
		System.out.println("src:"+src);
	}
	
	/**
	 * 默认用于加解密的key
	 */
	private static byte[] key = new byte[]{79, 13, 33, -66, -58, 103, 3, -34, -45, 53, 9, 45, 28, -124, 50, -2};

	static{
		init();
	}
	
	/**
	 * 初始化
	 */
	private static final void init(){
		try {
			ecipher = Cipher.getInstance("AES");
			dcipher = Cipher.getInstance("AES");
//		KeyGenerator _generator = KeyGenerator.getInstance("AES");
//		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG"); 
//		sr.setSeed(strKey.getBytes("UTF8"));
//		//_generator.init(new SecureRandom(strKey.getBytes()));
//		_generator.init(128,sr);
//		SecretKey key = _generator.generateKey();
			SKey key2 = new SKey("AES","RAW",key);
			ecipher.init(Cipher.ENCRYPT_MODE, key2);
			dcipher.init(Cipher.DECRYPT_MODE, key2);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	private Encrypter(){
		
	}
	
	/**
	 * 设置新的key,必须是16位的byte[]
	 * @param keyBytes
	 * @return key错误时返回错误
	 */
	public static final boolean setNewKey(byte[] keyBytes){
		if (keyBytes.length != 16) {
			return false;
		}
		key = keyBytes;
		init();
		return true;
	}
	
	/**
	 * 加密,失败返回null
	 * @param str
	 * @return
	 */
	public static final byte[] encryptToByte(String str) {
		
		try {
			//return SimpleCrypto.encrypt(this.key, str);

			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");

			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			return enc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}

	/**
	 * 加密并返回非标准base64的string,失败返回null
	 * @param str
	 * @return
	 */
	public static final String encrypt(String str){

		// Encode bytes to base64 to get a string
		return Base64Coder.encode(encryptToByte(str));
	
	}
	
	/**
	 * 使用base64处理string并解密,返回解密后的byte[],失败返回null
	 * @param str
	 * @return
	 */
	public static final byte[] decryptToByte(String str) {
		//return SimpleCrypto.decrypt(this.key, str);
		
		// Decode base64 to get bytes
		byte[] utf8 = null;
		try {
			byte[] dec = Base64Coder.decode(str);

			utf8 = dcipher.doFinal(dec);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return utf8;
	}

	/**
	 * 使用base64处理string并解密，使用utf-8的string输出,失败返回null
	 * @param str
	 * @return
	 */
	public static final String decrypt(String str) {
		// Decode using utf-8
		String de = null;
		try {
			de = new String(decryptToByte(str), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return de;
	}

	
}
