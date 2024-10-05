package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.CurrencyDTO;
import com.grtship.mdm.service.CurrencyService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class CurrencyServiceImplTest {
	
	@Autowired
	private CurrencyService currencyService;
	
	@Mock
	private Pageable pageable;
	
	private CurrencyDTO currencyDto;
	
	public static CurrencyDTO prepareCurrencyDto() {
		CurrencyDTO currencyDto=new CurrencyDTO();
		currencyDto.setCode("INP");
		currencyDto.setName("Indian paisa");
		return currencyDto;
	}

	@BeforeEach
    public void init() {
		currencyDto = prepareCurrencyDto();
	}

	@Test
	void testSave() {
		CurrencyDTO savedCurrency = currencyService.save(currencyDto);
		assertThat(savedCurrency.getCode()).isEqualTo(currencyDto.getCode());
		assertThat(savedCurrency.getName()).isEqualTo(currencyDto.getName());
	}
	
	
	@Test
	void checkCodeIsRequired() throws Exception {
		currencyDto.setCode(null);
		assertThrows(ConstraintViolationException.class, () ->{
			currencyService.save(currencyDto);
		});
	}
	
	@Test
	void checkCodeIsOfProperLength() throws Exception {
		currencyDto.setCode("ABCHDUN");
		assertThrows(ConstraintViolationException.class, () ->{
			currencyService.save(currencyDto);
		});
	}
	
	@Test
	void checkCodeOnlyContainsAlphabet() throws Exception {
		currencyDto.setCode("AB1");
		assertThrows(ConstraintViolationException.class, () ->{
			currencyService.save(currencyDto);
		});
	}
	
	@Test
	void checkNameIsRequired() throws Exception {
		currencyDto.setName(null);
		assertThrows(ConstraintViolationException.class, () ->{
			currencyService.save(currencyDto);
		});
	}
	
	@Test
	void checkNameIsOfProperLength() throws Exception {
		currencyDto.setName("Indian Paisa Indian Paisa Indian Paisa Indian Paisa Indian Paisa Indian Paisa Indian Paisa");
		assertThrows(ConstraintViolationException.class, () ->{
			currencyService.save(currencyDto);
		});
	}
	
	@Test
	void checkNameMatchesPattern() throws Exception {
		currencyDto.setName("Indain @123 Rupees");
		assertThrows(ConstraintViolationException.class, () ->{
			currencyService.save(currencyDto);
		});
	}
	
	@Test
	void checkCodeIsUnique() throws Exception {
		currencyService.save(currencyDto);
		assertThrows(ValidationException.class, () ->{
			currencyService.save(currencyDto);
		});
	}
	
	@Test
	void checkNameIsUnique() throws Exception {
		currencyService.save(currencyDto);
		currencyDto.setCode("IP");
		assertThrows(ValidationException.class, () -> {
			currencyService.save(currencyDto);
		});
	}
	

	@Test
	void testUpdate() {
		CurrencyDTO savedCurrency = currencyService.save(currencyDto);
		savedCurrency.setCode("IP");
		savedCurrency.setName("Indian Rupay");
		CurrencyDTO updatedCurrency = currencyService.save(savedCurrency);
		assertThat(updatedCurrency.getId()).isNotEqualTo(currencyDto.getId());
		assertThat(updatedCurrency.getCode()).isNotEqualTo(currencyDto.getCode());
		assertThat(updatedCurrency.getName()).isNotEqualTo(currencyDto.getName());
	}

	@Test
	void testFindAll() {
		currencyService.save(currencyDto);
		List<CurrencyDTO> currencies = currencyService.findAll(pageable).getContent();
		assertThat(currencies).asList();
		assertThat(currencies).allMatch(currency -> !currency.getCode().isEmpty());
		assertThat(currencies).allMatch(currency -> !currency.getName().isEmpty());
	}

	@Test
	void testFindOne() {
		CurrencyDTO currencyDTO = currencyService.save(currencyDto);
		CurrencyDTO dto = currencyService.findOne(currencyDTO.getId()).get();
		assertThat(dto.getCode()).isEqualTo(currencyDTO.getCode());
		assertThat(dto.getName()).isEqualTo(currencyDTO.getName());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<CurrencyDTO> dto = currencyService.findOne(0L);
		assertFalse(dto.isPresent());
	}

	@Test
	void testFindCurrencyNameByIdList() {
		CurrencyDTO currencyDTO = currencyService.save(currencyDto);
		Set<Long> ids=new TreeSet<>();
		ids.add(currencyDTO.getId());
		Map<Long, String> currencyNames = currencyService.findCurrencyNameByIdList(ids);
		assertThat(currencyNames).isNotEmpty();
		assertThat(currencyNames.get(currencyDTO.getId())).isNotEmpty();
		assertThat(currencyNames.get(currencyDTO.getId())).isEqualTo(currencyDTO.getName());
	}
	
	@Test
	void testFindCurrencyNameByIdListForInvalidId() {
		Set<Long> ids=new TreeSet<>();
		ids.add(0l);
		Map<Long, String> currencyNames = currencyService.findCurrencyNameByIdList(ids);
		assertThat(currencyNames).isEmpty();
	}

	@Test
	void testDelete() {
		CurrencyDTO currencyDTO = currencyService.save(currencyDto);
		currencyService.delete(currencyDTO.getId());
		Optional<CurrencyDTO> findOne = currencyService.findOne(currencyDTO.getId());
		assertFalse(findOne.isPresent());
	}

	@Test
	void testGetCurrencyIdNameByIds() {
		CurrencyDTO currencyDTO = currencyService.save(currencyDto);
		List<Long> currencyIds = new ArrayList<>();
		currencyIds.add(currencyDTO.getId());
		Map<Long, String> currencyIdNames = currencyService.getCurrencyIdNameByIds(currencyIds);
		assertThat(currencyIdNames).isNotEmpty();
		assertThat(currencyIdNames.get(currencyDTO.getId())).isNotEmpty();
		assertThat(currencyIdNames.get(currencyDTO.getId())).isEqualTo(currencyDTO.getName());
	}
	
	@Test
	void testGetCurrencyIdNameByIdsForInvalidIds() {
		List<Long> currencyIds = new ArrayList<>();
		currencyIds.add(0l);
		Map<Long, String> currencyIdNames = currencyService.getCurrencyIdNameByIds(currencyIds);
		assertThat(currencyIdNames).isEmpty();;
	}

	@Test
	void testGetAllCurrenciesIdList() {
		CurrencyDTO currencyDTO = currencyService.save(currencyDto);
		List<Long> currencyIds = new ArrayList<>();
		currencyIds.add(currencyDTO.getId());
		Map<Long, CurrencyDTO> allCurrencies = currencyService.getAllCurrenciesIdList(currencyIds);
		assertThat(allCurrencies).isNotEmpty();
		assertThat(allCurrencies.get(currencyIds.get(0))).isNotNull();
		assertThat(allCurrencies.get(currencyIds.get(0)).getId()).isNotNull();
		assertThat(allCurrencies.get(currencyIds.get(0)).getCode()).isEqualTo(currencyDTO.getCode());
		assertThat(allCurrencies.get(currencyIds.get(0)).getName()).isEqualTo(currencyDTO.getName());
	}
	
	@Test
	void testGetAllCurrenciesIdListForInvalidIds() {
		List<Long> currencyIds = new ArrayList<>();
		currencyIds.add(0l);
		Map<Long, CurrencyDTO> allCurrencies = currencyService.getAllCurrenciesIdList(currencyIds);
		assertThat(allCurrencies).isEmpty();
	}

}
