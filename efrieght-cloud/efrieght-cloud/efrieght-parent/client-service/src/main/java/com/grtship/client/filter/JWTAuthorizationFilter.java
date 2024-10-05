/**
 * 
 */
package com.grtship.client.filter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.grtship.client.adaptor.UserModuleAdaptor;
import com.grtship.client.util.JwtTokenUtil;
import com.grtship.core.dto.UserDetailDto;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserModuleAdaptor userModuleAdaptor;

	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
		log.info("Jwt Authorization filter loaded --->  ");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		String header = req.getHeader(HttpHeaders.AUTHORIZATION);

		if (header == null || !header.startsWith("Bearer")) {
			log.info(" " + HttpHeaders.AUTHORIZATION + " not present bypassing the request to requested url");
			chain.doFilter(req, res);
		} else {
			log.info(" " + HttpHeaders.AUTHORIZATION
					+ " found in the request starting to create authentication princle from token");
			log.info("requested url : {} ", req.getRequestURL());
			UsernamePasswordAuthenticationToken authentication = null;
			try {
				authentication = getAuthentication(req);
				if (authentication != null) {
					log.info("Authentication object created successfully");
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
				chain.doFilter(req, res);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}
	}

	// Reads the JWT from the Authorization header, and then uses JWT to validate
	// the token
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (StringUtils.isNotEmpty(token)) {
			// parse the token.
			String accessToken = token.replace("Bearer ", "");
			String user = jwtTokenUtil.getUsernameFromAccessToken(accessToken);
			if (user != null) {
				log.info("user-service called to get user details : ");
				UserDetailDto userDetails = userModuleAdaptor.getUserDetails(user);
				if (ObjectUtils.isNotEmpty(userDetails)) {
					log.info("get response from user service : {} ", userDetails);
					return createAuthenticationPrinciple(userDetails);
				}
			}
		}

		return null;
	}

	private UsernamePasswordAuthenticationToken createAuthenticationPrinciple(UserDetailDto detailDto) {
		if (detailDto != null) {
			log.info("user-detail object found in the response with details : {} ", detailDto);
			log.info("creating user authentication principle from details");
			log.info("commma seperated authority : {} ", detailDto.getCommmaSeperatedAuthority());
			return new UsernamePasswordAuthenticationToken(detailDto.getEmail(), null, AuthorityUtils.commaSeparatedStringToAuthorityList(detailDto.getCommmaSeperatedAuthority()));
		}
		return null;
	}
}
