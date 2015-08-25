package com.cyou.wg.sns.gs.core.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import com.cyou.wg.sns.gs.core.exception.CyouSysException;

/**
 * 使用AES算法加密解密的工具类
 * @author Administrator
 *
 */
public class AESUtil {
	public static KeyGenerator keyGenerator;
	
	public static Key key;
	
	public static Cipher encode;
	public static Cipher decode;
	
	static  {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("not sopport aes");
		}
		
	}
	
	private static void init() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		key = keyGenerator.generateKey();
		encode = Cipher.getInstance("AES");
		encode.init(Cipher.ENCRYPT_MODE, key);
		decode = Cipher.getInstance("AES");
		decode.init(Cipher.DECRYPT_MODE, key);
	}
	
	public static byte[] encode(byte[] src) {
		try {
			return encode.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("aes encode fail");
		}
	}
	
	public static byte[] decode(byte[] src) {
		try {
			return decode.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("aes decode fail");
		}
	}
}
