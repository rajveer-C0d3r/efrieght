package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.AddressDTO;
import com.grtship.mdm.domain.Address;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {

	@Mapping(source = "country.id", target = "countryId")
	@Mapping(source = "country.name", target = "countryName")
	@Mapping(source = "state.id", target = "stateId")
	@Mapping(source = "state.name", target = "stateName")
	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "city.name", target = "cityName")
	@Mapping(source = "fullAddress", target = "address")
	AddressDTO toDto(Address address);

	@Mapping(source = "countryId", target = "country.id")
	@Mapping(source = "countryName", target = "country.name")
	@Mapping(source = "stateId", target = "state.id")
	@Mapping(source = "stateName", target = "state.name")
	@Mapping(source = "cityId", target = "city.id")
	@Mapping(source = "cityName", target = "city.name")
	@Mapping(source = "address", target = "fullAddress")
//	@Mapping(target = "landMarks", ignore = true)
	Address toEntity(AddressDTO addressDTO);

	default Address fromId(Long id) {
		if (id == null) {
			return null;
		}
		Address address = new Address();
		address.setId(id);
		return address;
	}
}
