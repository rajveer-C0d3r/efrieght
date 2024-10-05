package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Department;

/**
 * Spring Data  repository for the Department entity.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

	 @Query("SELECT COUNT(d) FROM Department d")
	 Long countDepartments();
	 
	Department findByCode(String code);

	List<Department> findByCodeAndClientIdAndCompanyId(String code, Long clientId, Long companyId);

	List<Department> findByNameAndClientIdAndCompanyId(String name, Long clientId, Long companyId);

	 
}
