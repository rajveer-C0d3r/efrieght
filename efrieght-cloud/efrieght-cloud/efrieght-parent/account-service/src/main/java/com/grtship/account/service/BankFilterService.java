package com.grtship.account.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.criteria.BankCriteria;
import com.grtship.account.domain.Bank;
import com.grtship.account.domain.Bank_;
import com.grtship.account.feignclient.MasterModule;
import com.grtship.account.generic.QueryService;
import com.grtship.account.mapper.BankMapper;
import com.grtship.account.repository.BankRepository;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BankDTO;

@Service
@Transactional(readOnly = true)
public class BankFilterService extends QueryService<Bank> {

	private final Logger log = LoggerFactory.getLogger(BankFilterService.class);

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private BankMapper bankMapper;

	@Autowired
	private MasterModule masterModule;

	/**
	 * Return a {@link List} of {@link BankDTO} which matches the criteria from the
	 * database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public List<BankDTO> findByCriteria(BankCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Bank> specification = createSpecification(criteria);
		List<BankDTO> listOfBank = bankMapper.toDto(bankRepository.findAll(specification));
		prepareAddress(listOfBank);
		return listOfBank;
	}

	/**
	 * Return a {@link Page} of {@link BankDTO} which matches the criteria from the
	 * database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @param page     The page, which should be returned.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public Page<BankDTO> findByCriteria(BankCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Bank> specification = createSpecification(criteria);
		Page<Bank> banks = bankRepository.findAll(specification, page);
		List<BankDTO> listOfBank = bankMapper.toDto(banks.getContent());
		prepareAddress(listOfBank);
		return new PageImpl<>(listOfBank, page, banks.getTotalElements());
	}

	private void prepareAddress(List<BankDTO> listOfBank) {
		List<Long> addressIdList = listOfBank.stream()
				.filter(bank -> bank.getAddress() != null && bank.getAddress().getId() != null)
				.map(bank -> bank.getAddress().getId()).collect(Collectors.toList());
		Map<Long, AddressDTO> addressMap = masterModule.getAllAddresses(addressIdList);
		listOfBank.forEach(bankDto -> bankDto.setAddress(addressMap.get(bankDto.getAddress().getId())));
	}

	/**
	 * Function to convert {@link BankCriteria} to a {@link Specification}
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching {@link Specification} of the entity.
	 */
	protected Specification<Bank> createSpecification(BankCriteria criteria) {
		Specification<Bank> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria.getId())).and(getBankCodeSpec(criteria.getCode()))
					.and(getBankNameSpec(criteria.getName())).and(getBranchNameSpec(criteria.getBranchName()))
					.and(getAccountNoSpec(criteria.getAccountNo()));
		}
		return specification;
	}

	private Specification<Bank> getIdSpec(Long id) {

		return (id != null) ? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Bank_.id), id)
				: Specification.where(null);
	}

	private Specification<Bank> getBankCodeSpec(String code) {

		return (code != null)
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Bank_.code), "%" + code + "%")
				: Specification.where(null);
	}

	private Specification<Bank> getBankNameSpec(String name) {
		return (name != null)
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Bank_.name), "%" + name + "%")
				: Specification.where(null);
	}

	private Specification<Bank> getBranchNameSpec(String branchName) {
		return (branchName != null) ? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Bank_.branchName),
				"%" + branchName + "%") : Specification.where(null);
	}

	private Specification<Bank> getAccountNoSpec(String accountNo) {
		return (accountNo != null)
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Bank_.accountNo),
						"%" + accountNo + "%")
				: Specification.where(null);
	}

}
