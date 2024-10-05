/**
 * 
 */
package com.grtship.audit.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.audit.domain.SystemAuditData;
import com.grtship.core.dto.SystemAuditDataDto;

/**
 * @author hp
 *
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface SystemAuditDataMapper extends EntityMapper<SystemAuditDataDto, SystemAuditData>{

}
