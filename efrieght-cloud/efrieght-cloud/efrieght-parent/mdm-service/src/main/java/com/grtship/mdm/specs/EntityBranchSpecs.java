package com.grtship.mdm.specs;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import com.grtship.core.enumeration.DomainStatus;
import com.grtship.mdm.criteria.EntityBranchCriteria;
import com.grtship.mdm.domain.Address;
import com.grtship.mdm.domain.Address_;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.domain.EntityBranchTax;
import com.grtship.mdm.domain.EntityBranchTax_;
import com.grtship.mdm.domain.EntityBranch_;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.domain.ExternalEntity_;


public class EntityBranchSpecs {

	private EntityBranchSpecs() {}

	public static Specification<EntityBranch> getEntityBranchByIdSpecs(Long id) {
		if(id!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(EntityBranch_.id), id);
		}
		return null;
	}

	public static Specification<EntityBranch> getEntityBranchByNameSpecs(String name) {
		if(name!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(EntityBranch_.name), "%"+name+"%");
		}
		return null;
	}

	public static Specification<EntityBranch> getEntityBranchByCodeSpec(String code) {
		if(code!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(EntityBranch_.code), "%"+code+"%");
		}
		return null;
	}

	public static Specification<EntityBranch> getEntityBranchByCountryNameSpec(String country) {
		if(country!=null) {
			return (root, query, criteriaBuilder) -> {
				Join<EntityBranch, Address> addressjoin = root.join(EntityBranch_.address);
				return criteriaBuilder.like(addressjoin.get(Address_.country).get("name"), "%"+country+"%");
			};
		}
		return null;
	}

	public static Specification<EntityBranch> getEntityBranchByActiveFlagSpec(Boolean activeFlag) {
		if(activeFlag!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(EntityBranch_.activeFlag), activeFlag);
		}
		return null;
	}

	public static Specification<EntityBranch> getEntityBranchByCustomerFlagSpec(Boolean customerFlag) {
		if(customerFlag!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(EntityBranch_.customerFlag), customerFlag);
		}
		return null;
	}

	public static Specification<EntityBranch> getEntityBranchByVendorFlagSpec(Boolean vendorFlag) {
		if(vendorFlag!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(EntityBranch_.vendorFlag), vendorFlag);
		}
		return null;
	}

	public static Specification<EntityBranch> getEntityBranchByEntityIdSpec(Long entityId) {
		if(entityId!=null) {
			return (root, query, criteriaBuilder) -> {
				Join<EntityBranch, ExternalEntity> entityJoin = root.join(EntityBranch_.externalEntity);
				return criteriaBuilder.equal(entityJoin.get(ExternalEntity_.id), entityId);
			};
		}
		return null;
	}
	
	public static Specification<EntityBranch> getEntityBranchByEntityIdsSpec(List<Long> entityIds) {
		if(!CollectionUtils.isEmpty(entityIds)) {
			return (root, query, criteriaBuilder) -> {
				Join<EntityBranch, ExternalEntity> entityJoin = root.join(EntityBranch_.externalEntity);
				return entityJoin.get(ExternalEntity_.id).in(entityIds);
			};
		}
		return null;
	}
	
	public static Specification<EntityBranch> getEntityBranchByGstNoSpecs(String gstNo) {
		if(gstNo!=null) {
			return (root, query, criteriaBuilder) -> {
				Root<EntityBranchTax> branchTaxRoot = query.from(EntityBranchTax.class);
				return criteriaBuilder.and(
						criteriaBuilder.equal(branchTaxRoot.get(EntityBranchTax_.gstNo), gstNo),
						criteriaBuilder.equal(root.get(EntityBranch_.id),branchTaxRoot.get(EntityBranchTax_.entityBranch).get("id"))
						);
			};
		}
		return null;
	}
	
	public static Specification<EntityBranch> getEntityBranchByStatus(String status){
		if(status!=null) {
			String domainStatus = DomainStatus.getName(status);
			if(domainStatus!=null) {
				return (root, query, criteriaBuilder) -> 
				criteriaBuilder.equal(root.get(EntityBranch_.status), DomainStatus.valueOf(domainStatus) );
			}
		 return (root, query, criteriaBuilder) -> 
			criteriaBuilder.isNull(root.get(EntityBranch_.status));
		}
		return null;
	}


	public static Specification<EntityBranch> getEntityBranchBySpecs(EntityBranchCriteria entityBranchCriteria){
		if(entityBranchCriteria!=null) {
			return Specification.where(getEntityBranchByNameSpecs(entityBranchCriteria.getName()))
					.and(getEntityBranchByCodeSpec(entityBranchCriteria.getCode()))
					.and(getEntityBranchByCountryNameSpec(entityBranchCriteria.getCountry()))
					.and(getEntityBranchByActiveFlagSpec(entityBranchCriteria.getActiveFlag()))
					.and(getEntityBranchByCustomerFlagSpec(entityBranchCriteria.getCustomerFlag()))
					.and(getEntityBranchByVendorFlagSpec(entityBranchCriteria.getVendorFlag()))
					.and(getEntityBranchByEntityIdSpec(entityBranchCriteria.getEntiyId()))
					.and(getEntityBranchByIdSpecs(entityBranchCriteria.getId()))
					.and(getEntityBranchByGstNoSpecs(entityBranchCriteria.getGstNo()))
					.and(getEntityBranchByStatus(entityBranchCriteria.getStatus()))
					.and(getEntityBranchByEntityIdsSpec(entityBranchCriteria.getEntityIds()));
		}
		return Specification.where(null);
	}
}
