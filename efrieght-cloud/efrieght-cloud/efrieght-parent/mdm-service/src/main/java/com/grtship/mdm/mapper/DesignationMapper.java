package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.DesignationDTO;
import com.grtship.mdm.domain.Designation;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Designation} and its DTO {@link DesignationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface DesignationMapper extends EntityMapper<DesignationDTO, Designation> {



    default Designation fromId(Long id) {
        if (id == null) {
            return null;
        }
        Designation designation = new Designation();
        designation.setId(id);
        return designation;
    }
}
