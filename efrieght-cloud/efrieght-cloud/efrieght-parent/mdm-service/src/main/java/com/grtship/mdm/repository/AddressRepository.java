package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Address;

/**
 * Spring Data  repository for the Address entity.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByIdIn(List<Long> addressIdList);
}
