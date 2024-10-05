package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.CountryDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.criteria.CountryCriteria;
import com.grtship.mdm.service.CountryService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class CountryFilterServiceImplTest {
	
    @Autowired private CountryService countryService;
	@Mock private Pageable pageable;
	@Autowired private EntityManager em;
	@Autowired private CountryFilterServiceImpl countryFilterServiceImpl;
	private CountryDTO countryDto;
	private CountryCriteria countryCriteria;
	
	@BeforeEach
    public void setUp() {
		countryDto=CountryServiceImplTest.createEntity(em);
	}
	
	@Test
	void testFindByCriteriaCountryCriteriaId() {
		CountryDTO savedCountry = countryService.save(countryDto);
		LongFilter filter = new LongFilter();
		filter.setEquals(savedCountry.getId());
		countryCriteria = new CountryCriteria();
		countryCriteria.setId(filter);
		List<CountryDTO> countries = countryFilterServiceImpl.findByCriteria(countryCriteria);
		assertThat(countries).isNotEmpty();
		assertThat(countries).allMatch(countryObj -> countryObj.getId().equals(filter.getEquals()));
		assertThat(countries).allMatch(countryObj -> countryObj.getCode() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getName() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getSectorId() != null);
	}

	@Test
	void testFindByCriteriaCountryCriteriaName() {
		CountryDTO savedCountry = countryService.save(countryDto);
		StringFilter filter = new StringFilter();
		filter.setEquals(savedCountry.getName());
		countryCriteria=new CountryCriteria();
		countryCriteria.setName(filter);
		List<CountryDTO> countries = countryFilterServiceImpl.findByCriteria(countryCriteria);
		assertThat(countries).isNotEmpty();
		assertThat(countries).allMatch(countryObj -> countryObj.getId() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getCode() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getName().equals(filter.getEquals()));
		assertThat(countries).allMatch(countryObj -> countryObj.getSectorId() != null);
	}
	
	@Test
	void testFindByCriteriaCountryCriteriaCode() {
		CountryDTO savedCountry = countryService.save(countryDto);
		StringFilter filter = new StringFilter();
		filter.setEquals(savedCountry.getCode());
		countryCriteria = new CountryCriteria();
		countryCriteria.setCode(filter);
		List<CountryDTO> countries = countryFilterServiceImpl.findByCriteria(countryCriteria);
		assertThat(countries).isNotEmpty();
		assertThat(countries).allMatch(countryObj -> countryObj.getId() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getCode().equals(filter.getEquals()));
		assertThat(countries).allMatch(countryObj -> countryObj.getName() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getSectorId() != null);
	}
	
	@Test
	void testFindByCriteriaCountryCriteriaSectorName() {
		CountryDTO savedCountry = countryService.save(countryDto);
		StringFilter filter = new StringFilter();
		filter.setEquals(savedCountry.getSectorName());
		countryCriteria = new CountryCriteria();
		countryCriteria.setSectorName(filter);
		List<CountryDTO> countries = countryFilterServiceImpl.findByCriteria(countryCriteria);
		assertThat(countries).isNotEmpty();
		assertThat(countries).allMatch(countryObj -> countryObj.getId() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getCode() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getName() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getSectorId() != null);
	}
	
	@Test
	void testFindByCriteriaCountryCriteriaStatus() {
		countryService.save(countryDto);
		countryCriteria = new CountryCriteria();
		countryCriteria.setStatus("PENDING");
		List<CountryDTO> countries = countryFilterServiceImpl.findByCriteria(countryCriteria);
		assertThat(countries).isNotEmpty();
		assertThat(countries).allMatch(countryObj -> countryObj.getId() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getCode() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getName() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getSectorId() != null);
		assertThat(countries)
				.allMatch(countryObj -> countryObj.getStatus().equals(DomainStatus.valueOf(countryCriteria.getStatus())));
	}
	
	@Test
	void testFindByCriteriaCountryCriteriaActiveFlag() {
		CountryDTO savedCountry = countryService.save(countryDto);
		countryCriteria = new CountryCriteria();
		countryCriteria.setActiveFlag(savedCountry.getActiveFlag());
		List<CountryDTO> countries = countryFilterServiceImpl.findByCriteria(countryCriteria);
		assertThat(countries).isNotEmpty();
		assertThat(countries).allMatch(countryObj -> countryObj.getId() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getCode() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getName() != null);
		assertThat(countries).allMatch(countryObj -> countryObj.getSectorId() != null);
		assertThat(countries)
				.allMatch(countryObj -> countryObj.getActiveFlag().equals(countryCriteria.getActiveFlag()));
	}

	@Test
	void testFindByCriteriaCountryCriteriaPageable() {
		CountryDTO savedCountry = countryService.save(countryDto);
		StringFilter filter = new StringFilter();
		filter.setEquals(savedCountry.getName());
		countryCriteria = new CountryCriteria();
		countryCriteria.setName(filter);
		Page<CountryDTO> countries = countryFilterServiceImpl.findByCriteria(countryCriteria, PageRequest.of(0, 20));
		assertThat(countries).isNotEmpty();
		assertThat(countries.getSize()).isEqualTo(20);
		assertThat(countries.getContent()).allMatch(countryObj -> countryObj.getId() != null);
		assertThat(countries.getContent()).allMatch(countryObj -> countryObj.getCode() != null);
		assertThat(countries.getContent()).allMatch(countryObj -> countryObj.getName().equals(filter.getEquals()));
		assertThat(countries.getContent()).allMatch(countryObj -> countryObj.getSectorId() != null);
	}

	@Test
	void testCountByCriteria() {
		CountryDTO savedCountry = countryService.save(countryDto);
		StringFilter filter = new StringFilter();
		filter.setEquals(savedCountry.getName());
		countryCriteria = new CountryCriteria();
		countryCriteria.setName(filter);
		Long count = countryFilterServiceImpl.countByCriteria(countryCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}

}
