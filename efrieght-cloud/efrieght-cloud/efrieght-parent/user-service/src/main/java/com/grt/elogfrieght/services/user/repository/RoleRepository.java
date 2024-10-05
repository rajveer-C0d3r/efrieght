package com.grt.elogfrieght.services.user.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grt.elogfrieght.services.user.domain.Role;

/**
 * Spring Data  repository for the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

	boolean existsByNameAndClientIdAndCompanyId(String name, Long clientId, Long companyId);
	
	boolean existsByNameAndClientId(String name, Long ClientId);

	boolean existsByName(String name);
	
	boolean existsByNameAndIdNot(String name, Long id);
	
	boolean existsByNameAndIsPublicTrue(String name);
	
	boolean existsByNameAndIsPublicTrueAndIdNot(String name, Long id);

	boolean existsByNameAndClientIdAndCompanyIdAndIdNot(String name, Long clientId, Long companyId, Long id);
	
	boolean existsByNameAndClientIdAndIdNot(String name, Long clientId, Long id);

	Optional<Role> findFirstByName(String string);

	boolean existsByNameAndCompanyId(String name, Long companyId);

	boolean existsByNameAndCompanyIdAndIdNot(String name, Long companyId, Long id);
	
}
