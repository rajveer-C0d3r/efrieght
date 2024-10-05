package com.grtship.mdm.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.MemoryDTO;
import com.grtship.core.dto.ObjectCodeDTO;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.ObjectCodeService;

@Service
@Transactional
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

	private final Logger log = LoggerFactory.getLogger(CodeGeneratorServiceImpl.class);

	@Autowired
	private ObjectCodeService objectCodeService;

	private Map<String, MemoryDTO> data = new HashMap<>();

	@Override
	public String generateCode(String objectName, String parentCode) {
		log.debug("In generate Code");
		return getCode(objectName, parentCode);
	}

	private String getCode(String objectName, String parentCode) {
		ObjectCodeDTO objectCodeDto = getObjectCodeByObjectName(objectName, parentCode);
		Long counter = objectCodeDto.getCounter();
		Integer blockSize = objectCodeDto.getBlockSize();
		Integer max;
		if (counter.equals(0L)) {
			max = blockSize;
			prepareAndSaveData(objectName, objectCodeDto, counter, max);
			return generateCode(objectCodeDto);
		} else {
			if (!data.containsKey(objectName)) {
				max = counter.intValue() + blockSize;
				prepareAndSaveData(objectName, objectCodeDto, counter, max);
				return generateCode(objectCodeDto);
			} else if (counter.equals(data.get(objectName).getCounter())) {
				MemoryDTO memoryDto = data.get(objectName);
				memoryDto.setMax(memoryDto.getMax() + blockSize);
				data.put(objectName, memoryDto);
				objectCodeDto.setCounter(Long.valueOf(memoryDto.getMax()));
				objectCodeService.save(objectCodeDto);
				return generateCode(objectCodeDto);
			}
		}
		return generateCode(objectCodeDto);
	}

	private void prepareAndSaveData(String objectName, ObjectCodeDTO objectCodeDto, Long counter, Integer max) {
		objectCodeDto.setCounter(Long.valueOf(max));
		objectCodeService.save(objectCodeDto);
		MemoryDTO memoryDto = new MemoryDTO();
		memoryDto.setCounter(counter);
		memoryDto.setMax(max);
		data.put(objectName, memoryDto);
	}

	private String generateCode(ObjectCodeDTO objectCodeDto) {
		MemoryDTO memoryDto = data.get(objectCodeDto.getObjectName());
		Long counter = memoryDto.getCounter() + 1;
		memoryDto.setCounter(counter);
		data.put(objectCodeDto.getObjectName(), memoryDto);
		if (StringUtils.isNotBlank(objectCodeDto.getPrefix())
				&& StringUtils.isNotBlank(objectCodeDto.getParentCode())) {
			return objectCodeDto.getPrefix() + objectCodeDto.getParentCode()
					+ getCounterWithPadding(objectCodeDto, counter);
		} else if (StringUtils.isBlank(objectCodeDto.getPrefix())
				&& StringUtils.isNotBlank(objectCodeDto.getParentCode())) {
			return objectCodeDto.getParentCode() + getCounterWithPadding(objectCodeDto, counter);
		} else if (StringUtils.isNotBlank(objectCodeDto.getPrefix())
				&& StringUtils.isBlank(objectCodeDto.getParentCode())) {
			return objectCodeDto.getPrefix() + getCounterWithPadding(objectCodeDto, counter);
		}
		return getCounterWithPadding(objectCodeDto, counter);
	}

	private String getCounterWithPadding(ObjectCodeDTO objectCodeDto, Long counter) {

		StringBuilder code = new StringBuilder();
		code.append("%0");
		code.append(objectCodeDto.getPadding());
		code.append("d");
		return String.format(code.toString(), counter);
	}

	private ObjectCodeDTO getObjectCodeByObjectName(String objectName, String parentCode) {
		ObjectCodeDTO objectCodeDto = objectCodeService.findByObjectNameAndParentCode(objectName, parentCode);
		if (objectCodeDto == null) {
			objectCodeDto = new ObjectCodeDTO();
			objectCodeDto.setObjectName(objectName);
			objectCodeDto.setParentCode(parentCode);
			objectCodeDto.setCounter(0L);
			List<ObjectCodeDTO> objectCodeList = objectCodeService.findByObjectName(objectName);
			if (!CollectionUtils.isEmpty(objectCodeList)) {
				objectCodeDto.setPrefix(objectCodeList.get(0).getPrefix());
				objectCodeDto.setPadding(objectCodeList.get(0).getPadding());
			}
			objectCodeDto = objectCodeService.save(objectCodeDto);
		}
		return objectCodeDto;
	}

}
