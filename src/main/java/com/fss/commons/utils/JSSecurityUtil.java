package com.fss.commons.utils;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.fss.pos.base.commons.Log;

/**
 * @author PurnaChandraReddy Vaka
 *
 */
public final class JSSecurityUtil {

	//public static final Logger LOGGER = Logger.getLogger(JSSecurityUtil.class);
	
	public static final String	RSA_ECB_PKCS1_PADDING	= "RSA/ECB/PKCS1Padding";
	public static final String	PRIVATE_KEY_HEADER			= "-----BEGIN PRIVATE KEY-----";
	public static final String	PRIVATE_KEY_FOOTER			= "-----END PRIVATE KEY-----";
	public static final String	PUBLIC_KEY_HEADER			= "-----BEGIN RSA PUBLIC KEY-----";
	public static final String	PUBLIC_KEY_FOOTER			= "-----END RSA PUBLIC KEY-----";
	private static final String	ALGORITHM	= "RSA";
	

	public static byte[] encryptAsByteArray(String data, PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(data.getBytes());
		} catch (Exception e) {
			throw new IllegalArgumentException("Encrypt failed!", e);
		}
	}

	public static String encryptAsString(String data, PublicKey publicKey) {
		return Base64.encodeBase64String(encryptAsByteArray(data, publicKey));
	}

	public static String encryptAsString(String data, String base64PublicKey) {
		return Base64.encodeBase64String(encryptAsByteArray(data, getPublicKey(base64PublicKey)));
	}

	
	public static String decrypt(String data, PrivateKey privateKey) {
		
		String val = new String(Base64.decodeBase64(data.getBytes(StandardCharsets.ISO_8859_1)),StandardCharsets.ISO_8859_1);
		try {
			Provider cp = Security.getProvider("SunJCE");
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", cp);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
	   	    byte[] plainText = cipher.doFinal(val.getBytes(StandardCharsets.ISO_8859_1));
	   	   Log.debug("plain : " , new String(plainText));
			return new String(plainText);
		} catch (Exception e) {
			throw new IllegalArgumentException("Decrypt failed!", e);
		}
	
	/*	return decrypt(Base64.decodeBase64(data.getBytes(StandardCharsets.ISO_8859_1)), privateKey);*/
		}
	
	public static String decrypt(byte[] data, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			String value = new String(cipher.doFinal(data));
			return value;
		} catch (Exception e) {
			throw new IllegalArgumentException("Decrypt failed!", e);
		}
	}
	public static PrivateKey getPrivateKey(String base64PrivateKey) {
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(base64PrivateKey.getBytes(StandardCharsets.ISO_8859_1)));
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get private key!", e);
		}
	}
	
	
	public static PublicKey getPublicKey(String base64PublicKey) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(base64PublicKey));
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key!", e);
		}
	}
	
	/*
	 * filePath : file Path (public or private key file path)
	 * boolena : true - public , false - private 
	 */
	public static String getPemString(String filePath,boolean indicationOfKey) {
		File file = new File(filePath);
		String privKeyPEM = "";
		try (FileInputStream fileInputStream = new FileInputStream(file);
				DataInputStream dataInputInstream = new DataInputStream(fileInputStream);) {
			byte[] keyBytes = new byte[(int) file.length()];
			dataInputInstream.readFully(keyBytes);
			privKeyPEM = new String(keyBytes);
			if(indicationOfKey){
				privKeyPEM = privKeyPEM.replace(PUBLIC_KEY_HEADER, "");
				privKeyPEM = privKeyPEM.replace(PUBLIC_KEY_FOOTER, "");
			}else{
				privKeyPEM = privKeyPEM.replace(PRIVATE_KEY_HEADER, "");
				privKeyPEM = privKeyPEM.replace(PRIVATE_KEY_FOOTER, "");
			}
			
		} catch (IOException e) {
			Log.error("Exception occured in getPemString ", e);
			return null;
		}
		return privKeyPEM;
	}
	
	/*public static void main(String[] args) {
		PublicKey publicKey = getPublicKey(getPemString("D:\\POSABILITY\\Portal\\config\\keys\\public.pem",true));
	    String encryptedData = "HELLOHIIIIIIIIIIIIIIII";
		String decryptData = "QtTYA2UnLybnzNEIAsG+oJ0VYiaTyHMExhVaKiV52sbXEs/z6nP+9FV1E2detAQ9azihgl7HoLJVmBT6yMk2t7im/Nal0tOPDD0m5FQEYyV7JhMx24OOuF7xfgK9a6eBdWzIl3qXwoJl+fqmsbdBeOfo9pVMyMtiDEadvB6aWojvpUatCewSW1qK4tHSFoYqZL3YhWb0QosqTp31e+28Buzcggpr5ZTCtLGuR8/1sY5SLeeSml2IPS+Alk6yx9/XDZum16j3f2Q4swqFin/IhYxNWMoDMuxW4Ls6sWhNApzVJHmh6WWXHFoomGzbnbV3cRtAeIj652s6tbMKzsnh0+P/ErUJahpNIcNhXaSnoUM1OG91rerDlyOC3+MHJDzLo4UPBW/37dCaQ9YKfmT2srSE0cgEZiCtlPfpCvWpot+Xu/pAPU9LxDiifeQF8qk1iHYEWO48AkZJLZUE5cGBUomHbqaCWqMKR1vGYKybiIdWGaL9xepNWMn0wpVv6AKx";
		System.err.println("decrypt data :::::::::::"+decryptData);
		//QtTYA2UnLybnzNEIAsG+oJ0VYiaTyHMExhVaKiV52sbXEs/z6nP+9FV1E2detAQ9azihgl7HoLJVmBT6yMk2t7im/Nal0tOPDD0m5FQEYyV7JhMx24OOuF7xfgK9a6eBdWzIl3qXwoJl+fqmsbdBeOfo9pVMyMtiDEadvB6aWojvpUatCewSW1qK4tHSFoYqZL3YhWb0QosqTp31e+28Buzcggpr5ZTCtLGuR8/1sY5SLeeSml2IPS+Alk6yx9/XDZum16j3f2Q4swqFin/IhYxNWMoDMuxW4Ls6sWhNApzVJHmh6WWXHFoomGzbnbV3cRtAeIj652s6tbMKzsnh0+P/ErUJahpNIcNhXaSnoUM1OG91rerDlyOC3+MHJDzLo4UPBW/37dCaQ9YKfmT2srSE0cgEZiCtlPfpCvWpot+Xu/pAPU9LxDiifeQF8qk1iHYEWO48AkZJLZUE5cGBUomHbqaCWqMKR1vGYKybiIdWGaL9xepNWMn0wpVv6AKx
		PrivateKey privateKey =getPrivateKey(getPemString("D:\\POSABILITY\\Portal\\config\\keys\\private.pem",false));
		String encrypt = decrypt(decryptData, privateKey);
		
	}*/
	
}
