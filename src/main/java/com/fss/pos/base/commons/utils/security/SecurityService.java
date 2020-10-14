package com.fss.pos.base.commons.utils.security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.sun.crypto.provider.SunJCE;

@Component
public class SecurityService {
	
	private static final String DES_ALG = "DESede";
	private static final String AES_ALG = "AES";
	private static final String DES_MODE = "ECB";
	private static final String AES_MODE = "ECB";
	private static final String DES_PADDING = "PKCS7Padding";
	private static final String AES_PADDING = "PKCS5Padding";
	private static final String AES_ENCRYPTTYPE = "1";

	@Autowired
	private  Config config;
	


	static KeyPair keyPair = null;
	public static final int KEY_SIZE = 2048;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private static void generateKeyPair() {
		if (keyPair == null) {
			jCryption jc = new jCryption();
			keyPair = jc.generateKeypair(KEY_SIZE);
		}
	}

	public static KeyPair getKeyPair() {
		if (keyPair == null) {
			generateKeyPair();
		}
		return keyPair;
	}

	private static String digits = "0123456789ABCDEF";
	private static final String HEX_DIGITS = "0123456789abcdef";

	static SunJCE sunjce = new SunJCE();

	/**
	 * Convert a byte array of 8 bit characters into a String.
	 * 
	 * @param bytes
	 *            the array containing the characters
	 * @param length
	 *            the number of bytes to process
	 * @return a String representation of bytes
	 */
	/*public static String byteArrayToString(byte[] bytes, int length) {
		char[] chars = new char[length];

		for (int i = 0; i != chars.length; i++) {
			chars[i] = (char) (bytes[i] & 0xff);
		}

		return new String(chars);
	}*/
	
	public static String byteArrayToString(byte[] bytes, int length) {
		
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[length];

		for (int i = 0; i != chars.length; i++) {
			chars[i] = (char) (bytes[i] & 0xff);
			sb.append(chars);
		}

		return sb.toString();
	}

	/**
	 * To load bouncy castle provider on windows pc static {
	 * Security.addProvider(new BouncyCastleProvider()); }
	 */

	/**
	 * Convert a byte array of 8 bit characters into a String.
	 * 
	 * @param bytes
	 *            the array containing the characters
	 * @param length
	 *            the number of bytes to process
	 * @return a String representation of bytes
	 */
	/*public static String byteArrayToString(byte[] bytes) {
		return byteArrayToString(bytes, bytes.length);
	}*/

	public static String byteArrayToString(byte[] bytes) {		
		return new StringBuilder(byteArrayToString(bytes, bytes.length)).toString();
		 
	}
	
	/**
	 * Convert the passed in String to a byte array by taking the bottom 8 bits
	 * of each character it contains.
	 * 
	 * @param string
	 *            the string to be converted
	 * @return a byte array representation
	 */
	public static byte[] stringToByteArray(String string) {
		byte[] bytes = new byte[string.length()];
		char[] chars = string.toCharArray();

		for (int i = 0; i != chars.length; i++) {
			bytes[i] = (byte) chars[i];
		}

		return bytes;
	}

