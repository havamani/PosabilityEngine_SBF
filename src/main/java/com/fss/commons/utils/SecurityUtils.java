package com.fss.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.security.Decryptor;
import com.fss.pos.base.commons.utils.security.Encryptor;
import com.fss.pos.base.commons.utils.security.KeyStoreManager;
import com.fss.pos.base.commons.utils.security.SecureData;
import com.fss.pos.base.commons.utils.security.jCryption;
import com.sun.crypto.provider.SunJCE;
@Service
public class SecurityUtils {

	private static final String SHA_512_ALGORITHM = "SHA-512";

	static KeyPair keyPair = null;
	public static final int KEY_SIZE = 2048;
	
	@Autowired
	private Config config;
	// Key For DESede Algm
			public static final String keyString1 = "222222222222222222222222222222222222222222222222";
	
	/**
	 * SHA 512 Hashing
	 * 
	 * @param codeToHash      
	 * @param salt
	 * @return 128 byte hashed value in String
	 * @throws UnsupportedEncodingException
	 */
	public static String hashSHA512(String codeToHash, String salt)
			throws UnsupportedEncodingException {
		try {
			MessageDigest md = MessageDigest.getInstance(SHA_512_ALGORITHM);
			md.update(salt.getBytes(StandardCharsets.UTF_8));
			byte[] bytes = md.digest(codeToHash
					.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			Log.error("SecurityUtils hashSHA512", e);
			return null;
		}
	}
	
/*	public static void main(String[] args)throws Exception {
		System.out.println(hashSHA512("ac0927358e8c4a1e558ad6cd5d2ff3485fd4f6344d2f50a4355d01b5c41672314536027ec8d2c7ea3a8fbd05c35766a5fffe7558eecd701f17dbacb365e2d2d7",""));
		 //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
         System.out.println("generated Salt:"+salt);
	}
*/
	private static void generateKeyPair()
	{
		if(keyPair==null)
		{
			jCryption jc = new jCryption();
			keyPair = jc.generateKeypair(KEY_SIZE);
		}
	}
	
	public static KeyPair getKeyPair()
	{
		if(keyPair==null)
		{
			generateKeyPair();
		}
		return keyPair;
	}
	
	static
	{
        Security.addProvider(new BouncyCastleProvider());
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String,String> decryptJCryptionData(String encryptedString)
	{
		KeyPair keyPair = null;
		jCryption jc = null;
		String urlToParse = "";
		String str[] = null;
		Map<String,String> params = null;
		try {
			  params = new HashMap<String,String>();
			jc = new jCryption();
			keyPair = SecurityUtils.getKeyPair();
			str = encryptedString.split(" ");
			
			for(int i = 0; i < str.length; i++)
			{
				urlToParse = urlToParse + url(decrypt(str[i], keyPair), "UTF-8");
			}
				 
				params = parse(urlToParse);
				Log.trace("User Login Details loaded successfully");
				
				
		} catch (Exception e) {
			Log.error("UserAuthentication Spring Security decryption of jcryption data in java:", e);
		}finally{
			keyPair = null;
			jc = null;
			urlToParse = "";
			str = null;
		}
		return params;
	}
	
	
	private static String digits = "0123456789ABCDEF";
	private static final String HEX_DIGITS = "0123456789abcdef";

	static  SunJCE sunjce = new SunJCE();


	/**
	 * Convert a byte array of 8 bit characters into a String.
	 * 
	 * @param bytes
	 *            the array containing the characters
	 * @param length
	 *            the number of bytes to process
	 * @return a String representation of bytes
	 */
	public static String byteArrayToString(byte[] bytes, int length) {
		char[] chars = new char[length];

		for (int i = 0; i != chars.length; i++) {
			chars[i] = (char) (bytes[i] & 0xff);
		}

		return new String(chars);
	}

	/**
	To load bouncy castle provider on windows pc 
	static
	{
	Security.addProvider(new BouncyCastleProvider());
	}
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
	public static String byteArrayToString(byte[] bytes) {
		return byteArrayToString(bytes, bytes.length);
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
		return byteArrayToHexString(data, data.length);
	}


	/**
	 * Converts a string of hex digits into a byte array of those digits.
	 * 
	 * @param hex - Hex String to be converted.
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
	 * @param hex - Hex String to be converted.
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
	 * @param hex - Hex String to be converted.
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

	/**
	 * To generate key for Triple DES Encryption and Decryption.
	 * @return Secret key which can be used for Triple DES.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException 
	 */
	public static SecretKey generateDESedeKey() throws InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		//String keyString = "222222222222222222222222222222222222222222222222";
		
		String keyString = keyString1;
		
		DESedeKeySpec keyspec = null;
		byte[] key1 = new byte[24];
		byte rawkey[] = SecurityUtils.stringToByteArray(keyString);
		System.arraycopy(rawkey, 0, key1, 0, 24);
		try {
			keyspec = new DESedeKeySpec(key1);
		} catch (InvalidKeyException e) {
			Log.error("Exception occured in generateDESedeKey : " ,e);
		}
		

	//	SunJCE sunjce = new SunJCE();

		Security.addProvider(sunjce);
		
		Security.insertProviderAt(sunjce, 1);
		
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede", sunjce);

	//	Log.trace("Get Me Provider Name for gen Key:::"+keyfactory.getProvider());
		
		SecretKey key = null;
		try {
			key = keyfactory.generateSecret(keyspec);
		} catch (InvalidKeySpecException e) {
			Log.error("Exception occured in generateDESedeKey : " ,e);
		}

		return key;
	}
	
	
  
    
    public static byte[] strToBytes(String s)
    {
        if(s.length() % 2 != 0)
            throw new IllegalArgumentException("String length % 2 != 0");
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        for(int i = 0; i < s.length(); i += 2)
        {
            byte byte0 = (byte)(Character.digit(s.charAt(i), 16) & 0xff);
            byte0 <<= 4;
            byte0 |= Character.digit(s.charAt(i + 1), 16);
            bytearrayoutputstream.write(byte0);
        }

        return bytearrayoutputstream.toByteArray();
    }
	
	public static SecretKey generateDESedeKey(String keyString) throws InvalidKeyException,
	NoSuchAlgorithmException, InvalidKeySpecException {
		if(keyString.length() < 24){
			keyString += keyString;
			return generateDESedeKey(keyString);
		}
		DESedeKeySpec keyspec = null;
		byte[] key1 = new byte[24];
		byte rawkey[] = SecurityUtils.stringToByteArray(keyString);

		System.arraycopy(rawkey, 0, key1, 0, 24);
		try {
			keyspec = new DESedeKeySpec(key1);
		} catch (InvalidKeyException e) {
			Log.error("Exception occured in generateDESedeKey : " ,e);
		}

		//SunJCE sunjce = new SunJCE();

	    Security.addProvider(sunjce);
		
		Security.insertProviderAt(sunjce, 1);
		
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede", sunjce);
		//Log.trace("Get Me Provider Name for gen Key with Key STring:::"+keyfactory.getProvider());
		
	  	SecretKey key = null;
		
		try {
			key = keyfactory.generateSecret(keyspec);
		} catch (InvalidKeySpecException e) {
			Log.error("Exception occured in generateDESedeKey : " ,e);
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
	
	
	
	private static SecurityUtils instance;
	
	public static synchronized SecurityUtils getInstance()
	{
	    if(instance == null)
	        return new SecurityUtils();
	    else
	        return instance;
	}
	
	public synchronized String hashPassword(String s) throws Exception 
	{
	    MessageDigest messagedigest = null;
	    try
	    {
	        messagedigest = MessageDigest.getInstance("SHA-256");
	    }
	    catch(NoSuchAlgorithmException nosuchalgorithmexception)
	    {
	        throw new Exception(nosuchalgorithmexception.getMessage());
	    }
	    try
	    {
	        messagedigest.update(s.getBytes("UTF-8"));
	    }
	    catch(UnsupportedEncodingException unsupportedencodingexception)
	    {
	        throw new Exception(unsupportedencodingexception.getMessage());
	    }
	    byte abyte0[] = messagedigest.digest();
	    String s1 = (new BASE64Encoder()).encode(abyte0);
	    return s1;
	}

	public static SecretKey genDESedeKeyFrmByteArray(byte[] byteArrSecretKey)throws InvalidKeyException, NoSuchAlgorithmException,InvalidKeySpecException 
	{
		DESedeKeySpec keyspec = null;
		try {
			keyspec = new DESedeKeySpec(byteArrSecretKey);
		} catch (InvalidKeyException e) {
			Log.error("Exception occured in genDESedeKeyFrmByteArray : " ,e);
		}
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		SecretKey key = null;
		try {
			key = keyfactory.generateSecret(keyspec);
		} catch (InvalidKeySpecException e) {
			Log.error("Exception occured in genDESedeKeyFrmByteArray : " ,e);
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
			Log.error("Exception occured in getPrivateKey : " ,e);
		} catch (InvalidKeySpecException e) {
			Log.error("Exception occured in getPrivateKey : " ,e);
		}

		return privateKey;

	}
	
	
	@SuppressWarnings("unused")
	public static String encryptDEKWithnewKEK(String dekAliasname, SecretKey oldkeyEncKey, SecretKey newkeyEncKey, KeyStoreManager dekKsm, String keyStorePassword, String dek_Key_Value) {
		String keyString = null;
		String hexData = null;
		String encoded = null;
		String secKey = null;
		byte[] sk1_byte = null;
		byte[] encryptedKey = null;
		SecretKey oldDataEncKey = null;
		SecretKey newDataEncKey = null;
		Encryptor enc = null;
		Decryptor dec = null;
		BASE64Encoder basEncoder = null;
		BASE64Decoder base64Decoder = null;
		byte[] decodedValue = null;
		byte[] decrypted = null;
		String decryptedStr = null;
		try {
				
				hexData=dek_Key_Value;

				base64Decoder = new BASE64Decoder();
				decodedValue = base64Decoder.decodeBuffer(hex2Alpha(hexData));
				dec = new Decryptor("DESede");
				//decrypted = dec.decrypt(decodedValue,oldkeyEncKey);
				
				decryptedStr = ba2s(decrypted);
				sk1_byte = decryptedStr.getBytes();
				enc = new Encryptor("DESede");
				//encryptedKey = enc.encrypt(sk1_byte, newkeyEncKey);
				basEncoder = new BASE64Encoder();
				encoded = basEncoder.encode(encryptedKey);
				
				hexData = alpha2Hex(encoded);
				//newDataEncKey = new SecretKeySpec(byteConvertor(hexData), "DESede");
				//dekKsm.replaceSecretKey(dekAliasname, newDataEncKey, keyStorePassword);
			//}
			return hexData;
		} catch (Exception e) {
			Log.error("Error while encrypting the data.", e);
			return hexData;
		}finally{
			dekAliasname = null;
			keyString = null;
			hexData = null;
			encoded = null;
			secKey = null;
			sk1_byte = null;
			encryptedKey = null;
			oldDataEncKey = null;
			newDataEncKey = null;
			enc = null;
			dec = null;
			basEncoder = null;
			base64Decoder = null;
			decodedValue = null;
			decrypted = null;
			decryptedStr = null;
			
		}
	}
	
	public  byte[] encryptText(byte[] keyVal, String aliasName,String instId, String keyStorePassword2, String dek_Key_Value) 
	{
		byte[] encryptedValue = null;
		//String dekAliasname = null;
		String kekAliasname = null;
		SecretKey dataEncKey = null;
		SecretKey keyEncKey = null;
		String secKey = null;
		String hexData = null;
		String keyStorePassword = null;
		BASE64Decoder base64Decoder = null;
		Encryptor enc = null;
		Decryptor dec = null;
		byte[] decodedValue = null;
		byte[] decrypted = null;
		
		KeyStoreManager kek_ksm = null;
		//KeyStoreManager dek_ksm = null;
		
		String kek_keyStoreLocation = null;
		//String dek_keyStoreLocation = null;
		
		try {
			kek_keyStoreLocation = config.getKeyStoreLocation()+File.separator+instId+ File.separator+config.getKeyStoreFileName();
			
			if(!(kek_keyStoreLocation!=null && kek_keyStoreLocation.length()>0))
			{
				Log.trace("Key store location is null");
				return null;
			}
			  keyStorePassword = StaticStore.kekCodes
					.get(Constants.KEK_CODE_PREFIX + instId) + keyStorePassword2;
			
			if(!(keyStorePassword!=null && keyStorePassword.length()>0))
			{
				Log.trace("Key store password is null");
				return null;
			}
			
			if(kek_keyStoreLocation != null && keyStorePassword != null) {
				kek_ksm = KeyStoreManager.getInstance("JCEKS", kek_keyStoreLocation, keyStorePassword);
				
			} else {
				Log.trace("KeyStoreManager is null");
				return null;
			}
			
			//DEK KeyStoreLocation Path
			/*dek_keyStoreLocation = ConfigFileUtil.getStringValue(Constants.POS_key_store_location)+File.separator+instId+ File.separator+ConfigFileUtil.getStringValue(Constants.POS_dek_keystore_file_name);
			
			if(!(dek_keyStoreLocation!=null && dek_keyStoreLocation.length()>0))
			{
				Log.trace("DEK Key store location is null");
				return null;
			}
			
			if(dek_keyStoreLocation != null && keyStorePassword != null) {
				dek_ksm = KeyStoreManager.getInstance("JCEKS", dek_keyStoreLocation, keyStorePassword);
				
			} else {
				Log.trace("KeyStoreManager is null");
				return null;
			}*/
			
			/*dekAliasname = aliasName;
			dataEncKey = (SecretKey)dek_ksm.getKey(dekAliasname, keyStorePassword);
			if(dataEncKey==null)
			{
				Log.trace("Data Enc key is null");
				throw new Exception("Data Enc key is null");
			}*/
			
			dataEncKey=  new SecretKeySpec(byteConvertor(dek_Key_Value), "DESede");
			
			kekAliasname = aliasName;
			keyEncKey = (SecretKey)kek_ksm.getKey(kekAliasname, keyStorePassword);
			if(keyEncKey==null)
			{
				Log.trace("Key Enc key is null");
				throw new Exception("Key Enc key is null");
			}
			
			secKey = ba2s(dataEncKey.getEncoded());
			hexData = alpha2Hex(secKey);

			base64Decoder = new BASE64Decoder();
			decodedValue = base64Decoder.decodeBuffer(hex2Alpha(hexData));
			 
			dec = new Decryptor("DESede");
			//decrypted = dec.decrypt(decodedValue,keyEncKey);
			
			String decryptedStr = ba2s(decrypted);
			System.out.println("encryption key :::::::::"+decryptedStr);
			dataEncKey = generateDESedeKey(decryptedStr);
			enc = new Encryptor("DESede");
			//encryptedValue = enc.encrypt(keyVal, dataEncKey);
			
			return encryptedValue;
		} catch (Exception e) {
			Log.error("Error while encrypting the data.", e);
			return null;
		}finally{
			//dekAliasname = null;
			kekAliasname = null;
			dataEncKey = null;
			keyEncKey = null;
			secKey = null;
			hexData = null;
			keyStorePassword = null;
			base64Decoder = null;
			enc = null;
			dec = null;
			decodedValue = null;
			decrypted = null;
			kek_keyStoreLocation = null;
			//dek_keyStoreLocation = null;
			kek_ksm = null;
			//dek_ksm = null;
		}
	}
	
	
	public   String decryptText(byte[] keyVal, String aliasName, String instId, String keyStroePassword2, String dek_Key_Value) 
	{
		byte[] decryptedValue = null;
		//String dekAliasname = null;
		String kekAliasname = null;
		SecretKey dataEncKey = null;
		SecretKey keyEncKey = null;
		String secKey = null;
		String hexData = null;

		String keyStorePassword = null;
		BASE64Decoder base64Decoder = null;
		Decryptor dec = null;
		byte[] decodedValue = null;
		byte[] decrypted = null;

		KeyStoreManager kek_ksm = null;
		//KeyStoreManager dek_ksm = null;
		
		String kek_keyStoreLocation = null;
		//String dek_keyStoreLocation = null;
		try
		{
			kek_keyStoreLocation = config.getKeyStoreLocation()+File.separator+instId+ File.separator+config.getKeyStoreFileName();
			Log.trace("Jsk file Path Security Util:"+kek_keyStoreLocation);
			//kek_keyStoreLocation = "D:\\POSABILITY\\Portal\\config\\Security"+File.separator+instId+ File.separator+"KEK_Key.jks";
 			
			if(!(kek_keyStoreLocation!=null && kek_keyStoreLocation.length()>0))
			{
				Log.trace("Key store location is null");
				return null;
			}
			
			  keyStorePassword = StaticStore.kekCodes
						.get(Constants.KEK_CODE_PREFIX + instId) + keyStroePassword2;
			  Log.trace("Second Password in Security Util:"+keyStorePassword);
			  //keyStorePassword = "Fss@1234"+ keyStroePassword2;
			
			if(!(keyStorePassword!=null && keyStorePassword.length()>0))
			{
				Log.trace("Key store password is null");
				return null;
			}
			
			if(kek_keyStoreLocation != null && keyStorePassword != null) {
				kek_ksm = KeyStoreManager.getInstance("JCEKS", kek_keyStoreLocation, keyStorePassword);
				
			} else {
				Log.trace("KeyStoreManager is null");
				return null;
			}
			
			//DEK KeyStoreLocation Path
			/*dek_keyStoreLocation = ConfigFileUtil.getStringValue(Constants.POS_key_store_location)+File.separator+instId+ File.separator+ConfigFileUtil.getStringValue(Constants.POS_dek_keystore_file_name);
			
			if(!(dek_keyStoreLocation!=null && dek_keyStoreLocation.length()>0))
			{
				Log.trace("DEK Key store location is null");
				return null;
			}
			
			if(dek_keyStoreLocation != null && keyStorePassword != null) {
				dek_ksm = KeyStoreManager.getInstance("JCEKS", dek_keyStoreLocation, keyStorePassword);
				
			} else {
				Log.trace("KeyStoreManager is null");
				return null;
			}*/
			//End
			/*dekAliasname = aliasName;
			dataEncKey = (SecretKey)dek_ksm.getKey(dekAliasname, keyStorePassword);*/
			dataEncKey=  new SecretKeySpec(byteConvertor(dek_Key_Value), "DESede");
			
			kekAliasname = aliasName;
			keyEncKey = (SecretKey)kek_ksm.getKey(kekAliasname, keyStorePassword);
			if(keyEncKey==null)
			{
				Log.trace("Key Enc key is null");
				throw new Exception("Key Enc key is null");
			}
			
			secKey = ba2s(dataEncKey.getEncoded());
			hexData = alpha2Hex(secKey);
			

			base64Decoder = new BASE64Decoder();
			decodedValue = base64Decoder.decodeBuffer(hex2Alpha(hexData));
			dec = new Decryptor("DESede");
			//decrypted = dec.decrypt(decodedValue,keyEncKey);
			
			String decryptedStr = ba2s(decrypted);
			dataEncKey = generateDESedeKey(decryptedStr);
			System.out.println("decrypt time :::::::::"+decryptedStr);
			//Decrypting the value with DEK
			//decryptedValue = dec.decrypt(keyVal, dataEncKey);
			
			return SecurityUtils.byteArrayToString(decryptedValue);
		} 
		catch (Exception e) 
		{
			Log.error("Error while decrypt the data.", e);
			return null;
		}
		finally
		{
			decryptedValue = null;
			//dekAliasname = null;
			kekAliasname = null;
			dataEncKey = null;
			keyEncKey = null;
			secKey = null;
			hexData = null;

			keyStorePassword = null;
			base64Decoder = null;
			dec = null;
			
			kek_keyStoreLocation = null;
			//dek_keyStoreLocation = null;
			kek_ksm = null;
			//dek_ksm = null;
		}
	}
	
	public static SecretKey getKey(String aliasName,String kek_keyStoreLocationPath,String instId, String keyStorePassword) {

		KeyStoreManager ksm = null;
		SecretKey key = null;
		
		try {
			
						
			
			if(!(kek_keyStoreLocationPath!=null && kek_keyStoreLocationPath.length()>0))
			{
				Log.trace("Key Stroe Location is null");
			}
			
			
			if(!(keyStorePassword!=null && keyStorePassword.length()>0))
			{
				Log.trace("Key store password is null");
			}
			
			if(kek_keyStoreLocationPath != null && keyStorePassword != null) {
				ksm = KeyStoreManager.getInstance("JCEKS", kek_keyStoreLocationPath, keyStorePassword);
								
				
			} else {
				Log.trace("KeyStoreManager is null");
			}
			
			key = (SecretKey)ksm.getKey(aliasName,keyStorePassword);
			
			return key;
			
		} catch (Exception e) {
			Log.error("Error in getKey:",e);
			 return null;
			 
		} finally {
			
			kek_keyStoreLocationPath = null;
			ksm = null;
		}
		
	}
	//End
	
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
			int b = (int) ba[i];
			char c = digits.charAt((b >> 4) & 0xf);
			sb.append(c);
			c = digits.charAt(b & 0xf);
			sb.append((char) c);
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
		data = sb.toString();
		return data;
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
	
	public static String hashingSHA256(String hashData)
	{
		MessageDigest messagedigest = null;
		StringBuffer hash = new StringBuffer();
		byte digest[] = null;
		String str1 = null;
	    try
	    {
		   messagedigest = MessageDigest.getInstance("SHA256");
		   messagedigest.update(stringToByteArray(hashData));
		   digest = messagedigest.digest();
		   hash = new StringBuffer(digest.length*2);
		   int length = digest.length;
		   
		   for (int n=0; n < length; n++)
		   {
		      int number = digest[n];
		      if(number < 0)
		      {
		    	  number= number + 256;
		      }
		      if(Integer.toString(number,16).length() == 1)
		      {
		    	  str1="0"+String.valueOf(Integer.toString(number,16));
		      }
		      else
		      {
		    	  str1=String.valueOf(Integer.toString(number,16));
		      }
		      hash.append(str1);
		      
		      str1 = null;
		   }
	    }
		catch (Exception e) 
		{
			Log.error("Exception occured in hashingSHA256 : " ,e);
			return null;
		}
		finally
		{
			messagedigest = null;
			digest = null;
		}
		return hash.toString().toUpperCase();
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

	  /**
     * Decrypts a given string with the RSA keys
     * @param encrypted full encrypted text
     * @param keys RSA keys
     * @return decrypted text
     * @throws RuntimeException if the RSA algorithm not supported or decrypt operation failed
     */
    public static String decrypt( String encrypted, KeyPair keys ) {
        Cipher dec;
        try {
            dec = Cipher.getInstance("RSA/NONE/NoPadding");
            dec.init(Cipher.DECRYPT_MODE, keys.getPrivate());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("RSA algorithm not supported",e);
        }
        String[] blocks = encrypted.split("\\s");
        StringBuffer result = new StringBuffer();
        try {
            for ( int i = 0; i<blocks.length; i++ ) {
                byte[] data = hexStringToByteArray(blocks[i]);
                byte[] decryptedBlock = dec.doFinal(data);
                result.append( new String(decryptedBlock) );
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Decrypt error",e);
        }
        return result.reverse().toString();
    }
    /**
     * Parse url string (Todo - better parsing algorithm)
     * @param url value to parse
     * @param encoding encoding value
     * @return Map with param name, value pairs
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map parse(String urlToParse) 
    {
        try 
        {
            String[] params = urlToParse.split("&");
            Map parsed = new HashMap();
            for (int i = 0; i<params.length; i++ )  
            {
                String[] p = params[i].split("=");
                if(p.length==2)
                {
                	String name = p[0];
                    String value = (p.length==2)?p[1]:null;
                    parsed.put(name, value);
                }
            }
            return parsed;
        }
        catch (Exception e) 
        {
            throw new RuntimeException("Unknown encoding.",e);
        }
    }
    public static String url(String url,String encoding) 
    {
        try 
        {
        	url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            return URLDecoder.decode(url,encoding);
        }
        catch (UnsupportedEncodingException e) 
        {
            throw new RuntimeException("Unknown encoding.",e);
        }
    }
    public  String encryptBase64Encode(String plainData,String mspAcr){
    	SecureData sd = StaticStore.deks.get(mspAcr);
		try {
			Log.trace("Encrypt Params: PlainData:"+plainData+" AliasNamePrefix:"+config.getAliasNamePrefix()+"    MSPCR:"+mspAcr+"   kekCode:"+sd.getKekCode()+"   DekBytes:"+sd.getDekBytes()+"  Dek Value:"+sd.getDek());
			byte[] encCaptureId = encryptText(plainData.getBytes(StandardCharsets.ISO_8859_1),
					config.getAliasNamePrefix() + mspAcr,mspAcr, sd.getKekCode(),
					sd.getDek());
 			Log.trace("after Encrypted data:"+encCaptureId.toString());
 			return new String(Base64.encodeBase64(encCaptureId),StandardCharsets.ISO_8859_1);
		} catch (Exception e) {
			Log.error("Encryption Failed", e);
			return null;
		}
    }
    
    public String decryptBase64Decode(String encryptData,String mspAcr){
    	
    	
    	SecureData sd = StaticStore.deks.get(mspAcr);
    	try{
	    Log.trace("Decrypt Params: Data:"+encryptData+" AliasNamePrefix:"+config.getAliasNamePrefix()+"MSPCR:"+mspAcr+"kekCode:"+sd.getKekCode()+"DekBytes:"+sd.getDekBytes());
		//String plainCaptureId = securityUtils.decryptText(new String(Base64.decodeBase64(hd.getAccountNo().getBytes())).getBytes(), config.getAliasNamePrefix() + mspAcr, sd.getKekCode(),sd.getDekBytes(), mspAcr);
		String plainCaptureId = decryptText(new String(Base64.decodeBase64(encryptData.getBytes(StandardCharsets.ISO_8859_1)),StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1), config.getAliasNamePrefix()+mspAcr,mspAcr ,sd.getKekCode(),sd.getDek());
    	//	String plainCaptureId = decryptText(new String(Base64.decodeBase64(encryptData.getBytes())).getBytes(), "card_alsnm_"+ mspAcr,mspAcr ,"Fss@1234","4B314F7849496C6266364743695668524B7534562F513D3D");
		Log.trace("Decrypted Customer info:"+plainCaptureId);
			return plainCaptureId;
    	}catch(Exception e){
    		Log.error("Decryption Failed", e);
			return null;
    	}
    	
    }
}
