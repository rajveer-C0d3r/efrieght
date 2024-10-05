package com.grtship.mdm.serviceimpl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.AddressDTO;
import com.grtship.mdm.domain.Address;
import com.grtship.mdm.mapper.AddressMapper;
import com.grtship.mdm.repository.AddressRepository;
import com.grtship.mdm.service.AddressService;

/**
 * Service Implementation for managing {@link Address}.
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

	private final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

	private final AddressRepository addressRepository;

	private final AddressMapper addressMapper;

	public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper) {
		this.addressRepository = addressRepository;
		this.addressMapper = addressMapper;
	}

	@Override
	@Transactional
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.ADDRESS)
	public AddressDTO save(AddressDTO addressDTO) {
		log.debug("Request to save Address : {}", addressDTO);
		Set<String> landMarks = addressDTO.getLandMarkSet();
		String landMarkString = null;
		if (!CollectionUtils.isEmpty(landMarks)) {
			landMarkString = landMarks.stream().map(Object::toString).collect(Collectors.joining(","));
		}
		Address address = addressMapper.toEntity(addressDTO);
		address.setLandMarks(landMarkString);
		address = addressRepository.save(address);
		return addressMapper.toDto(address);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AddressDTO> findAll() {
		log.debug("Request to get all Addresses");
		return addressRepository.findAll().stream().map(addressMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<AddressDTO> findOne(Long id) {
		log.debug("Request to get Address : {}", id);
		return addressRepository.findById(id).map(addressMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.UPDATE,module = com.grtship.core.annotation.Auditable.Module.ADDRESS)
	public void delete(Long id) {
		log.debug("Request to delete Address : {}", id);
		addressRepository.deleteById(id);
	}

	@Override
	public List<Address> findAllById(List<Long> addressIdList) {
		List<Address> addressList = addressRepository.findByIdIn(addressIdList);
		return (!CollectionUtils.isEmpty(addressList)) ? addressList : Collections.emptyList();
	}

	@Override
	public Map<Long, AddressDTO> getAllAddressesByIdList(List<Long> addressIdList) {
		return addressRepository.findByIdIn(addressIdList).stream()
				.filter(address -> address != null && address.getId() != null)
				.collect(Collectors.toMap(Address::getId, addressMapper::toDto));

	}
}
