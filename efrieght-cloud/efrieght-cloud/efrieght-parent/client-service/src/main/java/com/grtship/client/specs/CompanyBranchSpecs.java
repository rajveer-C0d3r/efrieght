package com.grtship.client.specs;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import com.grtship.client.criteria.CompanyBranchCriteria;
import com.grtship.client.domain.Address_;
import com.grtship.client.domain.Company;
import com.grtship.client.domain.CompanyBranch;
import com.grtship.client.domain.CompanyBranch_;
import com.grtship.client.domain.Company_;
import com.grtship.core.enumeration.DomainStatus;

public class CompanyBranchSpecs {
	private CompanyBranchSpecs() {}

	public static Specification<CompanyBranch> getCompanyBranchByIdSpecs(Long id) {
		if(id!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(CompanyBranch_.id), id);
		}
		return null;
	}

	public static Specification<CompanyBranch> getCompanyBranchByNameSpecs(String name) {
		if(name!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(CompanyBranch_.name), "%"+name+"%");
		}
		return null;
	}

	public static Specification<CompanyBranch> getCompanyBranchByCodeSpec(String code) {
		if(code!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(CompanyBranch_.code), "%"+code+"%");
		}
		return null;
	}

	public static Specification<CompanyBranch> getCompanyBranchByActiveFlag(Boolean activeFlag){
		if(activeFlag!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(CompanyBranch_.activeFlag), activeFlag);
		}
		return null;
	}

	public static Specification<CompanyBranch> getCompanyBranchBySubmitedForApproval(Boolean submitForApproval){
		if(submitForApproval!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(CompanyBranch_.submittedForApproval), submitForApproval);
		}
		return null;
	}

	public static Specification<CompanyBranch> getCompanyBranchByStatus(String status){
		if(status!=null) {
			String domainStatus = DomainStatus.getName(status);
			if(domainStatus!=null) {
				return (root, query, criteriaBuilder) -> 
				criteriaBuilder.equal(root.get(CompanyBranch_.status), DomainStatus.valueOf(domainStatus) );
			}
		 return (root, query, criteriaBuilder) -> 
			criteriaBuilder.isNull(root.get(CompanyBranch_.status));
		}
		return null;
	}

	public static Specification<CompanyBranch> getCompanyBranchByLocation(String location){
		if(location!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.like(root.get(CompanyBranch_.address).get(Address_.location), "%"+location+"%");
		}
		return null;
	}

	public static Specification<CompanyBranch> getCompanyBranchByCityId(Long cityId){
		if(cityId!=null) {
			return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get(CompanyBranch_.address).get(Address_.city), cityId);
		}
		return null;
	}
	
	public static Specification<CompanyBranch> getCompanyBranchByIdsSpecs(List<Long> ids) {
		if(!CollectionUtils.isEmpty(ids)) {
			return (root, query, criteriaBuilder) -> 
			root.get("id").in(ids);
		}
		return null;
	}
	
	public static Specification<CompanyBranch> getCompanyBranchByComIdsSpecs(List<Long> companyIds) {
		if(!CollectionUtils.isEmpty(companyIds)) {
			return (root, query, criteriaBuilder) -> {
				Root<Company> companyRoot = query.from(Company.class);
				return companyRoot.get("id").in(companyIds);
			};
		}
		return null;
	}
	public static Specification<CompanyBranch> getCompanyBranchByClientIdsSpecs(List<Long> clientIds) {
		if(!CollectionUtils.isEmpty(clientIds)) {
			return (root, query, criteriaBuilder) -> 
			root.get("clientId").in(clientIds);
		}
		return null;
	}
	
	public static Specification<CompanyBranch> getCompanyBranchByCompanyIdSpecs(Long companyId){
		if(companyId!=null) {
			return (root, query, criteriaBuilder) -> {
				Join<CompanyBranch, Company> companyJoin = root.join(CompanyBranch_.company);
				return criteriaBuilder.equal(companyJoin.get(Company_.id), companyId);
			};
		}
		return null;
	}


	public static Specification<CompanyBranch> getCompanyBranchBySpecs(CompanyBranchCriteria branchCriteria){
		if(branchCriteria!=null) {
			return Specification.where(getCompanyBranchByNameSpecs(branchCriteria.getName()))
					.and(getCompanyBranchByCodeSpec(branchCriteria.getCode()))
					.and(getCompanyBranchByIdSpecs(branchCriteria.getId()))
					.and(getCompanyBranchByActiveFlag(branchCriteria.getActiveFlag()))
					.and(getCompanyBranchBySubmitedForApproval(branchCriteria.getSubmitForApproval()))
					.and(getCompanyBranchByStatus(branchCriteria.getStatus()))
					.and(getCompanyBranchByLocation(branchCriteria.getLocation()))
					.and(getCompanyBranchByCityId(branchCriteria.getCityId()))
					.and(getCompanyBranchByIdsSpecs(branchCriteria.getIds()))
					.and(getCompanyBranchByCompanyIdSpecs(branchCriteria.getCompanyId()))
					.and(getCompanyBranchByComIdsSpecs(branchCriteria.getCompanyIds()))
					.and(getCompanyBranchByClientIdsSpecs(branchCriteria.getClientIds()));
		}
		return Specification.where(null);
	}
}
