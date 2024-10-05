package com.grtship.mdm.serviceimpl;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.mdm.criteria.DestinationCriteria;
import com.grtship.mdm.domain.Country_;
import com.grtship.mdm.domain.Destination;
import com.grtship.mdm.domain.Destination_;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.domain.ObjectAlias_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.DestinationMapper;
import com.grtship.mdm.repository.DestinationRepository;
import com.grtship.mdm.service.DestinationQueryService;
import com.grtship.mdm.service.ObjectAliasQueryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DestinationQueryServiceImpl extends QueryService<Destination> implements DestinationQueryService {

	private static final String SECTOR_NAME = "sectorName";

	private static final String PORT = "PORT";

	private static final String CITY = "CITY";

	@Autowired
	private DestinationRepository destinationRepository;

	@Autowired
	private DestinationMapper destinationMapper;

	@Autowired
	private ObjectAliasQueryService aliasQueryService;

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Transactional(readOnly = true)
	public List<Destination> findByCriteria(DestinationCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Destination> specification = createSpecification(criteria);
		List<DestinationDTO> destinationDtos = destinationRepository.findAll(specification).stream()
				.map(destinationMapper::toDto).collect(Collectors.toList());
		prepareAlias(destinationDtos);
		return destinationDtos.stream().map(destinationMapper::toEntity).collect(Collectors.toList());
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Transactional(readOnly = true)
	public Page<DestinationDTO> findByCriteria(DestinationCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Destination> specification = createSpecification(criteria);
		Page<Destination> destinations = (page.getSort().getOrderFor(SECTOR_NAME) != null)
				? destinationRepository.findAll(specification, PageRequest.of(page.getPageNumber(), page.getPageSize()))
				: destinationRepository.findAll(specification, page);
		List<DestinationDTO> destinationDtos = destinationMapper.toDto(destinations.getContent());
		prepareAlias(destinationDtos);
		if (page.getSort().getOrderFor(SECTOR_NAME) != null && page.getSort().getOrderFor(SECTOR_NAME).isAscending())
			return new PageImpl<>(destinationDtos.stream().sorted(Comparator.comparing(DestinationDTO::getSectorName))
					.collect(Collectors.toList()), page, destinations.getTotalElements());
		else if (page.getSort().getOrderFor(SECTOR_NAME) != null
				&& page.getSort().getOrderFor(SECTOR_NAME).isDescending())
			return new PageImpl<>(
					destinationDtos.stream().sorted(Comparator.comparing(DestinationDTO::getSectorName).reversed())
							.collect(Collectors.toList()),
					page, destinations.getTotalElements());
		return new PageImpl<>(destinationDtos, page, destinations.getTotalElements());
	}

	private void prepareAlias(List<DestinationDTO> destinationDtos) {
		List<Long> destinationIds = destinationDtos.stream().filter(destinationDto -> destinationDto.getId() != null)
				.map(DestinationDTO::getId).collect(Collectors.toList());
		List<ObjectAliasDTO> aliasList = aliasQueryService
				.getListOfAliasByReferanceIdListAndReferenceName(destinationIds, ReferenceNameConstant.DESTINATION);
		destinationDtos.forEach(destinationDto -> {
			Set<ObjectAliasDTO> aliasSet = aliasList.stream()
					.filter(obj -> obj.getReferenceId().equals(destinationDto.getId())).collect(Collectors.toSet());
			destinationDto.setAliases(aliasSet);
		});
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Transactional(readOnly = true)
	public long countByCriteria(DestinationCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Destination> specification = createSpecification(criteria);
		return destinationRepository.count(specification);
	}

	protected Specification<Destination> createSpecification(DestinationCriteria criteria) {
		Specification<Destination> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getDestinationByIdSpec(criteria.getId()))
					.and(getDestinationByCodeSpec(criteria.getCode())).and(getDestinationByNameSpec(criteria.getName()))
					.and(getDestinationByPortIdSpec(criteria.getPortId()))
					.and(getDestinationBySectorSpec(criteria.getSector()))
					.and(getDestinationByCountrySpec(criteria.getCountry()))
					.and(getDestinationByCountryIdSpec(criteria.getCountryId()))
					.and(getDestinationByIdsSpec(criteria.getIds()))
					.and(getDestinationByStateIdSpec(criteria.getStateId()))
					.and(getDestinationByCityIdSpec(criteria.getCityId()))
					.and(getDestinationByReworkingSpec(criteria.getIsReworkingPort()))
					.and(getDestinationByDestinationTypeSpec(criteria.getType()))
					.and(getDestinationByAliasSpec(criteria.getAlias()));
		}

		return specification;
	}

	private Specification<Destination> getDestinationByIdSpec(Long id) {
		if (id != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Destination_.id), id);
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByCodeSpec(String code) {
		if (code != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Destination_.code),
					"%" + code + "%");
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByNameSpec(String name) {
		if (name != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Destination_.name),
					"%" + name + "%");
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByPortIdSpec(Long portId) {
		if (portId != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.and(
					criteriaBuilder.equal(root.get(Destination_.port), portId),
					criteriaBuilder.equal(root.get(Destination_.type), DestinationType.TERMINAL));
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationBySectorSpec(String sector) {
		if (sector != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder
					.like(root.join(Destination_.country).get("sector").get("name"), "%" + sector + "%");
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByCountrySpec(String country) {
		if (country != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder
					.like(root.get(Destination_.country).get(Country_.name), "%" + country + "%");
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByCountryIdSpec(Long countryId) {
		if (countryId != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Destination_.country), countryId);
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByIdsSpec(List<Long> ids) {
		if (!CollectionUtils.isEmpty(ids))
			return (root, query, criteriaBuilder) -> root.get("id").in(ids);
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByStateIdSpec(Long stateId) {
		if (stateId != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Destination_.state), stateId);
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByCityIdSpec(Long cityId) {
		if (cityId != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Destination_.city), cityId);
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByReworkingSpec(Boolean isReworkingPort) {
		if (isReworkingPort != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Destination_.isReworkingPort),
					isReworkingPort);
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByDestinationTypeSpec(String type) {
		if (type != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Destination_.type),
					DestinationType.valueOf(type));
		return Specification.where(null);
	}

	private Specification<Destination> getDestinationByAliasSpec(String alias) {
		if (alias != null)
			return (root, query, criteriaBuilder) -> {
				Root<ObjectAlias> aliasRoot = query.from(ObjectAlias.class);
				return criteriaBuilder.and(criteriaBuilder.equal(aliasRoot.get(ObjectAlias_.name), alias),
						criteriaBuilder.equal(aliasRoot.get(ObjectAlias_.referenceName),
								ReferenceNameConstant.DESTINATION),
						criteriaBuilder.equal(aliasRoot.get(ObjectAlias_.referenceId), root.get(Destination_.id)));
			};
		return Specification.where(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<DestinationDTO> findOne(Long id) {
		log.debug("Request to get Destination : {}", id);
		Optional<DestinationDTO> destinationDTO = destinationRepository.findById(id).map(destinationMapper::toDto);
		destinationDTO.ifPresent(dto -> dto.setAliases(new HashSet<>(
				aliasQueryService.getListOfAliasByReferanceIdAndReferenceName(id, ReferenceNameConstant.DESTINATION))));
		return (destinationDTO.isPresent()) ? Optional.of(destinationDTO.get()) : Optional.ofNullable(null);
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Override
	public List<DestinationDTO> getCities(Long countryId) {
		log.debug("Request to get cities ");
		DestinationCriteria criteria = new DestinationCriteria();
		criteria.setType(CITY);
		criteria.setCountryId(countryId);
		return destinationMapper.toDto(findByCriteria(criteria));
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Override
	public List<BaseDTO> getPorts(Long cityId) {
		log.debug("Request to get ports ");
		DestinationCriteria criteria = new DestinationCriteria();
		criteria.setType(PORT);
		criteria.setCityId(cityId);
		return findByCriteria(criteria).stream().map(port -> new BaseDTO(port.getId(), port.getName()))
				.collect(Collectors.toList());
	}

	@AccessFilter(allowAdminData = true,clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Override
	public List<DestinationDTO> getDestinationsByIdList(List<Long> ids) {
		log.debug("Request to get destinations by ids {}", ids);
		DestinationCriteria criteria = new DestinationCriteria();
		criteria.setIds(ids);
		return findByCriteria(criteria).stream().map(destinationMapper::toDto).collect(Collectors.toList());
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Override
	public List<DestinationDTO> getDestinationByCountryId(Long id) {
		log.debug("Request to get All Destination by Country id : {}", id);
		DestinationCriteria criteria = new DestinationCriteria();
		criteria.setCountryId(id);
		return findByCriteria(criteria).stream().map(destinationMapper::toDto).collect(Collectors.toList());
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Override
	public List<DestinationDTO> getDestinationStateId(Long id) {
		log.debug("Request to get All Destination by State id: {}", id);
		DestinationCriteria criteria = new DestinationCriteria();
		criteria.setStateId(id);
		return findByCriteria(criteria).stream().map(destinationMapper::toDto).collect(Collectors.toList());
	}

}
