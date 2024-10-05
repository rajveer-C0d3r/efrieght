package com.grtship.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.Ledger;

/**
 * Spring Data repository for the Ledger entity.
 * 
 * 
 */
@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long>, JpaSpecificationExecutor<Ledger> {
}
