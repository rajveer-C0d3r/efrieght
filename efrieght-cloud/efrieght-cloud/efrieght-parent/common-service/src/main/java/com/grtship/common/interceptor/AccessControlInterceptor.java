package com.grtship.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.grtship.common.beans.SpringBeans;

@Component
public class AccessControlInterceptor extends HandlerInterceptorAdapter {
	
	
	private static final Logger log = LoggerFactory.getLogger(AccessControlInterceptor.class);


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		log.info("request : {} ",request);
		log.info("response : {} ",response);
		log.info("handler : {} ",handler);

		Long clientId = (request.getHeader("clientId") != null) ? Long.parseLong(request.getHeader("clientId")) : null;
		Long companyId = (request.getHeader("companyId") != null) ? Long.parseLong(request.getHeader("companyId"))
				: null;
		Long branchId = (request.getHeader("branchId") != null) ? Long.parseLong(request.getHeader("branchId")) : null;

		SpringBeans.getAccessDTO().setClientId(clientId);
		SpringBeans.getAccessDTO().setCompanyId(companyId);
		SpringBeans.getAccessDTO().setBranchId(branchId);

		return true;
	}
}
