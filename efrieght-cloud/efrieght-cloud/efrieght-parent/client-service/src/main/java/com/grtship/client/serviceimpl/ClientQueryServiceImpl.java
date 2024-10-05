package com.grtship.client.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.client.criteria.ClientCriteria;
import com.grtship.client.domain.Client;
import com.grtship.client.domain.Client_;
import com.grtship.client.mapper.ClientMapper;
import com.grtship.client.repository.ClientRepository;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.enumeration.DomainStatus;

//import io.github.jhipster.service.QueryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ClientQueryServiceImpl /* extends QueryService<Client> */ {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientMapper clientMapper;

	@Transactional(readOnly = true)
	public List<ClientDTO> findByCriteria(ClientCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Client> specification = createSpecification(criteria);
		return clientMapper.toDto(clientRepository.findAll(specification));
	}

	@Transactional(readOnly = true)
	public Page<ClientDTO> findByCriteria(ClientCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Client> specification = createSpecification(criteria);
		Page<Client> clients = clientRepository.findAll(specification, page);
		List<ClientDTO> clientDtos = clientMapper.toDto(clients.getContent());
		return new PageImpl<>(clientDtos, page, clients.getTotalElements());
	}

	@Transactional(readOnly = true)
	public long countByCriteria(ClientCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Client> specification = createSpecification(criteria);
		return clientRepository.count(specification);
	}

	private Specification<Client> createSpecification(ClientCriteria criteria) {
		Specification<Client> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria.getId())).and(getCodeSpec(criteria.getCode()))
					.and(getNameSpec(criteria.getName())).and(getIdsSpec(criteria.getIds()))
					.and(getStatusSpec(criteria.getStatus()))
					.and(getActiveFlagSpec(criteria.getActiveFlag()));
		}
		return specification;
	}

	private Specification<Client> getIdSpec(Long id) {
		return (id != null) ? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Client_.id), id)
				: Specification.where(null);
	}

	private Specification<Client> getCodeSpec(String code) {
		return (!StringUtils.isBlank(code))
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Client_.code), "%" + code + "%")
				: Specification.where(null);
	}

	private Specification<Client> getNameSpec(String name) {
		return (!StringUtils.isBlank(name))
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Client_.name), "%" + name + "%")
				: Specification.where(null);
	}

	private Specification<Client> getStatusSpec(String status) {
		if (status != null) {
			for (DomainStatus domainStaus : DomainStatus.values()) {
				if (domainStaus.name().equals(status)) {
					return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Client_.status),
							DomainStatus.valueOf(status));
				}
			}
		}
		return Specification.where(null);
	}

	private Specification<Client> getIdsSpec(List<Long> ids) {
		return (!CollectionUtils.isEmpty(ids)) ? (root, query, criteriaBuilder) -> root.get("id").in(ids)
				: Specification.where(null);
	}
	
	private Specification<Client> getActiveFlagSpec(Boolean activeFlag) {
		return (activeFlag!=null)
				? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Client_.activeFlag), activeFlag)
				: Specification.where(null);
	}

	@Transactional(readOnly = true)
	public Optional<ClientDTO> findOne(Long id) {
		log.debug("Request to get Client : {}", id);
		ClientCriteria criteria = new ClientCriteria();
		criteria.setId(id);
		List<ClientDTO> clientDtos = findByCriteria(criteria);
		return (!CollectionUtils.isEmpty(clientDtos)) ? Optional.of(clientDtos.get(0)) : Optional.ofNullable(null);
	}

	public List<ClientDTO> getByIds(List<Long> clientIds) {
		if (CollectionUtils.isEmpty(clientIds)) {
			return new ArrayList<>();
		}
		ClientCriteria criteria = new ClientCriteria();
		criteria.setIds(clientIds);
		List<ClientDTO> clientDtos = findByCriteria(criteria);
		return (!CollectionUtils.isEmpty(clientDtos)) ? clientDtos : Collections.emptyList();
	}

}
