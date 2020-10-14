package com.fss.pos.base.commons.utils.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;


import com.sun.crypto.provider.SunJCE;

/**
 * This class is used for data encryption.
 * 
 * @author Dinakar K
 * @version 1.0
 *
 */
public class Encryptor {

	static SunJCE sunjce = new SunJCE();

	/**
	 * Default Constructor.
	 */
	public Encryptor() {
		super();
	}

	/**
	 * Gives a Encryptor instance by changing default algorithm.
	 * 
	 * @param algorithm
	 *            - Algorithm to be used for encryption.
	 */
	public Encryptor(String algorithm) {
		super();
		_algorithm = algorithm;
	}

	/**
	 * Gives a Encryptor instance by changing default algorithm and default
	 * padding type.
	 * 
	 * @param algorithm
	 *            - Algorithm to be used for encryption.
	 * @param padding
	 *            - Padding Type to be used for encryption.
	 */
	public Encryptor(String algorithm, String padding) {
		super();
		_algorithm = algorithm;
		_padding = padding;
	}
	
	public Encryptor(String algorithm, String padding,String mode) {
		super();
		_algorithm = algorithm;
		_padding = padding;
		_mode=mode;
	}

	/**
	 * Encrypts the given plain text using the given secret key and returns the
	 * encrypted cipher text.
	 * 
	 * @param cipherText
	 *            - To be encrypted.
	 * @param key
	 *            - Secret Key to be used for encryption.
	 * @return byte[] - Encrypted Cipher text.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws ShortBufferException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] encrypt(byte[] plainText, SecretKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, ShortBufferException,
			IllegalBlockSizeException, BadPaddingException {
			Cipher cipher = Cipher.getInstance(_algorithm + "/" + _mode + "/"
					+ _padding);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int cipherTextLength = cipher.getOutputSize(plainText.length);
			byte[] cipherText = new byte[cipherTextLength];
			int ctLength = cipher.update(plainText, 0, plainText.length,
					cipherText, 0);
			ctLength += cipher.doFinal(cipherText, ctLength);
			return cipherText;
	}

	/**
	 * The algorithm to be used for decryption.
	 */
	private String _algorithm = "DESede";

	/**
	 * The Cipher Mode to be used for decryption.
	 */
	private String _mode = "ECB";

	/**
	 * The Cipher Padding to be used for decryption.
	 */
	private String _padding = "PKCS7Padding";

}
