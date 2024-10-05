package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.MainModuleDTO;
import com.grtship.mdm.domain.MainModule;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link MainModule} and its DTO {@link MainModuleDTO}.
 */
@Mapper(componentModel = "spring", uses = { SubModuleMapper.class })
@Component
public interface MainModuleMapper extends EntityMapper<MainModuleDTO, MainModule> {

	default MainModule fromId(Long id) {
		if (id == null) {
			return null;
		}
		MainModule mainModule = new MainModule();
		mainModule.setId(id);
		return mainModule;
	}
}
