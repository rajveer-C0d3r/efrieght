package com.grtship.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.grtship.core.enumeration.ActionType;
import com.grtship.core.enumeration.ModuleName;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
     ActionType action() default ActionType.SAVE;
     ModuleName moduleName();
}
