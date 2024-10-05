package com.grtship.account.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.Invoice;

/**
 * Spring Data repository for the Invoice entity.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

	List<Invoice> findByIdIn(Collection<Long> ids);
}
