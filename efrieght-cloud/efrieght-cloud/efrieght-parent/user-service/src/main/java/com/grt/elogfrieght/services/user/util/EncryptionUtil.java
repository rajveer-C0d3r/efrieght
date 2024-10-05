/**
 * 
 */
package com.grt.elogfrieght.services.user.util;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class EncryptionUtil {
	
	
	private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);

	
	@Value("${jwt.encrption.secret}")
	private String secretKey;
	
	@Value("${jwt.encrption.salt}")
	private String salt;
	
	public String encrptionUsingAes(String token) {
		logger.info("START method encrypt");
	    try
	    {
	        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ivspec = new IvParameterSpec(iv);
	         
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
	        SecretKey tmp = factory.generateSecret(spec);
	        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
	        return Base64.getEncoder().encodeToString(cipher.doFinal(token.getBytes("UTF-8")));
	    }
	    catch (Exception e)
	    {
	        logger.info("Error while encrypting: " + e.toString());
	    }
	    logger.info("END method encrypt");
	    return null;
	}

}
