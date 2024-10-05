/**
 * 
 */
package com.grt.elogfrieght.services.user.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class JwtGenerator {

	private static final Logger log = LoggerFactory.getLogger(JwtGenerator.class);

	@Value("${jwt.accesstoken.privateKey}")
	private String accessTokenPrivateKey;

	@Value("${jwt.accesstoken.publicKey}")
	private String accessTokenPublicKey;

	@Value("${jwt.accesstoken.expiryTime}")
	private int accessTokenExpireTime;

	@Value("${jwt.refreshtoken.privateKey}")
	private String refreshTokenPrivateKey;

	@Value("${jwt.refreshtoken.publicKey}")
	private String refreshTokenPublicKey;

	@Value("${jwt.refreshtoken.expiryTime}")
	private int refreshTokenExpireTime;

	public String buildToken(String subject, String issuer, Map<String, Object> claims)
			throws NoSuchAlgorithmException {
		String token = "";
		KeyFactory factory = KeyFactory.getInstance("RSA");
		byte[] decode = Base64Codec.BASE64.decode(accessTokenPrivateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
		try {
			PrivateKey generatePrivate = factory.generatePrivate(keySpec);
			token = generateJwtToken(generatePrivate, subject, issuer, claims);
		} catch (InvalidKeySpecException e) {
			log.error("exception occured while generating token : {} ", e.getMessage());
		}
		return token;
	}

	public String generateJwtToken(PrivateKey privateKey, String subject, String issuer, Map<String, Object> claims) {
		return Jwts.builder().setSubject(subject).setExpiration(DateUtils.addHours(new Date(), accessTokenExpireTime))
				.setIssuer(issuer).addClaims(claims).signWith(SignatureAlgorithm.RS256, privateKey).compact();
	}

	public Claims decryptJwtToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		byte[] publicDecode = Base64Codec.BASE64.decode(accessTokenPublicKey);
		X509EncodedKeySpec keyPublic = new X509EncodedKeySpec(publicDecode);
		PublicKey generatePublic = factory.generatePublic(keyPublic);
		Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey(generatePublic).parseClaimsJws(token);
		return parseClaimsJws.getBody();
	}

	public String buildRefreshToken(String subject, String issuer, Map<String, Object> claims)
			throws NoSuchAlgorithmException {
		String token = "";
		KeyFactory factory = KeyFactory.getInstance("RSA");
		byte[] decode = Base64Codec.BASE64.decode(refreshTokenPrivateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
		try {
			PrivateKey generatePrivate = factory.generatePrivate(keySpec);
			token = generateJwtRefreshToken(generatePrivate, subject, issuer, claims);
		} catch (InvalidKeySpecException e) {
			log.error("exception occured while generating token : {} ", e.getMessage());
		}
		return token;
	}

	public String generateJwtRefreshToken(PrivateKey privateKey, String subject, String issuer,
			Map<String, Object> claims) {
		return Jwts.builder().setSubject(subject).setExpiration(DateUtils.addHours(new Date(), refreshTokenExpireTime))
				.setIssuer(issuer).addClaims(claims).signWith(SignatureAlgorithm.RS256, privateKey).compact();
	}

	public Claims decryptJwtRefreshToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		byte[] publicDecode = Base64Codec.BASE64.decode(refreshTokenPublicKey);
		X509EncodedKeySpec keyPublic = new X509EncodedKeySpec(publicDecode);
		PublicKey generatePublic = factory.generatePublic(keyPublic);
		Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey(generatePublic).parseClaimsJws(token);
		return parseClaimsJws.getBody();
	}
}
