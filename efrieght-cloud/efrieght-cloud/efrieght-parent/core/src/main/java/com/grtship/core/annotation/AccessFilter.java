/**
 * 
 */
package com.grtship.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ER Ajay Sharma
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessFilter {
	
	 boolean allowAdminData() default true;
	 
	 boolean clientAccessFlag() default false;
	 
	 boolean companyAccessFlag() default false;
	 
	 boolean branchAccessFlag() default false;
}
