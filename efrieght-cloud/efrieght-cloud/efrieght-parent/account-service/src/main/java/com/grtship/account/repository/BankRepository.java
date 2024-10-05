package com.grtship.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.Bank;

/**
 * Spring Data repository for the Bank entity.
 */
@Repository
public interface BankRepository extends JpaRepository<Bank, Long>, JpaSpecificationExecutor<Bank> {

	List<Bank> findByCodeAndCompanyId(String code, Long companyId);

	List<Bank> findByCodeAndCompanyIdAndIdNot(String code, Long id, Long companyId);

	List<Bank> findByAccountNoAndCompanyId(String accountNo, Long companyId);

	List<Bank> findByAccountNoAndCompanyIdAndIdNot(String accountNo, Long id, Long companyId);
}
