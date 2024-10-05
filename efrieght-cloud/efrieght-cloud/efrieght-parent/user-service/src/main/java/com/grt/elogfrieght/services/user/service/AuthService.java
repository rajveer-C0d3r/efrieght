/**
 * 
 */
package com.grt.elogfrieght.services.user.service;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.InvalidCredentialsException;

import com.grt.elogfrieght.services.user.dto.AuthResponseDto;
import com.grtship.core.dto.LoggedInUserDto;
import com.grtship.core.dto.UserAuthorityDto;
import com.grtship.core.dto.UserDetailDto;

/**
 * @author Ajay
 *
 */
public interface AuthService {

	AuthResponseDto authenticate(HttpServletRequest request, HttpServletResponse response,
			Map<String, String> params) throws NoSuchAlgorithmException, InvalidCredentialsException;

	AuthResponseDto renewToken(String refreshToken, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws NoSuchAlgorithmException;

	LoggedInUserDto getLoggedInUser();

	UserDetailDto getUserDetails(String username);

	UserAuthorityDto getUserAuthority();


}
