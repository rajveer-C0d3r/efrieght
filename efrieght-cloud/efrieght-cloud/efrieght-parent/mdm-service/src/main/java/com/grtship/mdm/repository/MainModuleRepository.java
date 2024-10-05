package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.MainModule;

/**
 * Spring Data  repository for the MainModule entity.
 */
@Repository
public interface MainModuleRepository extends JpaRepository<MainModule, Long> {

}
