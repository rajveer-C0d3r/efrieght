package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Gst;

/**
 * Spring Data  repository for the Gst entity.
 */
@Repository
public interface GstRepository extends JpaRepository<Gst, Long>, JpaSpecificationExecutor<Gst> {

	List<Gst> findByHsnSacCodeAndClientIdAndCompanyIdAndBranchId(String hsnSacCode, Long clientId, Long companyId,
			Long branchId);

	List<Gst> findByHsnSacCodeAndClientIdAndCompanyIdAndBranchIdAndIdNot(String hsnSacCode, Long clientId,
			Long companyId, Long branchId, Long id);
}
