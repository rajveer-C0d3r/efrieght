package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.EntityGroupDTO;
import com.grtship.mdm.domain.EntityGroup;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link EntityGroup} and its DTO {@link EntityGroupDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface EntityGroupMapper extends EntityMapper<EntityGroupDTO, EntityGroup> {



    default EntityGroup fromId(Long id) {
        if (id == null) {
            return null;
        }
        EntityGroup entityGroups = new EntityGroup();
        entityGroups.setId(id);
        return entityGroups;
    }
}
