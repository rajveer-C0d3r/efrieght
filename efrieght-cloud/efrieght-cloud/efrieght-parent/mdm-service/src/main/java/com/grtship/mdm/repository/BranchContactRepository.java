package com.grtship.mdm.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.BranchContact;

/**
 * Spring Data  repository for the ContactDetails entity.
 */
@Repository
public interface BranchContactRepository extends JpaRepository<BranchContact, Long> {

	List<BranchContact> findByEntityBranch_IdIn(List<Long> branchIdList);
	
	@Query("select id from BranchContact where entityBranch.id = ?1")
	Set<Long> getIdsByBranchId(Long branchId);

	void deleteByIdIn(Set<Long> savedContactDetailsIds);
}
