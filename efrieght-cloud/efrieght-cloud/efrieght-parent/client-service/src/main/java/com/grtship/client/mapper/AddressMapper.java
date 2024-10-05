package com.grtship.client.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.Address;
import com.grtship.core.dto.AddressDTO;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {
	
    @Mapping(source = "state", target = "stateId")
    @Mapping(source = "city", target = "cityId")
	AddressDTO toDto(Address entity);

    @Mapping(source = "stateId", target = "state")
    @Mapping(source = "cityId", target = "city")
	Address toEntity(AddressDTO dto);
}
