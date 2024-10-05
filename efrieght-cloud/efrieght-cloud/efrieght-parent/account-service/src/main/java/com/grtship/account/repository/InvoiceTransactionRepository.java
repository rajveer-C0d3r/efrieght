package com.grtship.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.InvoiceTransaction;

/**
 * Spring Data repository for the InvoiceTransaction entity.
 */
@Repository
public interface InvoiceTransactionRepository extends JpaRepository<InvoiceTransaction, Long> {
}
