package com.grtship.authorisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.authorisation.domain.Module;

/**
 * Spring Data repository for the Module entity.
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, String>, JpaSpecificationExecutor<Module> {
}
