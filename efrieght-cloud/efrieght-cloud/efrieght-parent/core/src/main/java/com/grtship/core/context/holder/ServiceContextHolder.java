/**
 * 
 */
package com.grtship.core.context.holder;

import com.grtship.core.context.ServiceContext;

/**
 * @author ER Ajay Sharma
 *
 */

public class ServiceContextHolder {
	private static ThreadLocal<ServiceContext> context = new ThreadLocal<ServiceContext>() {
		@Override
		protected ServiceContext initialValue() {
			return new ServiceContext();
		}
	};

	public static String getTokenContext() {
		return context.get().getToken();
	}

	public static void setTokenContext(String token) {
		ServiceContextHolder.context.get().setToken(token);
	}

	public static void close() {
		context.remove();
	}

}
