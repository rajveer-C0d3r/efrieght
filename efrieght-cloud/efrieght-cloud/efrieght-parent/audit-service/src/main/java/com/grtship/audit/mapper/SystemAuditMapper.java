/**
 * 
 */
package com.grtship.audit.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.audit.domain.SystemAudit;
import com.grtship.core.dto.SystemAuditDTO;

/**
 * @author hp
 *
 */
@Mapper(componentModel = "spring", uses = { SystemAuditDataMapper.class })
@Component
public interface SystemAuditMapper extends EntityMapper<SystemAuditDTO, SystemAudit> {

	public abstract SystemAuditDTO toDto(SystemAudit systemAudit);

	public abstract SystemAudit toEntity(SystemAuditDTO systemAuditDto);

	public abstract List<SystemAuditDTO> toDto(List<SystemAudit> systemAudits);

	public abstract List<SystemAudit> toEntity(List<SystemAuditDTO> systemAuditDTOs);
}
