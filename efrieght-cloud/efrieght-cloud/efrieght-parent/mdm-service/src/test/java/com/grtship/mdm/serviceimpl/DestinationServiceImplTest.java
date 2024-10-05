package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.domain.Sector;
import com.grtship.mdm.domain.State;
import com.grtship.mdm.service.DestinationService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class DestinationServiceImplTest {
	@Autowired
	private DestinationService destinationService;
	
	@Autowired
	private DestinationQueryServiceImpl destinationQueryServiceImpl;
	
	@Autowired private EntityManager em;

	private DestinationDTO destinationDTO;
	
	public static DestinationDTO prepareDestination(EntityManager em) {
		Sector sector = new Sector();
		sector.setCode("AS");
		sector.setName("HDGE");
		em.persist(sector);

		Currency currency = new Currency();
		currency.setCode("INR");
		currency.setName("Indian Rupee");
		em.persist(currency);

		Country country = new Country();
		country.setCode("INA");
		country.setName("Indiana");
		country.setGstOrVatType(GstVatType.GST);
		country.isStateMandatory(true);
		country.setCurrency(currency);
		country.setSector(sector);
		em.persist(country);
		
		State state = new State();
		state.setCode("TS");
		state.setName("Test State");
		state.setCountry(country);
		em.persist(state);

		Set<ObjectAliasDTO> aliases = new HashSet<>();
		ObjectAliasDTO alias = new ObjectAliasDTO();
		alias.setName("MU");
		aliases.add(alias);

		DestinationDTO destinationDTO = new DestinationDTO();
		destinationDTO.setAliases(aliases);
		destinationDTO.setClientId(1L);
		destinationDTO.setCompanyId(1L);
		destinationDTO.setCode("TTS");
		destinationDTO.setStateId(state.getId());
		destinationDTO.setName("Test Save Destination");
		destinationDTO.setType(DestinationType.CITY);
		destinationDTO.setCountryId(country.getId());
		destinationDTO.setSectorName(sector.getName());
		destinationDTO.setCountryName(country.getName());
		destinationDTO.setIsReworkingPort(Boolean.TRUE);
		destinationDTO.setSectorName("Indian Subcontinent");
		return destinationDTO;
	}
	
	@BeforeEach
	public void setUp() {
		destinationDTO=prepareDestination(em);
	}

	@Test
	void testSave() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		System.out.println("Saved Destination" + savedDestination);
		assertThat(savedDestination.getCode()).isEqualTo(destinationDTO.getCode());
		assertThat(savedDestination.getName()).isEqualTo(destinationDTO.getName());
		assertThat(savedDestination.getType()).isEqualTo(destinationDTO.getType());
		assertThat(savedDestination.getIsReworkingPort()).isEqualTo(destinationDTO.getIsReworkingPort());
		assertThat(savedDestination.getCountryId()).isEqualTo(destinationDTO.getCountryId());
		assertThat(savedDestination.getStateId()).isEqualTo(destinationDTO.getStateId());
	}
	
	@Test
	void checkDestinationCodeIsRequired() {
		destinationDTO.setCode(null);
		assertThrows(ConstraintViolationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkDestinationNameIsRequired() {
		destinationDTO.setName(null);
		assertThrows(ConstraintViolationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkDestinationTypeIsRequired() {
		destinationDTO.setType(null);
		assertThrows(ConstraintViolationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	//validation related to saveValidation()
	@Test
	void checkDestinationCodeIsUnique() {
		destinationService.save(destinationDTO);
		destinationDTO.setCode("UDA");
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkDestinationNameIsUnique() {
		destinationService.save(destinationDTO);
		destinationDTO.setCode("UTA");;
		destinationDTO.setName("Udaipur");
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkCompanyIdValidation() {
		destinationDTO.setCompanyId(0L);
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkDestinationAliasIsUnique() {
		destinationService.save(destinationDTO);
		destinationDTO.setCode("UTA");
		destinationDTO.setName("Udaipura");
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	
	@Test
	void checkStateIsMandatoryValidation() {
		destinationDTO.setStateId(null);
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkPortIdValidation() {
		destinationDTO.setType(DestinationType.TERMINAL);
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkCityIdValidation() {
		destinationDTO.setType(DestinationType.AIRPORT);
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkCountryIdValidation() {
		destinationDTO.setType(DestinationType.AIRPORT);
		destinationDTO.setCityId(null);;
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}
	
	@Test
	void checkIATAAirportCodeValidation() {
		destinationDTO.setType(DestinationType.AIRPORT);
		destinationDTO.setCityId(1L);
		assertThrows(ValidationException.class, () -> {
			destinationService.save(destinationDTO);
		});
	}

	@Test
	void testUpdate() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		savedDestination.setName("Test Update Destination");
		savedDestination.setCompanyId(destinationDTO.getCompanyId());
		savedDestination.setCode("TTU");
		DestinationDTO updateDestination = destinationService.update(savedDestination);
		assertThat(updateDestination).isNotNull();
		assertThat(updateDestination.getId()).isEqualTo(savedDestination.getId());
		assertThat(updateDestination.getName()).isNotEqualTo(destinationDTO.getName());
		assertThat(updateDestination.getCode()).isNotEqualTo(destinationDTO.getCode());
		assertThat(updateDestination.getType()).isEqualTo(savedDestination.getType());
		assertThat(updateDestination.getCountryId()).isEqualTo(destinationDTO.getCountryId());
	}

	@Test
	void testDelete() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		destinationService.delete(savedDestination.getId());
		Optional<DestinationDTO> optional=destinationQueryServiceImpl.findOne(savedDestination.getId());
		assertFalse(optional.isPresent());
	}
}
