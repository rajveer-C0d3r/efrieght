/**
 * 
 */
package com.grtship.mdm.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.grtship.core.context.holder.ServiceContextHolder;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class ServiceContextFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(ServiceContextFilter.class);

	public ServiceContextFilter() {
		log.info("ServiceContextFilter loaded ---> ");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("request Filter works");
		if (request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
			String token = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (StringUtils.isNotEmpty(token)) {
				log.info("token value : {} ", token);
				try {
					ServiceContextHolder.setTokenContext(token);
					filterChain.doFilter(request, response);

				} finally {
					ServiceContextHolder.close();
				}
			}
		}
	}

}
