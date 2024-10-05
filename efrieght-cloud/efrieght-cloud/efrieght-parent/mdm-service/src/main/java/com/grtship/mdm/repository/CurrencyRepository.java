package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grtship.core.dto.BaseDTO;
import com.grtship.mdm.domain.Currency;

/**
 * Spring Data  repository for the Currency entity.
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long>, JpaSpecificationExecutor<Currency> {

	Currency findByCode(String code);

	Currency findByName(String name);

	@Query("select c from Currency c where c.code = ?1 and c.id != ?2")
	Currency findyByCodeAndId(String code, Long id);

	@Query("select c from Currency c where c.name = ?1 and c.id != ?2")
	Currency findyByNameAndId(String name, Long id); 

	@Query("select new com.grtship.core.dto.BaseDTO(c.id, c.name) from Currency c where c.id in :currencyIdList")
	List<BaseDTO> currencyRepository(@Param("currencyIdList") List<Long> currencyIdList);
}
