package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.SubModuleDTO;
import com.grtship.mdm.domain.SubModule;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link SubModule} and its DTO {@link SubModuleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface SubModuleMapper extends EntityMapper<SubModuleDTO, SubModule> {

	default SubModule fromId(Long id) {
		if (id == null) {
			return null;
		}
		SubModule subModule = new SubModule();
		subModule.setId(id);
		return subModule;
	}
}
