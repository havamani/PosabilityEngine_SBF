package com.fss.pos.base.commons.utils.security;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.springframework.stereotype.Component;

import com.fss.pos.base.commons.Config;

/**
 * Key store Wrapper class which can be used to do operation on a keystore file
 * like loading key store file, retrieving certificate, retrieving key, reading
 * certificate chain and etc.
 * 
 * @author Dinakar k
 * @version 1.0
 *
 */
@Component
public class KeyStoreManager {
	
	

	/**
	 * Gives the Key for the given alias and the password.
	 * 
	 * @param alias
	 *            Alias name of the Key.
	 * @param password
	 *            Password used to retrieve the key.
	 * 
	 * @return Key, if the key store contains Key having given alias and
	 *         password.
	 * @return null, if the key store have any Key with the given alias and
	 *         password.
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 */
	public Key getKey(String alias, String password) throws KeyStoreException,
			NoSuchAlgorithmException, UnrecoverableKeyException {
		Key key = null;
		if (keyStore.containsAlias(alias)) {
			if (keyStore.isKeyEntry(alias)) {
				key = keyStore.getKey(alias, getCharArray(password));
			}
		}
		
		return key;
	}

	private void createKeyStore(String keyStoreType, String keyStorePath,
			String password) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		keyStore = KeyStore.getInstance(keyStoreType);
		File file = new File(keyStorePath);
		char[] pass = getCharArray(password);
		if (!file.exists()) {
			file.createNewFile();
			keyStore.load(null, pass);
		} else {
			InputStream is = new FileInputStream(file);
			try
			{
				keyStore.load(is, pass);
				is.close();
			}
			catch(EOFException e)
			{
				keyStore.load(null,pass);
				is.close();
			}
				

		}
	}
	
	private PrivateKey createKeyStoreForEncy(String keyStoreType, String keyStorePath,
			String password,Config config) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		keyStore = KeyStore.getInstance(keyStoreType);
		File file = new File(keyStorePath);
		char[] pass = getCharArray(password);
		  PublicKey publicKey = null;
			PrivateKey key = null;
		if (!file.exists()) {
			file.createNewFile();
			keyStore.load(null, pass);
		} else {
			InputStream is = new FileInputStream(file);
			try {
				keyStore.load(is, pass);
				 key = (PrivateKey)keyStore.getKey(config.getAliasNamePrefix(), password.toCharArray());
			//	key = (PrivateKey)keyStore.getKey("sample", password.toCharArray());
				if (key instanceof PrivateKey) {
				      // Get certificate of public key
				      Certificate cert = keyStore.getCertificate(config.getAliasNamePrefix());
				//	Certificate cert = keyStore.getCertificate("sample");
				      // Get public key
				      publicKey   = cert.getPublicKey();
				    }

				is.close();
			} catch (EOFException ex) {
				keyStore.load(null, pass);
			}
		}
		return new KeyPair(publicKey, (PrivateKey) key).getPrivate();
	}

	public void saveKeystore(String filePath, String password)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		OutputStream os = new FileOutputStream(new File(filePath));
		keyStore.store(os, getCharArray(password));
	}

	private char[] getCharArray(String str) {
		return (str != null && !str.equalsIgnoreCase("")) ? str.toCharArray()
				: null;
	}

	public static KeyStoreManager getInstance(String keyStoreType,
			String keyStorePath, String password) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		KeyStoreManager manager = new KeyStoreManager();
		manager.createKeyStore(keyStoreType, keyStorePath, password);
		return manager;
	}

	private KeyStoreManager() {
	}

	private KeyStore keyStore;

	public KeyStore getKeyStore() {
		return keyStore;
	}

	public static PrivateKey getInstanceKey(String keyStoreType,
			String keyStorePath, String password,Config config) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		KeyStoreManager manager = new KeyStoreManager();
		PrivateKey privateKey= 	manager.createKeyStoreForEncy(keyStoreType, keyStorePath, password,config);
		return privateKey;
	}

	

}
