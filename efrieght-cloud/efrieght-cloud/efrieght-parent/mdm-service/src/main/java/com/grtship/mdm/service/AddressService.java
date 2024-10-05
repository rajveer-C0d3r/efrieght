package com.grtship.mdm.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.grtship.core.dto.AddressDTO;
import com.grtship.mdm.domain.Address;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Address}.
 */
public interface AddressService {

    /**
     * Save a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    AddressDTO save(AddressDTO addressDTO);

    /**
     * Get all the addresses.
     *
     * @return the list of entities.
     */
    List<AddressDTO> findAll();


    /**
     * Get the "id" address.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AddressDTO> findOne(Long id);

    /**
     * Delete the "id" address.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

	List<Address> findAllById(List<Long> externEntityIdList);
	
	Map<Long, AddressDTO> getAllAddressesByIdList(List<Long> addressIdList);
}
