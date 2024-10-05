package com.grtship.mdm.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.EntityBranchTax;

/**
 * Spring Data  repository for the TaxDetails entity.
 */
@Repository
public interface EntityBranchTaxRepository extends JpaRepository<EntityBranchTax, Long> {

	List<EntityBranchTax> findByEntityBranch_IdIn(List<Long> branchIdList);

	@Query("select id from EntityBranchTax where entityBranch.id = ?1")
	Set<Long> findIdsByEntityBranchId(Long branchId);

	void deleteByIdIn(Set<Long> ids);

}
