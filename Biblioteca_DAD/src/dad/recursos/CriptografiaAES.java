package dad.recursos;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;

/**
 * Aes encryption
 */
public class CriptografiaAES {

	private static SecretKeySpec secretKey;
	private static byte[] key;

	private static String decryptedString;
	private static String encryptedString;

	public static void setKey(String myKey) {

		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			System.out.println(key.length);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			System.out.println(key.length);
			System.out.println(new String(key, "UTF-8"));
			secretKey = new SecretKeySpec(key, "AES");

		} catch (NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(null, "Erro ao criar a chave!");
		} catch (UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(null, "Erro ao criar a chave!");
		}

	}

	public static String getDecryptedString() {
		return decryptedString;
	}

	public static void setDecryptedString(String decryptedString) {
		CriptografiaAES.decryptedString = decryptedString;
	}

	public static String getEncryptedString() {
		return encryptedString;
	}

	public static void setEncryptedString(String encryptedString) {
		CriptografiaAES.encryptedString = encryptedString;
	}

	public static String encrypt(String strToEncrypt) throws Exception {

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		setEncryptedString(Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("ISO-8859-1"))));

		return null;
	}

	public static String decrypt(String strToDecrypt) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		setDecryptedString(new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt))));

		return null;
	}
}
