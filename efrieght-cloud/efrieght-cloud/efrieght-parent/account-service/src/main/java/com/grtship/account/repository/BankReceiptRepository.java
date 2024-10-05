package com.grtship.account.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.BankReceipt;
import com.grtship.core.enumeration.TransactionStatus;

/**
 * Spring Data repository for the BankReceipt entity.
 */
@Repository
public interface BankReceiptRepository extends JpaRepository<BankReceipt, Long>, JpaSpecificationExecutor<BankReceipt> {

	@Query("SELECT b.voucherNo from BankReceipt b ORDER BY b.lastModifiedDate DESC")
	List<String> getLastVoucherNo(Pageable pageable);

	List<BankReceipt> findByExternalEntityIdAndStatus(Long externalEntityId, TransactionStatus status);

	List<BankReceipt> findByInvoiceTransactionInvoiceId(Long invoiceId);
}
