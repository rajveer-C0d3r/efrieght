package com.grtship.mdm.specs;

import org.springframework.data.jpa.domain.Specification;

import com.grtship.mdm.criteria.ObjectAliasCriteria;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.domain.ObjectAlias_;

public class ObjectAliasSpecs {
	
	private ObjectAliasSpecs() {}
	
	public static Specification<ObjectAlias> getObjectAliasByIdSpec(Long id) {
		if(id!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ObjectAlias_.id), id);
		}
		return null;
	}
	
	public static Specification<ObjectAlias> getObjectAliasByReferenceNameSpec(String referenceName) {
		if(referenceName!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ObjectAlias_.referenceName), referenceName);
		}
		return null;
	}
	
	public static Specification<ObjectAlias> getObjectAliasByReferenceIdSpec(Long referenceId) {
		if(referenceId!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ObjectAlias_.referenceId), referenceId);
		}
		return null;
	}

	public static Specification<ObjectAlias> getAliasBySpecs(ObjectAliasCriteria aliasCriteria){
		if(aliasCriteria!=null) {
			return Specification.where(getObjectAliasByIdSpec(aliasCriteria.getId()))
					.and(getObjectAliasByReferenceNameSpec(aliasCriteria.getReferenceName()))
					.and(getObjectAliasByReferenceIdSpec(aliasCriteria.getReferenceId()));
		}
		return Specification.where(null);
	}
	
	public static Specification<ObjectAlias> getAliasByReferenceIdAndReferenceName(ObjectAliasCriteria aliasCriteria){
		if(aliasCriteria!=null) {
			return Specification.where(getObjectAliasByReferenceIdSpec(aliasCriteria.getReferenceId()))
					.and(getObjectAliasByReferenceNameSpec(aliasCriteria.getReferenceName()));
		}
		return Specification.where(null);
	}
}
