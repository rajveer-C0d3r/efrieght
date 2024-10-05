package com.grtship.common.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.grtship.common.dto.AccessControlDTO;

@Component
public class SpringBeans {
	@Autowired
	private static ApplicationContext applicationContext;
	
	SpringBeans(@Autowired ApplicationContext ctx){
		SpringBeans.applicationContext=ctx;
	}
	
	public static AccessControlDTO getAccessDTO(){
		return applicationContext.getBean(AccessControlDTO.class);
	}
}