	/**
	 * Return length many bytes of the passed in byte array as a hex string.
	 * 
	 * @param data
	 *            the bytes to be converted.
	 * @param length
	 *            the number of bytes in the data block to be converted.
	 * @return a hex representation of length bytes of data.
	 */
	public static String byteArrayToHexString(byte[] data, int length) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i != length; i++) {
			int v = data[i] & 0xff;

			buf.append(HEX_DIGITS.charAt(v >> 4));
			buf.append(HEX_DIGITS.charAt(v & 0xf));
		}

		return buf.toString();
	}

	/**
	 * Return the passed in byte array as a hex string.
	 * 
	 * @param data
	 *            the bytes to be converted.
	 * @return a hex representation of data.
	 */
	public static String byteArrayToHexString(byte[] data) {
		return new StringBuilder(byteArrayToHexString(data, data.length)).toString();
	}

	/**
	 * Converts a string of hex digits into a byte array of those digits.
	 * 
	 * @param hex
	 *            - Hex String to be converted.
	 * @return byte array representation of the given hex string.
	 */
	public static byte[] hexStringToByteArray1(String hex) {
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
					16);
		}
		return bts;
	}

	/**
	 * Converts a string of hex digits into a byte array of those digits.
	 * 
	 * @param hex
	 *            - Hex String to be converted.
	 * @return byte array representation of the given hex string.
	 */
	public static byte[] hexStringToByteArray2(String hex) {
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character
					.digit(hex.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Converts a string of hex digits into a byte array of those digits.
	 * 
	 * @param hex
	 *            - Hex String to be converted.
	 * @return byte array representation of the given hex string.
	 */
	public static byte[] hexStringToByteArray3(String hex) {
		byte[] number = new byte[hex.length() / 2];
		int i;
		for (i = 0; i < hex.length(); i += 2) {
			int j = Integer.parseInt(hex.substring(i, i + 2), 16);
			number[i / 2] = (byte) (j & 0x000000ff);
		}
		return number;

	}


	public static String appendBeginEndCertKey(String certData) {
		String begin = "-----BEGIN CERTIFICATE-----" + "\n";
		String end = "\n" + "-----END CERTIFICATE-----" + "\n";
		certData = begin + certData + end;
		return certData;
	}

	public static String encrypt(SecretKey secretkey, String s)
			throws NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IOException {
		Cipher cipher = Cipher.getInstance(DES_ALG);
		cipher.init(1, secretkey);
		String s1 = "";
		try {
			byte abyte0[] = cipher.doFinal(s.getBytes("UTF-8"));
			BASE64Encoder base64encoder = new BASE64Encoder();
			s1 = base64encoder.encode(abyte0);
		} catch (IllegalBlockSizeException illegalblocksizeexception) {
			Log.error("Error while encrypting the data.",
					illegalblocksizeexception);
		} catch (BadPaddingException badpaddingexception) {
			Log.error("Error while encrypting the data.", badpaddingexception);
		}
		return s1;
	}

	// End

	/***********************************************************************************
	 * Purpose in brief : To generate a new Secret Key for given Value using DES
	 * Written by : Vijayarumugam K Last Modified : 25 April 2009 Arguments
	 * passed : keyString
	 ************************************************************************************/

	/***********************************************************************************
	 * Purpose in brief : To generate a new Secret Key for given Value using DES
	 * Written by : SubramaniMohanam Last Modified : 17 Dec 2012 Arguments
	 * passed : keyString
	 ************************************************************************************/

	/**************************************************************************************
	 * Purpose in brief : To generate a new Secret Key for given Value using
	 * TripleDES Written by : Vijayarumugam K Last Modified : 25 April 2009
	 * Arguments passed : keyString
	 **************************************************************************************/
	public static SecretKey generateKey(String keyString, String encType)
			throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		if (keyString.length() < 24) {
			keyString += keyString;
			return generateKey(keyString, encType);
		}

		KeySpec keyspec = null;
		// DESedeKeySpec keyspec = null;
		byte[] key1 = new byte[24];
		byte rawkey[] = SecurityService.stringToByteArray(keyString);

		System.arraycopy(rawkey, 0, key1, 0, 24);
		try {
			// keyspec = new DESedeKeySpec(key1);
			if (encType.equals(AES_ENCRYPTTYPE))
				keyspec = new SecretKeySpec(key1, AES_ALG);
			else
				keyspec = new DESedeKeySpec(key1);
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			Log.error("Exception occured in generateSecretKey : ", e);
		}

		// SunJCE sunjce = new SunJCE();

		Security.addProvider(sunjce);

		Security.insertProviderAt(sunjce, 1);

		/*
		 * SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(DES_ALG,
		 * sunjce);
		 */

		SecretKeyFactory keyfactory = null;

		if (!encType.equals(AES_ENCRYPTTYPE))
			keyfactory = SecretKeyFactory.getInstance(DES_ALG);

		// KeySpec spec = new PBEKeySpec(secretKey.toCharArray(),
		// salt.getBytes(), 65536, 256);
		// SecretKey tmp = factory.generateSecret(spec);
		// SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		/*
		 * Log.trace("Get Me Provider Name for gen Key with Key STring:::" +
		 * keyfactory.getProvider());
		 */

		SecretKey key = null;

		try {
			if (encType.equals(AES_ENCRYPTTYPE))
				key = (SecretKey) keyspec;
			else
				key = keyfactory.generateSecret(keyspec);
		} catch (InvalidKeySpecException e) {
			Log.error("Exception occured in generateDESedeKey : ", e);
		}

		return key;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/***************************************************************************
	 * Purpose in brief : To generate a new Secret Key for given Value using
	 * TripleDES Written by : Sunil P Kumar Last Modified : 25 April 2012
	 * Arguments passed : byte array keyString
	 **************************************************************************/
	public static SecretKey genDESedeKeyFrmByteArray(byte[] byteArrSecretKey)
			throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		DESedeKeySpec keyspec = null;
		try {
			keyspec = new DESedeKeySpec(byteArrSecretKey);
		} catch (InvalidKeyException e) {
			Log.error("Exception occured in genDESedeKeyFrmByteArray : ", e);
		}
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(DES_ALG);
		SecretKey key = null;
		try {
			key = keyfactory.generateSecret(keyspec);
		} catch (InvalidKeySpecException e) {
			Log.error("Exception occured in genDESedeKeyFrmByteArray : ", e);
		}
		return key;
	}

	/***************************************************************************
	 * Get private key from Modulus and Exponent
	 * 
	 * @param modulus
	 * @param exponent
	 * @return
	 ***************************************************************************/
	public static PrivateKey getPrivateKey(BigInteger modulus,
			BigInteger exponent) {
		PrivateKey privateKey = null;
		try {
			KeyFactory keyfac = KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus,
					exponent);
			privateKey = keyfac.generatePrivate(privateKeySpec);
		} catch (NoSuchAlgorithmException e) {
			Log.error("Exception occured in getPrivateKey : ", e);
		} catch (InvalidKeySpecException e) {
			Log.error("Exception occured in getPrivateKey : ", e);
		}
		return privateKey;
	}

	public static byte[] getDekBytes(String dekKey, String encType)
			throws IOException {
		SecretKey dataEncKey = null;
		if (encType.equals(AES_ENCRYPTTYPE))
			dataEncKey = new SecretKeySpec(byteConvertor(dekKey), AES_ALG);
		else
			dataEncKey = new SecretKeySpec(byteConvertor(dekKey), DES_ALG);
		String hexData = alpha2Hex(ba2s(dataEncKey.getEncoded()));
		return new BASE64Decoder().decodeBuffer(hex2Alpha(hexData));
	}

	public  SecretKey getSecretKey(String aliasName,
			String keyStorePassword2, byte[] decodedValue, String msp,
			String encryptType) throws Exception {
		String kek_keyStoreLocation = config.getKeyStoreLocation()
				+ File.separator + msp + File.separator
				+ config.getKeyStoreFileName();
				
		String keyStorePassword = StaticStore.kekCodes
				.get(Constants.KEK_CODE_PREFIX + msp) + keyStorePassword2;
		
		
		KeyStoreManager kek_ksm = KeyStoreManager.getInstance(
				config.getKeyStoreType(), kek_keyStoreLocation,
				keyStorePassword);
		SecretKey keyEncKey = (SecretKey) kek_ksm.getKey(aliasName,
				keyStorePassword);

		byte[] decrypted;
		if (encryptType.equals(AES_ENCRYPTTYPE))
			decrypted = new Decryptor(AES_ALG, AES_PADDING, AES_MODE).decrypt(
					decodedValue, keyEncKey);
		else
			decrypted = new Decryptor(DES_ALG, DES_PADDING, DES_MODE).decrypt(
					decodedValue, keyEncKey);

		String decryptedStr = ba2s(decrypted);

		return generateKey(decryptedStr, encryptType);
	}

	public byte[] encryptText(byte[] keyVal, String aliasName,
			String keyStorePassword2, byte[] dekValue, String msp,
			String encType) throws Exception {

		SecretKey dataEncKey = getSecretKey(aliasName, keyStorePassword2,
				dekValue, msp, encType);
		byte[] encrypted;

		if (encType.equals(AES_ENCRYPTTYPE))
			encrypted = new Encryptor(AES_ALG, AES_PADDING, AES_MODE).encrypt(
					keyVal, dataEncKey);
		else
			encrypted = new Encryptor(DES_ALG, DES_PADDING, DES_MODE).encrypt(
					keyVal, dataEncKey);

		return encrypted;
	}

	public String decryptText(byte[] keyVal, String aliasName,
			String keyStorePassword2, byte[] dekValue, String msp,
			String encType) throws Exception {
		SecretKey dataEncKey = getSecretKey(aliasName, keyStorePassword2,
				dekValue, msp, encType);
		byte[] decryptedValue = new Decryptor(DES_ALG).decrypt(keyVal,
				dataEncKey);
		return SecurityService.byteArrayToString(decryptedValue);
	}

	public static byte[] byteConvertor(String str) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.reset();
		int j = 0;
		int strLen = str.length();
		for (int i = 0; i < strLen; i = i + 2) {
			j = i + 2;
			baos.write((byte) Integer.parseInt(str.substring(i, j), 16));
		}
		return baos.toByteArray();
	}

	public static String ba2s(byte[] ba) {
		StringBuffer sb = new StringBuffer();
		int off = 0;
		int len = ba.length;

		for (int i = off; i < off + len; i++) {
			int b = ba[i];
			char c = digits.charAt((b >> 4) & 0xf);
			sb.append(c);
			c = digits.charAt(b & 0xf);
			sb.append(c);
		}
		return hex2Alpha(sb.toString());
	}

	public static String hex2Alpha(String data) {
		int len = data.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i = i + 2) {
			int j = i + 2;
			sb.append((char) Integer.valueOf(data.substring(i, j), 16)
					.intValue());
		}
		return sb.toString();		 
	}

	public static String alpha2Hex(String data) {
		char[] alpha = data.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < alpha.length; i++) {
			int count = Integer.toHexString(alpha[i]).toUpperCase().length();
			if (count <= 1) {
				sb.append("0").append(
						Integer.toHexString(alpha[i]).toUpperCase());
			} else {
				sb.append(Integer.toHexString(alpha[i]).toUpperCase());
			}
		}
		return sb.toString();
	}

	public static byte[] stringtoByteConvertor(String str) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.reset();
		int j = 0;
		int strLen = str.length();
		for (int i = 0; i < strLen; i = i + 2) {
			j = i + 2;
			baos.write((byte) Integer.parseInt(str.substring(i, j), 16));
		}
		return baos.toByteArray();
	}

	public PrivateKey getSecretKeyForEncy(String msp) throws Exception {
		//SecureData sd = StaticStore.deks.get(msp);
		String kek_keyStoreLocation = config.getKeyStoreLocation()
				+ File.separator + msp + File.separator
				+ config.getKeyStoreFileName();
		String keyStorePassword = StaticStore.kekCodes
				.get(Constants.KEK_CODE_PREFIX + msp);
		PrivateKey privateKey = KeyStoreManager.getInstanceKey(
				config.getKeyStoreType(), kek_keyStoreLocation,
				keyStorePassword, config);
		Log.debug("Private Key", privateKey.toString());

		return privateKey;

	}


}
