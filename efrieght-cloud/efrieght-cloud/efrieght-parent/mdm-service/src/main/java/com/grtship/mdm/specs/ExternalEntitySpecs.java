package com.grtship.mdm.specs;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.criteria.ExternalEntityCriteria;
import com.grtship.mdm.domain.Address;
import com.grtship.mdm.domain.Address_;
import com.grtship.mdm.domain.EntityBusinessType;
import com.grtship.mdm.domain.EntityBusinessType_;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.domain.ExternalEntity_;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.domain.ObjectAlias_;

public class ExternalEntitySpecs {

	private static final String ID = "id";
	private static final String EXTERNAL_ENTITY2 = "externalEntity";
	private static final String ENTITY_TYPE = "entityType";
	private static final String EXTERNAL_ENTITY = "External Entity";
	private static final String NAME = "name";

	private ExternalEntitySpecs() {}

	public static Specification<ExternalEntity> getExternalEntityByNameSpec(String name) {
		if(name!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(ExternalEntity_.name), "%"+name+"%");
		}
		return null;
	}

	public static Specification<ExternalEntity> getExternalEntityByCodeSpec(String code) {
		if(code!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(ExternalEntity_.code), "%"+code+"%");
		}
		return null;
	}

	public static Specification<ExternalEntity> getExternalEntityByCountryNameSpec(String country) {
		if(country!=null) {
			return (root, query, criteriaBuilder) -> {
				Join<ExternalEntity, Address> addressjoin = root.join(ExternalEntity_.address);
				return criteriaBuilder.like(addressjoin.get(Address_.country).get(NAME), "%"+country+"%");
			};
		}
		return null;
	}

	public static Specification<ExternalEntity> getExternalEntityByAliasNameSpec(String alias) {
		if(alias!=null) {
			return (root, query, criteriaBuilder) -> {
				Root<ObjectAlias> aliasroot = query.from(ObjectAlias.class);
				return criteriaBuilder.and(
						criteriaBuilder.like(aliasroot.get(ObjectAlias_.name), "%"+alias+"%"),
						criteriaBuilder.equal(aliasroot.get(ObjectAlias_.referenceName), EXTERNAL_ENTITY),
						criteriaBuilder.equal(root.get(ExternalEntity_.id),aliasroot.get(ObjectAlias_.referenceId))
						);
			};
		}
		return null;
	}

	public static Specification<ExternalEntity> getExternalEntityByEntityTypeSpec(String entityType) {
		if(entityType!=null) {
			for (EntityType items : EntityType.values()) {
				if(items.toString().equalsIgnoreCase(entityType)) {
					return (root, query, criteriaBuilder) -> {
						Root<EntityBusinessType> enitytype = query.from(EntityBusinessType.class);
						return criteriaBuilder.and(
								criteriaBuilder.equal(enitytype.get(EntityBusinessType_.id).get(ENTITY_TYPE), EntityType.valueOf(entityType.toUpperCase())),
								criteriaBuilder.equal(root.get(ExternalEntity_.id),enitytype.get(EntityBusinessType_.id).get(EXTERNAL_ENTITY2).get(ID))
								);
					};
				}
			}
			return (root, query, criteriaBuilder) -> {
				Root<EntityBusinessType> enitytype = query.from(EntityBusinessType.class);
				return criteriaBuilder.and(
						criteriaBuilder.isNull(enitytype.get(EntityBusinessType_.id).get(ENTITY_TYPE)),
						criteriaBuilder.equal(root.get(ExternalEntity_.id),enitytype.get(EntityBusinessType_.id).get(EXTERNAL_ENTITY2).get(ID))
						);
			};	
		}
		return null;
	}

	public static Specification<ExternalEntity> getExternalEntityByActiveFlagSpec(Boolean activeFlag) {
		if(activeFlag!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ExternalEntity_.activeFlag), activeFlag);
		}
		return null;
	}

	public static Specification<ExternalEntity> getExternalEntityByCustomerFlagSpec(Boolean customerFlag) {
		if(customerFlag!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ExternalEntity_.customerFlag), customerFlag);
		}
		return null;
	}

	public static Specification<ExternalEntity> getExternalEntityByVendorFlagSpec(Boolean vendorFlag) {
		if(vendorFlag!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ExternalEntity_.vendorFlag), vendorFlag);
		}
		return null;
	}
	
	public static Specification<ExternalEntity> getExternalEntityByIdSpec(Long id) {
		if(id!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ExternalEntity_.id), id);
		}
		return null;
	}
	
	public static Specification<ExternalEntity> getExternalEntityByStatus(String status){
		if(status!=null) {
			String domainStatus = DomainStatus.getName(status);
			if(domainStatus!=null) {
				return (root, query, criteriaBuilder) -> 
				criteriaBuilder.equal(root.get(ExternalEntity_.status), DomainStatus.valueOf(domainStatus) );
			}
		 return (root, query, criteriaBuilder) -> 
			criteriaBuilder.isNull(root.get(ExternalEntity_.status));
		}
		return null;
	}
	
	public static Specification<ExternalEntity> getExternalEntityByGroupId(Long id) {
		if(id!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(ExternalEntity_.groups), id);
		}
		return null;
	}


	public static Specification<ExternalEntity> getExternalEntityBySpecs(ExternalEntityCriteria entityCriteria){
		if(entityCriteria!=null) {
			return Specification.where(getExternalEntityByNameSpec(entityCriteria.getName()))
					.and(getExternalEntityByCodeSpec(entityCriteria.getCode()))
					.and(getExternalEntityByCountryNameSpec(entityCriteria.getCountry()))
					.and(getExternalEntityByAliasNameSpec(entityCriteria.getAlias()))
					.and(getExternalEntityByEntityTypeSpec(entityCriteria.getEntityBusinessType()))
					.and(getExternalEntityByActiveFlagSpec(entityCriteria.getActiveFlag()))
					.and(getExternalEntityByCustomerFlagSpec(entityCriteria.getCustomerFlag()))
					.and(getExternalEntityByVendorFlagSpec(entityCriteria.getVendorFlag()))
					.and(getExternalEntityByIdSpec(entityCriteria.getId()))
					.and(getExternalEntityByStatus(entityCriteria.getStatus()))
			        .and(getExternalEntityByGroupId(entityCriteria.getGroupId()));
		}
		return Specification.where(null);
	}

}
