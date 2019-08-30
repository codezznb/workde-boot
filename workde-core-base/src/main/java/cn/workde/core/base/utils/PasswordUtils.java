package cn.workde.core.base.utils;

import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * @author zhujingang
 * @date 2019/8/30 3:10 PM
 */
public class PasswordUtils {

	public static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2) plainText = "0" + plainText;
			hexString.append(plainText);
		}
		return hexString.toString().toUpperCase();
	}

	public static String hexMD5(String salt, String src){
		String firstKey = DigestUtils.md5DigestAsHex(salt.getBytes()).substring(0, 8).toUpperCase();
		byte[] bs = firstKey.getBytes();
		byte[] inputArray = src.getBytes();
		try {
			DESKeySpec keySpec = new DESKeySpec(bs);
			IvParameterSpec iv = new IvParameterSpec(bs);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
			Key key = keyFactory.generateSecret(keySpec);

			Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
			enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
			byte[] pasByte = enCipher.doFinal(inputArray);
			return toHexString(pasByte);
		}catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}
