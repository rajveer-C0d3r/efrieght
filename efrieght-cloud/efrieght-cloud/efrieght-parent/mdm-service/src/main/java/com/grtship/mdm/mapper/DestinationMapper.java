package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.DestinationDTO;
import com.grtship.mdm.domain.Destination;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Destination} and its DTO {@link DestinationDTO}.
 */
@Mapper(componentModel = "spring", uses = {CountryMapper.class, StateMapper.class, ObjectAliasMapper.class})
@Component
public interface DestinationMapper extends EntityMapper<DestinationDTO, Destination> {

    @Mapping(source = "port.id", target = "portId")
    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "state.id", target = "stateId")
    @Mapping(source = "port.name", target = "portName")
    @Mapping(source = "city.name", target = "cityName")
    @Mapping(source = "country.name", target = "countryName")
    @Mapping(source = "country.sector.name", target = "sectorName")
    @Mapping(source = "state.name", target = "stateName")
    DestinationDTO toDto(Destination destination);

    @Mapping(source = "portId", target = "port.id")
    @Mapping(source = "cityId", target = "city.id")
    @Mapping(source = "countryId", target = "country.id")
    @Mapping(source = "stateId", target = "state.id")
    Destination toEntity(DestinationDTO destinationDTO);

    default Destination fromId(Long id) {
        if (id == null) {
            return null;
        }
        Destination destination = new Destination();
        destination.setId(id);
        return destination;
    }
}
