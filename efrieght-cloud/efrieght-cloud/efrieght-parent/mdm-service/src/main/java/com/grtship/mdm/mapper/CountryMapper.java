package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.CountryDTO;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Country} and its DTO {@link CountryDTO}.
 */
@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, SectorMapper.class})
@Component
public interface CountryMapper extends EntityMapper<CountryDTO, Country> {

    @Mapping(source = "currency.id", target = "currencyId")
    @Mapping(source = "currency.name", target = "currencyName")
    @Mapping(source = "sector.id", target = "sectorId")
    @Mapping(source = "sector.name", target = "sectorName")
    CountryDTO toDto(Country country);

    @Mapping(source = "sectorId", target = "sector.id")
    @Mapping(source = "currencyId", target = "currency")
    @Mapping(source = "sectorName", target = "sector.name")
    @Mapping(target = "states", ignore = true)
    @Mapping(target = "removeState", ignore = true)
    Country toEntity(CountryDTO countryDTO);

    default Country fromId(Long id) {
        if (id == null) {
            return null;
        }
        Country country = new Country();
        country.setId(id);
        return country;
    }
}
