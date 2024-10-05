package com.grtship.common.exception;
import java.util.ArrayList;
import java.util.List;

import com.grtship.core.exception.FieldMessage;


public class ConstraintViolationException extends ValidationException {
	private static final long serialVersionUID = -2547022139837613657L;
	private List<FieldMessage> violations;

	private void addViolation(FieldMessage violation) {
		if (this.violations == null) {
			this.violations = new ArrayList<FieldMessage>();
		}
		this.violations.add(violation);
	}

	public List<FieldMessage> getViolations() {
		return violations;
	}

	public void setViolations(List<FieldMessage> violations) {
		this.violations = violations;
	}

	public ConstraintViolationException() {
		this.violations = new ArrayList<FieldMessage>();
	}

	public ConstraintViolationException(List<FieldMessage> violations) {
		this.violations = violations;
	}

	public ConstraintViolationException(FieldMessage fieldMessage) {
		this.addViolation(fieldMessage);
	}
}
