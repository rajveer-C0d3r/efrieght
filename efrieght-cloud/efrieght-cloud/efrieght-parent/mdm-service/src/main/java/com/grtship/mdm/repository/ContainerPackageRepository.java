package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.ContainerPackage;

/**
 * Spring Data  repository for the ContainerPackage entity.
 */
@Repository
public interface ContainerPackageRepository extends JpaRepository<ContainerPackage, Long> {
}
