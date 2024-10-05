package com.grtship.mdm.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.ObjectCodeDTO;
import com.grtship.mdm.domain.ObjectCode;
import com.grtship.mdm.mapper.ObjectCodeMapper;
import com.grtship.mdm.repository.ObjectCodeRepository;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.ObjectCodeService;

/**
 * Service Implementation for managing {@link ObjectCode}.
 */
@Service
@Transactional
public class ObjectCodeServiceImpl implements ObjectCodeService {

	private final Logger log = LoggerFactory.getLogger(ObjectCodeServiceImpl.class);

	@Autowired
	private ObjectCodeRepository objectCodeRepository;

	@Autowired
	private ObjectCodeMapper objectCodeMapper;

	@Autowired
	private CodeGeneratorService codeGeneratorService;

	@Value("${object-code.default-block-size}")
	private Integer blockSize;

	/**
	 * Save a objectCode.
	 *
	 * @param objectCodeDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.OBJECT_CODE)
	public ObjectCodeDTO save(ObjectCodeDTO objectCodeDTO) {
		log.debug("Request to save ObjectCode : {}", objectCodeDTO);
		if (objectCodeDTO.getBlockSize() == null
				|| (objectCodeDTO.getBlockSize() != null && objectCodeDTO.getBlockSize().equals(0))) {
			objectCodeDTO.setBlockSize(blockSize);
		}
		ObjectCode objectCode = objectCodeMapper.toEntity(objectCodeDTO);
		objectCode = objectCodeRepository.save(objectCode);
		return objectCodeMapper.toDto(objectCode);
	}

	/**
	 * Get all the objectCodes.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ObjectCodeDTO> findAll(Pageable pageable) {
		log.debug("Request to get all ObjectCodes");
		return objectCodeRepository.findAll(pageable).map(objectCodeMapper::toDto);
	}

	/**
	 * Get one objectCode by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ObjectCodeDTO> findOne(Long id) {
		log.debug("Request to get ObjectCode : {}", id);
		return objectCodeRepository.findById(id).map(objectCodeMapper::toDto);
	}

	/**
	 * Delete the objectCode by id.
	 *
	 * @param id the id of the entity.
	 */
	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.OBJECT_CODE)
	public void delete(Long id) {
		log.debug("Request to delete ObjectCode : {}", id);

		objectCodeRepository.deleteById(id);
	}

	/**
	 * Get one objectCode by objectName.
	 *
	 * @param2 objectName the objectName of the entity.
	 * @param2 parentCode the parentCode of entity
	 * 
	 * @return the entity.
	 */
	@Override
	public ObjectCodeDTO findByObjectNameAndParentCode(String objectName, String parentCode) {
		log.debug("Request to get ObjectCode by objectName, parentCode : {}{}", objectName, parentCode);
		ObjectCode objectCode = objectCodeRepository.findByObjectNameAndParentCode(objectName, parentCode);
		return objectCodeMapper.toDto(objectCode);
	}

	@Override
	public List<ObjectCodeDTO> findByObjectName(String objectName) {
		return objectCodeRepository.findByObjectNameAndPrefixNotNullOrderByLastModifiedDateDesc(objectName).stream()
				.map(objectCodeMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public String generateCode(String objectName, String parentCode) {
		return codeGeneratorService.generateCode(objectName, parentCode);
	}

}
