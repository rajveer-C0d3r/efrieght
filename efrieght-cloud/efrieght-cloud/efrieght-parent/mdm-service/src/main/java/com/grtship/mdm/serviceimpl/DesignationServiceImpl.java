package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.DesignationDTO;
import com.grtship.core.dto.ResponseCodeDTO;
import com.grtship.mdm.criteria.DesignationCriteria;
import com.grtship.mdm.domain.Designation;
import com.grtship.mdm.mapper.DesignationMapper;
import com.grtship.mdm.repository.DesignationRepository;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.DesignationService;
import com.grtship.mdm.validator.DesignationValidator;

/**
 * Service Implementation for managing {@link Designation}.
 */
@Service
@Transactional
public class DesignationServiceImpl implements DesignationService {

	private static final String DESIGNATION = "Designation";

	private final Logger log = LoggerFactory.getLogger(DesignationServiceImpl.class);

	private final DesignationRepository designationRepository;

	private final DesignationMapper designationMapper;

	private final CodeGeneratorService codeGeneratorService;

	@Autowired
	private DesignationFilterServiceImpl designationFilterService;
	@Autowired
	private DesignationValidator designationValidator;

	public DesignationServiceImpl(DesignationRepository designationRepository, DesignationMapper designationMapper,
			CodeGeneratorService codeGeneratorService) {
		this.designationRepository = designationRepository;
		this.designationMapper = designationMapper;
		this.codeGeneratorService = codeGeneratorService;
	}

	@Override
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.DESIGNATION)
	@Validate(validator = "designationValidator",action = "save")
	public DesignationDTO save(DesignationDTO designationDto) {
		log.debug("Request to save Designation : {}", designationDto);
		if (designationDto.getId() == null)
			designationDto.setCode(codeGeneratorService.generateCode(DESIGNATION, null));
		Designation designation = designationMapper.toEntity(designationDto);
//		designationValidator.saveValidation(designationDto);
		designation = designationRepository.save(designation);
		return designationMapper.toDto(designation);
	}

	@Override
	@Transactional(readOnly = true)
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public Page<DesignationDTO> findAll(DesignationCriteria designationCriteria, Pageable pageable) {
		log.debug("Request to get all Designations");
		return designationFilterService.findByCriteria(designationCriteria, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<DesignationDTO> findOne(Long id) {
		log.debug("Request to get Designation : {}", id);
		return designationRepository.findById(id).map(designationMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.DESIGNATION)
	public void delete(Long id) {
		log.debug("Request to delete Designation : {}", id);
		designationRepository.deleteById(id);
	}

	@Override
	public ResponseCodeDTO getGeneratedDesignationCode() {
		log.debug("Request to generate Designation Code");
		return new ResponseCodeDTO(codeGeneratorService.generateCode(DESIGNATION, null));
	}

	@Override
	public List<DesignationDTO> getByIds(List<Long> designationIds) {
		if (CollectionUtils.isEmpty(designationIds)) {
			return new ArrayList<>();
		}
		List<DesignationDTO> designations = designationRepository.findAllById(designationIds).stream()
				.map(designationMapper::toDto).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(designations)) {
			return new ArrayList<>();
		}
		return designations;
	}
}
