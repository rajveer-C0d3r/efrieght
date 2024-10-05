package com.grtship.mdm.service;

import java.util.List;

import com.grtship.core.dto.KeyLabelBeanDTO;

/**
 * Service Interface for staticList
 */
public interface StaticListService {

	public <T extends Enum<T>> List<KeyLabelBeanDTO> getList(Class<T> enumType);

}
