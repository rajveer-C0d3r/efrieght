package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.DepartmentDTO;
import com.grtship.mdm.domain.Department;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Department} and its DTO {@link DepartmentDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface DepartmentMapper extends EntityMapper<DepartmentDTO, Department> {
	
	DepartmentDTO toDto(Department role);
	
	Department toEntity(DepartmentDTO roleDTO);

    default Department fromId(Long id) {
        if (id == null) {
            return null;
        }
        Department department = new Department();
        department.setId(id);
        return department;
    }
}
