package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Designation;

/**
 * Spring Data repository for the Designation entity.
 */
@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long>,JpaSpecificationExecutor<Designation> {

	@Query("SELECT COUNT(d) FROM Designation d")
	Long countDesignations();

	Designation findByCode(String code);

	List<Designation> findByCodeAndClientIdAndCompanyId(String code, Long clientId, Long companyId);

	List<Designation> findByNameAndClientIdAndCompanyId(String name, Long clientId, Long companyId);
}
