package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.CurrencyDTO;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.criteria.CurrencyCriteria;
import com.grtship.mdm.service.CurrencyService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class CurrencyFilterServiceTest {
	
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private CurrencyFilterService currencyFilterService;
	
	@Mock
	private Pageable pageable;
	
	private CurrencyDTO currencyDto;
	private CurrencyCriteria currencyCriteria;

	@BeforeEach
	public void init() {
		currencyDto = CurrencyServiceImplTest.prepareCurrencyDto();
	}

	@Test
	void testFindByCriteriaCurrencyCriteriaCode() {
		CurrencyDTO savedCurrency = currencyService.save(currencyDto);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedCurrency.getCode());
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setCode(stringFilter);
		List<CurrencyDTO> currencies = currencyFilterService.findByCriteria(currencyCriteria);
		assertThat(currencies).isNotEmpty();
		assertThat(currencies).allMatch(currency -> currency.getId()!=null);
		assertThat(currencies).allMatch(currency -> currency.getCode().equals(stringFilter.getEquals()));
		assertThat(currencies).allMatch(currency -> currency.getName() != null);
	}
	
	@Test
	void testFindByCriteriaCurrencyCriteriaCodeForInvalidCode() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setCode(stringFilter);
		List<CurrencyDTO> currencies = currencyFilterService.findByCriteria(currencyCriteria);
		assertThat(currencies).isEmpty();
	}
	
	@Test
	void testFindByCriteriaCurrencyCriteriaName() {
		CurrencyDTO savedCurrency = currencyService.save(currencyDto);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedCurrency.getName());
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setName(stringFilter);
		List<CurrencyDTO> currencies = currencyFilterService.findByCriteria(currencyCriteria);
		assertThat(currencies).isNotEmpty();
		assertThat(currencies).allMatch(currency -> currency.getId() != null);
		assertThat(currencies).allMatch(currency -> currency.getCode() != null);
		assertThat(currencies).allMatch(currency -> currency.getName().equals(stringFilter.getEquals()));
	}
	
	@Test
	void testFindByCriteriaCurrencyCriteriaNameForInvalidName() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setName(stringFilter);
		List<CurrencyDTO> currencies = currencyFilterService.findByCriteria(currencyCriteria);
		assertThat(currencies).isEmpty();
	}
	
	@Test
	void testFindByCriteriaCurrencyCriteriaId() {
		CurrencyDTO savedCurrency = currencyService.save(currencyDto);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(savedCurrency.getId());
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setId(longFilter);
		List<CurrencyDTO> currencies = currencyFilterService.findByCriteria(currencyCriteria);
		assertThat(currencies).isNotEmpty();
		assertThat(currencies).allMatch(currency -> currency.getId().equals(longFilter.getEquals()));
		assertThat(currencies).allMatch(currency -> currency.getCode() != null);
		assertThat(currencies).allMatch(currency -> currency.getName() != null);
	}
	
	@Test
	void testFindByCriteriaCurrencyCriteriaIdForInvalidId() {
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(0L);
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setId(longFilter);
		List<CurrencyDTO> currencies = currencyFilterService.findByCriteria(currencyCriteria);
		assertThat(currencies).isEmpty();
	}

	@Test
	void testFindByCriteriaCurrencyCriteriaPageable() {
		CurrencyDTO savedCurrency = currencyService.save(currencyDto);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedCurrency.getName());
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setName(stringFilter);
		Page<CurrencyDTO> currencies = currencyFilterService.findByCriteria(currencyCriteria, PageRequest.of(0, 20));
		assertThat(currencies).isNotEmpty();
		assertThat(currencies.getNumber()).isEqualTo(0);
		assertThat(currencies.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(currencies.getContent()).allMatch(currency -> currency.getId() != null);
		assertThat(currencies.getContent()).allMatch(currency -> currency.getCode() != null);
		assertThat(currencies.getContent()).allMatch(currency -> currency.getName().equals(stringFilter.getEquals()));
	}

	@Test
	void testCountByCriteria() {
		CurrencyDTO savedCurrency = currencyService.save(currencyDto);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedCurrency.getName());
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setName(stringFilter);
		long count = currencyFilterService.countByCriteria(currencyCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testCountByCriteriaForInvalidCriteria() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		currencyCriteria = new CurrencyCriteria();
		currencyCriteria.setName(stringFilter);
		long count = currencyFilterService.countByCriteria(currencyCriteria);
		assertThat(count).isEqualTo(0);
	}
}
