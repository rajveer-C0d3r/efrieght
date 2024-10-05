package com.grtship.client.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessFilter {
	
	 boolean clientAccessFlag() default false;
	 
	 boolean companyAccessFlag() default false;
	 
	 boolean branchAccessFlag() default false;
}
