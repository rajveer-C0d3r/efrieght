package com.grt.elogfrieght.services.user.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.Deactivate;
import com.grt.elogfrieght.services.user.domain.Reactivate;
import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.mapper.RoleMapper;
import com.grt.elogfrieght.services.user.repository.RoleRepository;
import com.grt.elogfrieght.services.user.service.RoleService;
import com.grt.elogfrieght.services.user.validator.impl.RoleValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.RoleDTO;
import com.grtship.core.enumeration.DomainStatus;

import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for managing {@link Role}.
 */
@Slf4j
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	private static final String ROLE_NOT_FOUND_FOR_GIVEN_ID = "Role not found for given Id..!!";

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private RoleValidator roleValidator;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.ROLE)
	@Validate(validator = "roleValidator", action = "save")
	public RoleDTO save(RoleDTO roleDTO) throws InvalidDataException {
		log.debug("Request to save Role : {}", roleDTO);
		Role role = roleMapper.toEntity(roleDTO);
		role.setStatus(DomainStatus.PENDING);
		role = roleRepository.save(role);
		return roleMapper.toDto(role);
	}

	@Override
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.ROLE)
	@Validate(validator = "roleValidator", action = "update")
	public RoleDTO update(RoleDTO roleDTO) throws ObjectNotFoundException, InvalidDataException {
		log.debug("Request to save Role : {}", roleDTO);
		Role role = roleMapper.toEntity(roleDTO);
		role = roleRepository.save(role);
		return roleMapper.toDto(role);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.ROLE)
	public void delete(Long id) {
		log.debug("Request to delete Role : {}", id);
		roleRepository.deleteById(id);
	}

	@Override
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.ROLE)
	@Validate(validator = "roleValidator", action = "deactivate")
	public RoleDTO deactivate(DeactivationDTO deactivationDto) {
		Optional<Role> roleById = roleRepository.findById(deactivationDto.getReferenceId());
		if (!roleById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ROLE_NOT_FOUND_FOR_GIVEN_ID);

		Role role = roleById.get();
		Deactivate deactivate = new Deactivate();
		deactivate.setReason(deactivationDto.getDeactivationReason());
		deactivate.setWefDate(deactivationDto.getDeactivationWefDate());
		role.setSubmittedForApproval(Boolean.TRUE);
		role.setDeactivate(deactivate);
		return roleMapper.toDto(roleRepository.save(role));
	}

	@Override
	@Transactional
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.ROLE)
	@Validate(validator = "roleValidator", action = "reactivate")
	public RoleDTO reactivate(ReactivationDTO reactivationDto) {
		Optional<Role> roleById = roleRepository.findById(reactivationDto.getReferenceId());
		if (!roleById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ROLE_NOT_FOUND_FOR_GIVEN_ID);

		Role role = roleById.get();
		Reactivate reactivate = new Reactivate();
		reactivate.setWefDate(reactivationDto.getReactivationWefDate());
		reactivate.setReason(reactivationDto.getReactivationReason());
		role.setSubmittedForApproval(Boolean.TRUE);// Submited For Reactivation Approval..
		role.setReactivate(reactivate);
		return roleMapper.toDto(roleRepository.save(role));
	}

}
