/**
 * 
 */
package com.grtship.audit.serviceimpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grtship.audit.domain.SystemAudit;
import com.grtship.audit.domain.SystemAuditData;
import com.grtship.audit.repository.SystemAuditRepository;
import com.grtship.audit.service.AuditService;
import com.grtship.common.util.JsonToMapConvertor;
import com.grtship.core.dto.AuditDataWrapper;

/**
 * @author ER Ajay Sharma
 *
 */
@Service
public class AuditServiceImpl implements AuditService {

	private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

	@Autowired
	private SystemAuditRepository auditRepository;

	@Autowired
	private JsonToMapConvertor jsonToMapConverter;

	private static final String ID = "id";

	/**
	 *
	 */
	@Override
	public void saveAudit(String message) {
		log.info("Reached save audit service method : {} ", message);
		try {
			if (StringUtils.isNotEmpty(message)) {
				executeProcess(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeProcess(String message) {
		AuditDataWrapper dataWrapper = extractAuditWrapperData(message);
		if (!dataWrapper.getPayload().isEmpty()) {
			executeAuditing(message, dataWrapper.getPayload(), dataWrapper);
		}
	}

	private void executeAuditing(String message, Map<String, Object> jsonObject, AuditDataWrapper dataWrapper) {
		if (ObjectUtils.isNotEmpty(dataWrapper)) {
			addUpdateAuditData(jsonObject, dataWrapper);
		}
	}

	private void addUpdateAuditData(Map<String, Object> jsonObject, AuditDataWrapper dataWrapper) {
		SystemAudit saveAudit;
		Map<String, String> data = null;
		Long referenceId = extractReferenceIdFromPayload(dataWrapper.getPayload());
		List<SystemAudit> systemList = getPreviousVersionOfAudit(dataWrapper.getHeader().getReferenceType(),
				referenceId);
		if (!CollectionUtils.isEmpty(systemList)) {
			saveAudit = updateAuditingProcessing(jsonObject, dataWrapper, data, systemList, referenceId);
		} else {
			saveAudit = saveAuditingProcessing(jsonObject, dataWrapper, referenceId);
		}
		auditRepository.save(saveAudit);
	}

	private Long extractReferenceIdFromPayload(Map<String, Object> payload) {
		Object referenceID = null;
		if (payload.containsKey(ID)) {
			referenceID = payload.get(ID);
			log.info("reference Id : {} ", referenceID);
		}
		return ObjectUtils.isNotEmpty(referenceID) ? Long.parseLong(String.valueOf(referenceID)) : 0L;
	}

	private SystemAudit saveAuditingProcessing(Map<String, Object> jsonObject, AuditDataWrapper dataWrapper,
			Long referenceId) {
		SystemAudit saveAudit;
		Map<String, String> data;
		JSONObject jsonData = new JSONObject(jsonObject);
		data = extractAuditData(jsonData, dataWrapper.getHeader().getReferenceType());
		saveAudit = new SystemAudit();
		saveAudit.setAction(dataWrapper.getHeader().getAction());
		saveAudit.setAuditData(convertToAuditData(data));
		saveAudit.setReferenceId(referenceId);
		saveAudit.setReferenceType(dataWrapper.getHeader().getReferenceType());
		saveAudit.setVersion(1L);
		saveAudit.setSequence(dataWrapper.getHeader().getSequence());
		saveAudit.setCreatedBy(dataWrapper.getHeader().getOwner());
		saveAudit.setCreatedOnDate(LocalDate.now());
		saveAudit.setCreatedOnTime(LocalTime.now());
		return saveAudit;
	}

	private SystemAudit updateAuditingProcessing(Map<String, Object> jsonObject, AuditDataWrapper dataWrapper,
			Map<String, String> data, List<SystemAudit> systemList, Long referenceId) {
		SystemAudit saveAudit;
		SystemAudit systemAudit;
		systemAudit = systemList.stream().findFirst().get();
		JSONObject jsonData = new JSONObject(jsonObject);
		saveAudit = updateAudit(jsonData, systemAudit, dataWrapper, data, referenceId);
		return saveAudit;
	}

	private SystemAudit updateAudit(JSONObject jsonObject, SystemAudit systemAudit, AuditDataWrapper dataWrapper,
			Map<String, String> data, Long referenceId) {
		SystemAudit saveAudit;
		Map<String, String> previousAuditData;
		Map<String, String> recentAuditData = new HashMap<>();
		previousAuditData = convertDataToMap(systemAudit.getAuditData());
		saveAudit = new SystemAudit();
		if (!MapUtils.isEmpty(previousAuditData)) {
			recentAuditData = extractAuditData(jsonObject, dataWrapper.getHeader().getReferenceType());
			data = compareAuditData(previousAuditData, recentAuditData);
			saveAudit.setAuditData(setAuditData(previousAuditData, recentAuditData));
		}
		saveAudit.setAction(dataWrapper.getHeader().getAction());
//		saveAudit.setAuditData(convertToAuditData(previousAuditData, recentAuditData));
		saveAudit.setReferenceId(referenceId);
		saveAudit.setReferenceType(dataWrapper.getHeader().getReferenceType());
		saveAudit.setVersion(systemAudit.getVersion() + 1);
		saveAudit.setSequence(dataWrapper.getHeader().getSequence());
		saveAudit.setCreatedBy(dataWrapper.getHeader().getOwner());
		saveAudit.setCreatedOnDate(LocalDate.now());
		saveAudit.setCreatedOnTime(LocalTime.now());
		return saveAudit;
	}

	private Map<String, String> extractAuditData(JSONObject jsonData, String referenceType) {
		return jsonToMapConverter.initiateJsonConvertion(jsonData, referenceType);
	}

	private List<SystemAudit> getPreviousVersionOfAudit(String referenceType, Long id) {
		List<SystemAudit> systemList = auditRepository.findByReferenceIdAndReferenceTypeOrderByVersionDesc(id,
				referenceType);
		return systemList;
	}

	private AuditDataWrapper extractAuditWrapperData(String message) {
		AuditDataWrapper preParameter = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			preParameter = mapper.readValue(message, new TypeReference<AuditDataWrapper>() {
			});

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return preParameter;
	}

	/**
	 * @param previousData
	 * @param recentData
	 * @return
	 */
	private Map<String, String> compareAuditData(Map<String, String> previousData, Map<String, String> recentData) {
		Map<String, String> data = new HashMap<>();
		for (String prekey : recentData.keySet()) {
			if (previousData.containsKey(prekey)) {
				if (previousData.get(prekey) != null && recentData.get(prekey) != null
						&& !(previousData.get(prekey).equals(recentData.get(prekey)))) {
					data.put(prekey, recentData.get(prekey));
				}
			} else {
				data.put(prekey, recentData.get(prekey));
			}

		}
		return data;
	}

	private List<SystemAuditData> setAuditData(Map<String, String> previousData, Map<String, String> recentData) {
		List<SystemAuditData> auditData = new ArrayList<>();
		for (String recentkey : recentData.keySet()) {
			if (previousData.containsKey(recentkey)) {
				if ((ObjectUtils.isNotEmpty(previousData.get(recentkey))
						&& ObjectUtils.isNotEmpty(recentData.get(recentkey)))
						&& !(previousData.get(recentkey).equals(recentData.get(recentkey)))) {
					getDifferenceInAuditData(previousData, recentData, auditData, recentkey, recentkey);
				}
			} else {
				getWithoutDiffAuditData(recentData, auditData, recentkey);
			}

		}
		return auditData;
	}

	private Map<String, String> convertDataToMap(List<SystemAuditData> data) {
		if (CollectionUtils.isNotEmpty(data)) {
			return data.stream()
					.collect(Collectors.toMap(SystemAuditData::getPropertyName, SystemAuditData::getNewValue));
		}
		return null;
	}

	private void newAuditValue(Map<String, String> recentData, List<SystemAuditData> auditData, String recentMapKey) {
		SystemAuditData data;
		data = new SystemAuditData();
		data.setPropertyName(recentMapKey);
		data.setOldValue("-");
		data.setNewValue(recentData.get(recentMapKey));
		auditData.add(data);
	}

	private void getDifferenceInAuditData(Map<String, String> previousData, Map<String, String> recentData,
			List<SystemAuditData> auditData, String recentMapKey, String previousMapKey) {
		SystemAuditData data = new SystemAuditData();
		data.setPropertyName(recentMapKey);
		data.setOldValue(previousData.get(previousMapKey));
		data.setNewValue(recentData.get(recentMapKey));
		auditData.add(data);
	}

	private void getWithoutDiffAuditData(Map<String, String> recentData, List<SystemAuditData> auditData,
			String recentMapKey) {
		SystemAuditData data;
		data = new SystemAuditData();
		data.setPropertyName(recentMapKey);
		data.setOldValue("-");
		data.setNewValue(recentData.get(recentMapKey));
		auditData.add(data);
	}

	private List<SystemAuditData> convertToAuditData(Map<String, String> jsonData) {
		List<SystemAuditData> auditData = new ArrayList<>();
		if (MapUtils.isNotEmpty(jsonData)) {
			for (String previousMapKey : jsonData.keySet()) {
				newAuditValue(jsonData, auditData, previousMapKey);
			}
		}
		return auditData;
	}

}
