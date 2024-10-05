/**
 * 
 */
package com.grtship.common.interceptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.grtship.common.beans.SpringBeans;
import com.grtship.core.context.holder.ServiceContextHolder;

import feign.RequestInterceptor;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class FeignInterceptorConfig {

	private static final Logger log = LoggerFactory.getLogger(FeignInterceptorConfig.class);
	public static final String CLEINT_ID = "clientId";
	public static final String COMPANY_ID = "companyId";
	public static final String BRANCH_ID = "branchId";

	@Bean
	public RequestInterceptor requestInterceptor() {
		log.info("feign call intercepted internally");
		log.info("setting token in feign call ");
		log.info("token value from context : {} ", ServiceContextHolder.getTokenContext());
//		String companyId = null;
//		Map<String, Collection<String>> headers=new HashMap<>();
//		if(ObjectUtils.isNotEmpty(ServiceContextHolder.getTokenContext())) {
//			headers.put(HttpHeaders.AUTHORIZATION,Arrays.asList(ServiceContextHolder.getTokenContext()));	
//		}
//		headers.put(CLEINT_ID,Arrays.asList(getClientId()));
//		headers.put(COMPANY_ID,Arrays.asList(getCompanyId()));
//		headers.put(BRANCH_ID,Arrays.asList(getBranchId()));
		return requestTemplate -> {
		      requestTemplate.header(HttpHeaders.AUTHORIZATION,ServiceContextHolder.getTokenContext());
		      requestTemplate.header(CLEINT_ID, getClientId());
		      requestTemplate.header(COMPANY_ID,getCompanyId());
		  };
//		return requestTemplate -> requestTemplate.header(HttpHeaders.AUTHORIZATION,
//				ServiceContextHolder.getTokenContext(),
//				"clientId",getClientId(),
//				"companyId",getCompanyId());

	}
	
	public Map<String, Collection<String>> getHeaders() {
		Map<String, Collection<String>> headers=new HashMap<>();
		if(ObjectUtils.isNotEmpty(ServiceContextHolder.getTokenContext())) {
			headers.put(HttpHeaders.AUTHORIZATION,Arrays.asList(ServiceContextHolder.getTokenContext()));	
		}
		headers.put(CLEINT_ID,Arrays.asList(getClientId()));
		headers.put(COMPANY_ID,Arrays.asList(getCompanyId()));
		headers.put(BRANCH_ID,Arrays.asList(getBranchId()));
		return headers;
	}
	
	public static String getClientId() {
		String clientId=null;
		if(ObjectUtils.isNotEmpty(SpringBeans.getAccessDTO())) {
			if(ObjectUtils.isNotEmpty(SpringBeans.getAccessDTO().getClientId())) {
				clientId=String.valueOf(SpringBeans.getAccessDTO().getClientId());
			}
		}
		return clientId;
	}
	
	
	public static String getCompanyId() {
		String companyId=null;
		if(ObjectUtils.isNotEmpty(SpringBeans.getAccessDTO())) {
			if(ObjectUtils.isNotEmpty(SpringBeans.getAccessDTO().getCompanyId())) {
				companyId=String.valueOf(SpringBeans.getAccessDTO().getCompanyId());
			}
		}
		return companyId;
	}
	
	public static String getBranchId() {
		String branchId=null;
		if(ObjectUtils.isNotEmpty(SpringBeans.getAccessDTO())) {
			if(ObjectUtils.isNotEmpty(SpringBeans.getAccessDTO().getBranchId())) {
				branchId=String.valueOf(SpringBeans.getAccessDTO().getBranchId());
			}
		}
		return branchId;
	}

}
