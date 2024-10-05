package com.grtship.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.Tds;

/**
 * Spring Data repository for the Tds entity.
 */
@Repository
public interface TdsRepository extends JpaRepository<Tds, Long>, JpaSpecificationExecutor<Tds> {

	Tds findByCode(String code);

	Tds findByLedgerId(Long ledgerId);

	Tds findByCodeAndIdNot(String code, Long id);

	Tds findByLedgerIdAndIdNot(Long ledgerId, Long id);
}
