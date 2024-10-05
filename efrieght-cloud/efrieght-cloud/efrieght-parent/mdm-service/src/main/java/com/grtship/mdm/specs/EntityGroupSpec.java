package com.grtship.mdm.specs;

import org.springframework.data.jpa.domain.Specification;

import com.grtship.core.dto.EntityGroupCriteriaDTO;
import com.grtship.mdm.domain.EntityGroup;
import com.grtship.mdm.domain.EntityGroup_;

public class EntityGroupSpec {
	private EntityGroupSpec() {}
	
	public static Specification<EntityGroup> getGroupByNameSpec(String name) {
		if(name!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(EntityGroup_.name), "%"+name+"%");
		}
		return null;
	}

	public static Specification<EntityGroup> getGroupByCodeSpec(String code) {
		if(code!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(EntityGroup_.code), "%"+ code+"%");
		}
		return null;
	}

	public static Specification<EntityGroup> getEntityGroupsBySpecs(EntityGroupCriteriaDTO entityBranchCriteria){
		if(entityBranchCriteria!=null) {
			return Specification.where(getGroupByNameSpec(entityBranchCriteria.getName()))
					.and(getGroupByCodeSpec(entityBranchCriteria.getCode()));
		}
		return Specification.where(null);
	}
}
