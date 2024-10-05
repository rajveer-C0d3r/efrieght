package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.SubModule;

/**
 * Spring Data  repository for the SubModule entity.
 */
@Repository
public interface SubModuleRepository extends JpaRepository<SubModule, Long> {

	List<SubModule> findByMainModuleId(Long mainModuleId);
}
