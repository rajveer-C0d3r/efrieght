package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Country;
import com.grtship.mdm.domain.State;

/**
 * Spring Data  repository for the State entity.
 */
@Repository
public interface StateRepository extends JpaRepository<State, Long> {
	
	@Query("SELECT s.id FROM State s where s.country = ?1")
	List<Long> findStateIdsByCountry(Country country);
	
	List<State> findByCountry(Country country);
	
	@Query("SELECT s.name FROM State s where s.id =?1")
	String findNameById(Long id);
	
}
