package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.StateDTO;
import com.grtship.mdm.domain.State;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link State} and its DTO {@link StateDTO}.
 */
@Mapper(componentModel = "spring", uses = { CountryMapper.class })
@Component
public interface StateMapper extends EntityMapper<StateDTO, State> {

	@Mapping(source = "country.id", target = "countryId")
	StateDTO toDto(State state);

	@Mapping(source = "countryId", target = "country")
	State toEntity(StateDTO stateDTO);

	default State fromId(Long id) {
		if (id == null) {
			return null;
		}
		State state = new State();
		state.setId(id);
		return state;
	}
}
