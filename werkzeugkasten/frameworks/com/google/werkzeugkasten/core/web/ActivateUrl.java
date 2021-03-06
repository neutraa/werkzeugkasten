package com.google.werkzeugkasten.core.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target( { ElementType.TYPE, ElementType.METHOD })
public @interface ActivateUrl {

	String value();

	RequestMethod[] method() default { RequestMethod.GET, RequestMethod.POST };

	@SuppressWarnings("unchecked")
	Class<? extends RequestMatcher> matcher() default RequestMatcher.class;
}
