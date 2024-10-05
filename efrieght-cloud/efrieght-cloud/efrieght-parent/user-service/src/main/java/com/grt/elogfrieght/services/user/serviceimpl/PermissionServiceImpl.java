package com.grt.elogfrieght.services.user.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.Permission;
import com.grt.elogfrieght.services.user.mapper.PermissionMapper;
import com.grt.elogfrieght.services.user.repository.PermissionRepository;
import com.grt.elogfrieght.services.user.service.PermissionService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.PermissionDTO;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Service Implementation for managing {@link Permission}.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

	private final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

	@NonNull
	private final PermissionRepository permissionRepository;
	@NonNull
	private final PermissionMapper permissionMapper;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.PERMISSION)
	public PermissionDTO save(PermissionDTO permissionDTO) {
		log.debug("Request to save Permission : {}", permissionDTO);
		Permission permission = permissionMapper.toEntity(permissionDTO);
		permission = permissionRepository.save(permission);
		return permissionMapper.toDto(permission);
	}

	@Override
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.PERMISSION)
	public PermissionDTO update(PermissionDTO permissionDTO) throws InvalidDataException {
		log.debug("Request to update Permission : {}", permissionDTO);
		Permission permission = permissionMapper.toEntity(permissionDTO);
		if (!permissionRepository.findByPermissionCode(permissionDTO.getPermissionCode()).isPresent()) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, "Permission code does not exist");
		}
		permission = permissionRepository.save(permission);
		return permissionMapper.toDto(permission);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.PERMISSION)
	public void delete(String permissionCode) {
		log.debug("Request to delete Permission : {}", permissionCode);
		permissionRepository.deleteById(permissionCode);
	}
}
