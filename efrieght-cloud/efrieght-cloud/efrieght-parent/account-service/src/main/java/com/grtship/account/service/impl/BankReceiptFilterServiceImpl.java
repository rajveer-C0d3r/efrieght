package com.grtship.account.service.impl;

import java.util.List;

import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.criteria.BankReceiptCriteria;
import com.grtship.account.domain.BankReceipt;
import com.grtship.account.domain.BankReceipt_;
import com.grtship.account.domain.InvoiceTransaction;
import com.grtship.account.domain.InvoiceTransaction_;
import com.grtship.account.mapper.BankReceiptResponseMapper;
import com.grtship.account.repository.BankReceiptRepository;
import com.grtship.core.dto.BankReceiptDTO;
import com.grtship.core.dto.BankReceiptResponseDTO;
import com.grtship.core.enumeration.InvoiceTransactionType;
import com.grtship.core.enumeration.PaymentType;
import com.grtship.core.enumeration.TransactionStatus;

@Service
@Transactional(readOnly = true)
public class BankReceiptFilterServiceImpl {

	private final Logger log = LoggerFactory.getLogger(BankReceiptFilterServiceImpl.class);

	@Autowired
	private BankReceiptRepository bankReceiptRepository;

	@Autowired
	private BankReceiptResponseMapper bankReceiptResponseMapper;

	/**
	 * Return a {@link List} of {@link BankReceiptDTO} which matches the criteria
	 * from the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public List<BankReceiptResponseDTO> findByCriteria(BankReceiptCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<BankReceipt> specification = createSpecification(criteria);
		return bankReceiptResponseMapper.toDto(bankReceiptRepository.findAll(specification));
	}

	/**
	 * Return a {@link Page} of {@link BankReceiptDTO} which matches the criteria
	 * from the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @param page     The page, which should be returned.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public Page<BankReceiptResponseDTO> findByCriteria(BankReceiptCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<BankReceipt> specification = createSpecification(criteria);
		Page<BankReceipt> bankReceipts = bankReceiptRepository.findAll(specification, page);
		List<BankReceiptResponseDTO> listOfBDtos = bankReceiptResponseMapper.toDto(bankReceipts.getContent());
		return new PageImpl<>(listOfBDtos, page, bankReceipts.getTotalElements());
	}

	/**
	 * Function to convert {@link BankReceiptCriteria} to a {@link Specification}
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching {@link Specification} of the entity.
	 */
	protected Specification<BankReceipt> createSpecification(BankReceiptCriteria criteria) {
		Specification<BankReceipt> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria)).and(getInvoiceIdSpec(criteria))
					.and(getStatusSpec(criteria)).and(getPaymentTypeSpec(criteria))
					.and(getExternalEntitySpec(criteria));
		}
		return specification;
	}

	private Specification<BankReceipt> getIdSpec(BankReceiptCriteria criteria) {
		if (criteria.getId() != null) {
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(BankReceipt_.id), criteria.getId());
		}
		return Specification.where(null);
	}

	private Specification<BankReceipt> getInvoiceIdSpec(BankReceiptCriteria criteria) {
		if (criteria.getInvoiceId() != null) {
			return (root, query, criteriaBuilder) -> {
				Root<InvoiceTransaction> invoiceTxnRoot = query.from(InvoiceTransaction.class);
				return criteriaBuilder.and(
						criteriaBuilder.equal(invoiceTxnRoot.get(InvoiceTransaction_.invoice), criteria.getInvoiceId()),
						criteriaBuilder.equal(invoiceTxnRoot.get(InvoiceTransaction_.transactionType),
								InvoiceTransactionType.ADJUSTMENT));
			};
		}
		return Specification.where(null);
	}

	private Specification<BankReceipt> getStatusSpec(BankReceiptCriteria criteria) {
		if (criteria.getStatus() != null) {
			for (TransactionStatus status : TransactionStatus.values()) {
				if (status.name().equals(criteria.getStatus())) {
					return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(BankReceipt_.status),
							TransactionStatus.valueOf(criteria.getStatus()));

				}
			}
		}
		return Specification.where(null);
	}

	private Specification<BankReceipt> getPaymentTypeSpec(BankReceiptCriteria criteria) {
		if (criteria.getPaymentType() != null) {
			for (PaymentType type : PaymentType.values()) {
				if (type.name().equals(criteria.getPaymentType())) {
					return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(BankReceipt_.paymentType),
							PaymentType.valueOf(criteria.getPaymentType()));

				}
			}
		}
		return Specification.where(null);
	}

	private Specification<BankReceipt> getExternalEntitySpec(BankReceiptCriteria criteria) {
		if (criteria.getExternalEntityId() != null) {
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(BankReceipt_.externalEntityId),
					criteria.getExternalEntityId());
		}
		return Specification.where(null);
	}
}
