package com.grtship.client.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.Alias;
import com.grtship.core.dto.AliasDTO;

/**
 * Mapper for the entity {@link Alias} and its DTO {@link AliasDTO}.
 */
@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
@Component
public interface AliasMapper extends EntityMapper<AliasDTO, Alias> {

    @Mapping(source = "alias.id", target = "companyAliasId")
    AliasDTO toDto(Alias alias);

    @Mapping(source = "companyAliasId", target = "companyAlias")
    Alias toEntity(AliasDTO aliasDTO);

    default Alias fromId(Long id) {
        if (id == null) {
            return null;
        }
        Alias alias = new Alias();
        alias.setId(id);
        return alias;
    }
}
