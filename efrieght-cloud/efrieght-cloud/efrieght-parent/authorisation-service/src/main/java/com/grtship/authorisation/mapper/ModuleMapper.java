package com.grtship.authorisation.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.authorisation.interfaces.EntityMapper;
import com.grtship.core.dto.ModuleDTO;
import com.grtship.authorisation.domain.Module;

/**
 * Mapper for the entity {@link Module} and its DTO {@link ModuleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface ModuleMapper extends EntityMapper<ModuleDTO, Module> {

	default Module fromId(String moduleName) {
		if (moduleName == null) {
			return null;
		}
		Module module=new Module();
		module.setModuleName(moduleName);
		return module;
	}
}
