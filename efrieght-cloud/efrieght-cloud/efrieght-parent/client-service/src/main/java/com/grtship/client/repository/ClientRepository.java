package com.grtship.client.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.client.domain.Client;

/**
 * Spring Data  repository for the Client entity.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
	
	List<Client> findByCode(String code);
	
	List<Client> findByName(String name);
	
	Client findByPanNo(String panNo);
	
	Client findBySalesTaxId(String salesTaxId);
	
	Client findByCodeAndIdNot(String code, Long id);

	Client findByNameAndIdNot(String name, Long id);
	
	Client findByPanNoAndIdNot(String panNo, Long id);
	
	Client findBySalesTaxIdAndIdNot(String salesTaxId, Long id);
	
}
