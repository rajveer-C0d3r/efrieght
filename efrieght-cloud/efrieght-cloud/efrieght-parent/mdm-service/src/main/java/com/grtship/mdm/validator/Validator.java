package com.grtship.mdm.validator;

import java.util.List;

public interface Validator<T> {
	List<ValidationError> validate(T obj);
}
