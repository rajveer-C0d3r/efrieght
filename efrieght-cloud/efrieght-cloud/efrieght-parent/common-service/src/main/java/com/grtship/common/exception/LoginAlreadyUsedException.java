package com.grtship.common.exception;

import com.grtship.core.constant.ErrorConstant;

public class LoginAlreadyUsedException extends BadRequestAlertException {

	private static final long serialVersionUID = 1L;

	public LoginAlreadyUsedException() {
		super(ErrorConstant.LOGIN_ALREADY_USED_TYPE, "Login name already used!", "userManagement", "userexists");
	}
}
