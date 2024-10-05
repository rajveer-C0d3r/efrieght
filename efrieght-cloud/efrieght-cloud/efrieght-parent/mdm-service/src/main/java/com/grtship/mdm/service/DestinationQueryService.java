package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.mdm.criteria.DestinationCriteria;
import com.grtship.mdm.domain.Destination;

public interface DestinationQueryService {

	List<Destination> findByCriteria(DestinationCriteria criteria);

	Page<DestinationDTO> findByCriteria(DestinationCriteria criteria, Pageable page);

	long countByCriteria(DestinationCriteria criteria);

	Optional<DestinationDTO> findOne(Long id);

	List<DestinationDTO> getCities(Long countryId);

	List<BaseDTO> getPorts(Long cityId);

	List<DestinationDTO> getDestinationsByIdList(List<Long> ids);

	List<DestinationDTO> getDestinationByCountryId(Long id);

	List<DestinationDTO> getDestinationStateId(Long id);
}
