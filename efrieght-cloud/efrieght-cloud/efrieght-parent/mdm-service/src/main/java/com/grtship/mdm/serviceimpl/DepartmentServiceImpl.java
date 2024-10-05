package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.DepartmentDTO;
import com.grtship.mdm.criteria.DepartmentCriteria;
import com.grtship.mdm.domain.Department;
import com.grtship.mdm.mapper.DepartmentMapper;
import com.grtship.mdm.repository.DepartmentRepository;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.DepartmentFilterService;
import com.grtship.mdm.service.DepartmentService;

/**
 * Service Implementation for managing {@link Department}.
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

	private static final String DEPARTMENT = "Department";

	private final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private DepartmentMapper departmentMapper;

	@Autowired
	private CodeGeneratorService codeGeneratorService;

	@Autowired
	private DepartmentFilterService departmentFilterService;

	@Override
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.DEPARTMENT)
	@Validate(validator = "departmentValidator",action = "save")
	public DepartmentDTO save(DepartmentDTO departmentDto) {
		log.debug("Request to save Department : {}", departmentDto);
		if (departmentDto.getId() == null)
			departmentDto.setCode(codeGeneratorService.generateCode(DEPARTMENT, null));
		Department department = departmentMapper.toEntity(departmentDto);
		department = departmentRepository.save(department);
		return departmentMapper.toDto(department);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DepartmentDTO> findAll(DepartmentCriteria departmentCriteria, Pageable pageable) {
		log.debug("Request to get all Departments");
		return departmentFilterService.findByCriteria(departmentCriteria, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<DepartmentDTO> findOne(Long id) {
		log.debug("Request to get Department : {}", id);
		DepartmentCriteria departmentCriteria = new DepartmentCriteria();
		List<Long> ids = new ArrayList<>();
		ids.add(id);
		departmentCriteria.setIds(ids);
		List<Department> findByCriteria = departmentFilterService.findByCriteria(departmentCriteria);
		departmentRepository.findById(id);
		DepartmentDTO departmentDTO=null;
		if (!findByCriteria.isEmpty()) {
			departmentDTO=departmentMapper.toDto(departmentFilterService.findByCriteria(departmentCriteria).get(0));
		}
		return Optional.ofNullable(departmentDTO);
	}

	@Override
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.DEPARTMENT)
	public void delete(Long id) {
		log.debug("Request to delete Department : {}", id);
		departmentRepository.deleteById(id);
	}

	@Override
	public List<DepartmentDTO> getDepartmentsByIds(List<Long> departmentIds) {
		DepartmentCriteria departmentCriteria = new DepartmentCriteria();
		departmentCriteria.setIds(departmentIds);
		return departmentMapper.toDto(departmentFilterService.findByCriteria(departmentCriteria));
	}

}
