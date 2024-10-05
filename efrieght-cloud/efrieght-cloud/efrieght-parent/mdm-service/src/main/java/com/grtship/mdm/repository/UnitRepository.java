package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Unit;

/**
 * Spring Data  repository for the Unit entity.
 */
@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
}
