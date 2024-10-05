package com.grtship.common.interfaces;

import java.util.List;

public interface Validator<T> {
	List<ValidationError> validate(T obj, String action);
}
