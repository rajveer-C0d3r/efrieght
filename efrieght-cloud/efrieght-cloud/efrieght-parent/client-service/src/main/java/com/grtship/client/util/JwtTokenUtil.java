/**
 * 
 */
package com.grtship.client.util;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	@Value("${jwt.accesstoken.publicKey}")
	private String accessTokenPublicKey;
	
	@Value("${jwt.refreshtoken.publicKey}")
	private String refreshTokenPublicKey;

	// retrieve username from jwt token
	public String getUsernameFromAccessToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws NoSuchAlgorithmException, InvalidKeySpecException {
		final Claims claims = getAllClaimsFromAccessToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromAccessToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		byte[] publicDecode = Base64Codec.BASE64.decode(accessTokenPublicKey);
		X509EncodedKeySpec keyPublic = new X509EncodedKeySpec(publicDecode);
		PublicKey generatePublic = factory.generatePublic(keyPublic);
		return Jwts.parser().setSigningKey(generatePublic).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	public Boolean isTokenExpired(String token) {
		Date expiration = null;
		try {
			expiration = getExpirationDateFromToken(token);
		} catch (Exception e) {
			return Boolean.TRUE;
		}
		return expiration.before(new Date());
	}

	public Boolean validateToken(String token) {
		return (isTokenExpired(token));
	}
	
	public Boolean validateRefreshToken(String token) {
		return (isRefreshTokenExpired(token));
	}

	public Boolean isRefreshTokenExpired(String token) {
		Date expiration = null;
		try {
			expiration = getExpirationDateFromRefreshToken(token);
		} catch (Exception e) {
			return Boolean.TRUE;
		}
		return expiration.before(new Date());
	}
	
	public Date getExpirationDateFromRefreshToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return getClaimFromRefreshToken(token, Claims::getExpiration);
	}
	public <T> T getClaimFromRefreshToken(String token, Function<Claims, T> claimsResolver) throws NoSuchAlgorithmException, InvalidKeySpecException {
		final Claims claims = getAllClaimsFromRefreshToken(token);
		return claimsResolver.apply(claims);
	}
	
	private Claims getAllClaimsFromRefreshToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		byte[] publicDecode = Base64Codec.BASE64.decode(refreshTokenPublicKey);
		X509EncodedKeySpec keyPublic = new X509EncodedKeySpec(publicDecode);
		PublicKey generatePublic = factory.generatePublic(keyPublic);
		return Jwts.parser().setSigningKey(generatePublic).parseClaimsJws(token).getBody();
	}
}
