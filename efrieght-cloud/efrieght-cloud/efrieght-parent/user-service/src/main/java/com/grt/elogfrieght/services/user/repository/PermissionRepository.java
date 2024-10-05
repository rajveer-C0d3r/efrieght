package com.grt.elogfrieght.services.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grt.elogfrieght.services.user.domain.Permission;

/**
 * Spring Data  repository for the Permission entity.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {

	Optional<Permission> findByPermissionCode(String permissionCode);

	List<Permission> findByPermissionCodeIn(List<String> permissionCodes);
}
