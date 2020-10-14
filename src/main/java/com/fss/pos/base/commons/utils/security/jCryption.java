package com.fss.pos.base.commons.utils.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

public class jCryption {

	/**
	 * Constructor
	 */
	public jCryption() {
		// java.security.Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Generates the Keypair with the given keyLength.
	 *
	 * @param keyLength
	 *            length of key
	 * @return KeyPair object
	 * @throws RuntimeException
	 *             if the RSA algorithm not supported
	 */
	public KeyPair generateKeypair(int keyLength) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(keyLength);
			return kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("RSA algorithm not supported", e);
		}
	}

	/**
	 * Return public RSA key modulus
	 * 
	 * @param keyPair
	 *            RSA keys
	 * @return modulus value as hex string
	 */
	public static String getPublicKeyModulus(KeyPair keyPair) {
		
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		return publicKey.getModulus().toString(16);
	}

	/**
	 * Return public RSA key exponent
	 * 
	 * @param keyPair
	 *            RSA keys
	 * @return public exponent value as hex string
	 */
	public static String getPublicKeyExponent(KeyPair keyPair) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		return publicKey.getPublicExponent().toString(16);
	}

	/**
	 * Max block size with given key length
	 * 
	 * @param keyLength
	 *            length of key
	 * @return numeber of digits
	 */
	public static int getMaxDigits(int keyLength) {
		return ((keyLength * 2) / 16) + 3;
	}

	/**
	 * Convert byte array to hex string
	 * 
	 * @param bytes
	 *            input byte array
	 * @return Hex string representation
	 */
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			result.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return result.toString();
	}

}
