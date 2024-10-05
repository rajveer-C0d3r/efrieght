package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.KeyLabelBeanDTO;
import com.grtship.mdm.service.StaticListService;

/**
 * Service Implementation for StaticList.
 */
@Service
@Transactional
public class StaticListServiceImpl implements StaticListService {

	private final Logger log = LoggerFactory.getLogger(StaticListServiceImpl.class);

	public <T extends Enum<T>> List<KeyLabelBeanDTO> getList(Class<T> enumType) {

		List<KeyLabelBeanDTO> keyLabelList = new ArrayList<>();
		for (T c : enumType.getEnumConstants()) {
			log.debug("Enum {}", enumType);
			if (c != null) {
				String key = c.name();
				String label = c.toString();
				log.debug("name: {}", key);
				log.debug("values {}", label);
				keyLabelList.add(new KeyLabelBeanDTO(key, label));
			}
		}
		return keyLabelList;
	}

}
