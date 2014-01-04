package sp.phone.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/***
 * @Description: des
 * @author zhouya
 * @version V1.0
 * 
 */
public class Des {

	public static String enCrypto(String txt, String key)
			throws InvalidKeySpecException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		StringBuffer sb = new StringBuffer();
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		SecretKeyFactory skeyFactory = null;
		Cipher cipher = null;
		try {
			skeyFactory = SecretKeyFactory.getInstance("DES");
			cipher = Cipher.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SecretKey deskey = skeyFactory.generateSecret(desKeySpec);
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] cipherText = cipher.doFinal(txt.getBytes());
		for (int n = 0; n < cipherText.length; n++) {
			String stmp = (java.lang.Integer.toHexString(cipherText[n] & 0XFF));

			if (stmp.length() == 1) {
				sb.append("0" + stmp);
			} else {
				sb.append(stmp);
			}
		}
		return sb.toString().toUpperCase();
	}


	public static String deCrypto(String txt, String key)
			throws InvalidKeyException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		SecretKeyFactory skeyFactory = null;
		Cipher cipher = null;
		try {
			skeyFactory = SecretKeyFactory.getInstance("DES");
			cipher = Cipher.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SecretKey deskey = skeyFactory.generateSecret(desKeySpec);
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		byte[] btxts = new byte[txt.length() / 2];
		for (int i = 0, count = txt.length(); i < count; i += 2) {
			btxts[i / 2] = (byte) Integer.parseInt(txt.substring(i, i + 2), 16);
		}
		return (new String(cipher.doFinal(btxts)));
	}


}