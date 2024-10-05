package com.grtship.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.TdsRate;

/**
 * Spring Data repository for the TdsRate entity.
 */
@Repository
public interface TdsRateRepository extends JpaRepository<TdsRate, Long> {
}
