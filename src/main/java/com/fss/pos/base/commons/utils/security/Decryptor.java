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
 * Decryptor class can be used to decrypt data's.
 * 
 * @author Dinakar k
 * @version 1.0
 * 
 */
public class Decryptor {

	static SunJCE sunjce = new SunJCE();

	/**
	 * Default Constructor.
	 */
	public Decryptor() {
		super();
	}

	/**
	 * Gives a Decryptor instance by changing default algorithm.
	 * 
	 * @param algorithm
	 *            - Algorithm to be used for decryption.
	 */
	public Decryptor(String algorithm) {
		super();
		this._algorithm = algorithm;
	}

	/**
	 * Gives a Decryptor instance by changing default algorithm and default
	 * padding type.
	 * 
	 * @param algorithm
	 *            - Algorithm to be used for decryption.
	 * @param padding
	 *            - Padding Type to be used for decryption.
	 */
	public Decryptor(String algorithm, String padding) {
		super();
		_algorithm = algorithm;
		_padding = padding;
	}
	
	
	public Decryptor(String algorithm, String padding,String mode) {
		super();
		_algorithm = algorithm;
		_padding = padding;
		_mode=mode;
	}

	/**
	 * Decrypts the given cyperText using the given secret key and returns the
	 * plaintext.
	 * 
	 * @param cipherText
	 *            - To be decrypted.
	 * @param key
	 *            - Secret Key to be used for decryption.
	 * @return byte[] - Decrypted plain text.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws ShortBufferException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] decrypt(byte[] cipherText, SecretKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, ShortBufferException,
			IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance(_algorithm + "/" + _mode + "/"
				+ _padding);
		cipher.init(Cipher.DECRYPT_MODE, key);
		int plainTextLength = cipher.getOutputSize(cipherText.length);
		byte[] tmpPlainText = new byte[plainTextLength];
		int ptLength = cipher.update(cipherText, 0, cipherText.length,
				tmpPlainText, 0);
		ptLength += cipher.doFinal(tmpPlainText, ptLength);
		byte[] plainText = new byte[ptLength];
		System.arraycopy(tmpPlainText, 0, plainText, 0, ptLength);
		return plainText;

	}

	/**
	 * The algorithm to be used for decryption.
	 */
	private String _algorithm = "DESede";

	/**
	 * The Cipher Mode to be used for decryption.
	 */
	private String _mode = "ECB";
	// private String _mode = "CBC";

	/**
	 * The Cipher Padding to be used for decryption.
	 */
	private String _padding = "PKCS7Padding";
	// private String _padding = "PKCS5Padding";

}
