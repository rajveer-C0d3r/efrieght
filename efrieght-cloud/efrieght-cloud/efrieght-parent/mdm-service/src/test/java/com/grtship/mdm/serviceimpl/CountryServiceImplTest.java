package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

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

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.KeyLabelBeanDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.StateDTO;
import com.grtship.core.enumeration.DocumentType;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.mdm.criteria.CountryCriteria;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.domain.Sector;
import com.grtship.mdm.service.CountryService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class CountryServiceImplTest {
	
    @Autowired private CountryService countryService;
	@Mock private Pageable pageable;
	@Autowired private EntityManager em;
	private CountryDTO countryDto;
	private CountryCriteria countryCriteria;
	
	public static CountryDTO createEntity(EntityManager em) {
		Sector sector = new Sector();
		sector.setCode("INR");
		sector.setName("Indian Rupees");
		em.persist(sector);
		
		Currency currency = new Currency();
		currency.setCode("INR");
		currency.setName("Indian Rupee");
		em.persist(currency);
		
		Set<StateDTO> states = new HashSet<>();
		StateDTO state1 = new StateDTO();
		state1.setCode("T1");
		state1.setName("Test Save 1");
		states.add(state1);
		StateDTO state2 = new StateDTO();
		state2.setCode("T2");
		state2.setName("Test Save 2");
		states.add(state2);
		
		Set<DocumentDTO> documents = new HashSet<>();
		DocumentDTO document1 = new DocumentDTO();
		document1.setName("Aadhar Card");
		document1.setIsMandatory(false);
		document1.setType(DocumentType.OPERATIONAL);
		documents.add(document1);
		DocumentDTO document2 = new DocumentDTO();
		document2.setName("PAN Card");
		document2.setIsMandatory(false);
		document2.setType(DocumentType.OPERATIONAL);
		documents.add(document2);
		
		Set<ObjectAliasDTO> aliases=new HashSet<>();
		aliases.add(ObjectAliasDTO.builder().name("HINDUSTAN").build());
		aliases.add(ObjectAliasDTO.builder().name("BHARAT").build());
		
		CountryDTO countryDto = new CountryDTO();
		countryDto.setCode("INA");
		countryDto.setName("Indiana");
		countryDto.setGstOrVatType(GstVatType.GST);
		countryDto.setIsStateMandatory(true);
		countryDto.setCurrencyId(currency.getId());
		countryDto.setSectorId(sector.getId());
		countryDto.setAliases(aliases);
		countryDto.setStates(states);
		countryDto.setDocuments(documents);
		return countryDto;
	}
	
	@BeforeEach
    public void setUp() {
		countryDto=createEntity(em);
	}

	@Test
	void testSave() {
		CountryDTO savedCountry = countryService.save(countryDto);
		assertThat(countryDto.getCode()).isEqualTo(savedCountry.getCode());
		assertThat(countryDto.getName()).isEqualTo(savedCountry.getName());
		assertThat(countryDto.getGstOrVatType()).isEqualTo(savedCountry.getGstOrVatType());
		assertThat(countryDto.getIsStateMandatory()).isEqualTo(savedCountry.getIsStateMandatory());
		assertThat(countryDto.getCurrencyId()).isEqualTo(savedCountry.getCurrencyId());
		assertThat(countryDto.getSectorId()).isEqualTo(savedCountry.getSectorId());
	}
	
	@Test
	public void checkCodeIsRequired() {
		countryDto.setCode(null);
		assertThrows(ConstraintViolationException.class, () ->{
			countryService.save(countryDto);
		});
	}
	
	@Test
	public void checkIfCodeIsUnique() {
		countryService.save(countryDto);
		assertThrows(ValidationException.class,() -> {
			countryService.save(countryDto);
		});
	}
	
	@Test
	public void checkNameIsRequired() {
		countryDto.setName(null);
		assertThrows(ConstraintViolationException.class, () ->{
			countryService.save(countryDto);
		});
	}
	
	@Test
	public void checkIfNameIsUnique() {
		countryService.save(countryDto);
		countryDto.setCode("INC");
		assertThrows(ValidationException.class, () -> {
			countryService.save(countryDto);
		});
	}
	
	@Test
	public void checkGstVatTypeIsRequired() {
		countryDto.setGstOrVatType(null);
		assertThrows(ConstraintViolationException.class, () ->{
			countryService.save(countryDto);
		});
	}
	
	@Test
	public void checkCurrencyIsRequired() {
		countryDto.setCurrencyId(null);
		assertThrows(ConstraintViolationException.class, () ->{
			countryService.save(countryDto);
		});
	}
	
	@Test
	public void checkSectorIsRequired() {
		countryDto.setSectorId(null);
		assertThrows(ConstraintViolationException.class, () ->{
			countryService.save(countryDto);
		});
	}
	
	@Test
	public void checkStateIsRequired() {
		countryDto.setStates(null);
		assertThrows(InvalidDataException.class, () ->{
			countryService.save(countryDto);
		});
	}

	@Test
	void testFindAll() {
		countryService.save(countryDto);
		countryCriteria = new CountryCriteria();
		Page<CountryDTO> countries = countryService.findAll(countryCriteria, PageRequest.of(0, 20));
		assertThat(countries.getSize()).isEqualTo(20);
		assertThat(countries.getContent()).allMatch(countryObj -> countryObj.getCode() != null);
		assertThat(countries.getContent()).allMatch(countryObj -> countryObj.getName() != null);
		assertThat(countries.getContent()).allMatch(countryObj -> countryObj.getSectorId() != null);
	}

	@Test
	void testFindOne() {
		CountryDTO savedCountry = countryService.save(countryDto);
		CountryDTO getCountryById = countryService.findOne(savedCountry.getId()).get();
		assertThat(getCountryById).isNotNull();
		assertThat(savedCountry.getCode()).isEqualTo(getCountryById.getCode());
		assertThat(savedCountry.getName()).isEqualTo(getCountryById.getName());
		assertThat(savedCountry.getGstOrVatType()).isEqualTo(getCountryById.getGstOrVatType());
		assertThat(savedCountry.getIsStateMandatory()).isEqualTo(getCountryById.getIsStateMandatory());
		assertThat(savedCountry.getCurrencyId()).isEqualTo(getCountryById.getCurrencyId());
		assertThat(savedCountry.getSectorId()).isEqualTo(getCountryById.getSectorId());
	}
	
	@Test
	public void testGetNoCountryForInvalidId() {
		assertFalse(countryService.findOne(0L).isPresent());
	}

	@Test
	void testGetCountryNameById() {
		CountryDTO savedCountry = countryService.save(countryDto);
		String name = countryService.getCountryNameById(savedCountry.getId());
		assertThat(name).isNotNull();
		assertThat(savedCountry.getName()).isEqualTo(name);
	}
	
	@Test
	public void testCountryNameByInvalidId() {
		assertThat(countryService.getCountryNameById(0L)).isNull();
	}

	@Test
	void testGetCountriesByIdList() {
		CountryDTO savedCountry = countryService.save(countryDto);
		List<CountryDTO> countryList = countryService
				.getCountriesByIdList(new HashSet<>(Arrays.asList(savedCountry.getId())));
		assertThat(countryList).isNotEmpty().hasSize(1);
	}

	@Test
	void testGetSectorIdByCountryId() {
		CountryDTO savedCountry = countryService.save(countryDto);
		Long sectorId = countryService.getSectorIdByCountryId(savedCountry.getId());
		assertThat(sectorId).isNotNull();
		assertThat(countryDto.getSectorId()).isEqualTo(sectorId);
	}
	
	@Test
	public void testGetNoSectorIdByInvalidCountryId() {
		assertThat(countryService.getSectorIdByCountryId(0L)).isNull();
	}

	@Test
	void testIsStateMandatoryForGivenCountry() {
		CountryDTO savedCountry = countryService.save(countryDto);
		Boolean isStateMandatory = countryService.isStateMandatoryForGivenCountry(savedCountry.getId());
		assertThat(isStateMandatory).isNotNull();
		assertThat(savedCountry.getIsStateMandatory()).isEqualTo(isStateMandatory);
	}
	
	@Test
	public void testIsStateMandatoryForInvalidCountry() {
		assertThat(countryService.isStateMandatoryForGivenCountry(0L)).isFalse();
	}
	
	@Test
	public void testCountriesByInvalidIdList() {
		assertThat(countryService.getCountriesByIdList(new HashSet<>(Arrays.asList(0L)))).isEmpty();
	}
	

	@Test
	void testGetGstVatType() {
		CountryDTO savedCountry = countryService.save(countryDto);
		KeyLabelBeanDTO gstVatType = countryService.getGstVatType(savedCountry.getId());
		assertThat(gstVatType).isNotNull();
		assertThat(gstVatType.getLabel()).isEqualTo(GstVatType.GST.toString());
	}
	
	@Test
	public void testNotGetGstVatTypeForInvalidCountry() {
		KeyLabelBeanDTO gstVatType = countryService.getGstVatType(0L);
		assertThat(gstVatType.getLabel()).isNull();
	}

}
