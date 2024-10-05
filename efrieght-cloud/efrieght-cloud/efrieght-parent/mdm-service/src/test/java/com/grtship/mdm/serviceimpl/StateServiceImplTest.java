package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.StateDTO;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.domain.Sector;
import com.grtship.mdm.domain.State;
import com.grtship.mdm.mapper.StateMapper;
import com.grtship.mdm.service.StateService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class StateServiceImplTest {
	
private static final String stateName="Maharashtra Maharashtra Maharashtra Maharashtra Maharashtra";
	
	@Autowired
	private StateService stateService;
	
	@Autowired
	private StateMapper stateMapper;
	
	@Autowired
	private EntityManager em;
	
	@Mock
	private Pageable pageable;
	
	private StateDTO stateDTO;
	
	public static StateDTO prepareStateDto(EntityManager em) {
		Sector sector=new Sector();
		sector.setName("Sounth East Asia");
		sector.setCode("SEA");
		em.persist(sector);
		
		Currency currency=new Currency();
		currency.setCode("INR");
		currency.setName("Indian Rupees");
		em.persist(currency);
		
		Country country=new Country();
		country.setCode("IND");
	    country.setName("India");
	    country.setGstOrVatType(GstVatType.GST);
	    country.setSector(sector);
	    country.setCurrency(currency);
	    em.persist(country);
		
		StateDTO stateDTO=new StateDTO();
		stateDTO.setCode("MH");
		stateDTO.setName("Maharashtra");
		stateDTO.setCountryId(country.getId());
		return stateDTO;
	}

    @BeforeEach
    void setUp() {
       stateDTO=prepareStateDto(em);	
    }
    
	@Test
	void testSave() {
		StateDTO savedState = stateService.save(stateDTO);
		assertThat(savedState.getId()).isNotNull();
		assertThat(savedState.getCode()).isEqualTo(stateDTO.getCode());
		assertThat(savedState.getName()).isEqualTo(stateDTO.getName());
		assertThat(savedState.getCountryId()).isEqualTo(stateDTO.getCountryId());
	}

	@Test
	void checkCodeIsRequired() {
		stateDTO.setCode(null);
		assertThrows(ConstraintViolationException.class,() -> {
			stateService.save(stateDTO);
		});
	}
	
	@Test
	void checkNameIsRequired() {
		stateDTO.setName(null);
		assertThrows(ConstraintViolationException.class,() -> {
			stateService.save(stateDTO);
		});
	}
	
	@Test
	void checkIfCodeLenghtIsProper() {
		stateDTO.setCode("MAHA");
		assertThrows(ConstraintViolationException.class,() -> {
			stateService.save(stateDTO);
		});
	}
	
	@Test
	void checkIfNameLenghtISProper() {
		stateDTO.setName(stateName);
		assertThrows(ConstraintViolationException.class,() -> {
			stateService.save(stateDTO);
		});
	}
	
	
	@Test
	void testFindAll() {
		stateService.save(stateDTO);
		List<StateDTO> states = stateService.findAll(pageable).getContent();
		assertThat(states).asList();
		assertThat(states).allMatch(state -> state.getId() != null);
		assertThat(states).allMatch(state -> state.getCode() != null);
		assertThat(states).allMatch(state -> state.getName() != null);
	}

	@Test
	void testFindOne() {
		StateDTO savedState = stateService.save(stateDTO);
		StateDTO dto = stateService.findOne(savedState.getId()).get();
		assertThat(dto.getCode()).isEqualTo(stateDTO.getCode());
		assertThat(dto.getName()).isEqualTo(stateDTO.getName());
		assertThat(dto.getCountryId()).isEqualTo(stateDTO.getCountryId());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<StateDTO> dto = stateService.findOne(0L);
		assertFalse(dto.isPresent());
	}

	@Test
	void testDelete() {
		StateDTO savedState = stateService.save(stateDTO);
		stateService.delete(savedState.getId());
		Optional<StateDTO> dto = stateService.findOne(savedState.getId());
		assertFalse(dto.isPresent());
	}

	@Test
	void testSaveAll() {
	   State state = stateMapper.toEntity(stateService.save(stateDTO));
       Set<State> states=new TreeSet<>();
       states.add(state);
	   List<State> savedStates = stateService.saveAll(states);
	   assertThat(savedStates).isNotEmpty();
	   assertThat(savedStates).hasSize(1);
	   assertThat(savedStates).allMatch(stateObj -> stateObj.getId()!=null);
	   assertThat(savedStates).allMatch(stateObj -> stateObj.getName()!=null);
	   assertThat(savedStates).allMatch(stateObj -> stateObj.getCode()!=null);
	   assertThat(savedStates).allMatch(stateObj -> stateObj.getCountry()!=null);
	}

	@Test
	void testGetStatesByIds() {
		StateDTO savedState = stateService.save(stateDTO);
		List<Long> stateIds=new ArrayList<>();
		stateIds.add(savedState.getId());
		List<StateDTO> statesByIds = stateService.getStatesByIds(stateIds);
		assertThat(statesByIds).isNotEmpty();
		assertThat(statesByIds).allMatch(state -> state.getId().equals(stateIds.get(0)));
		assertThat(statesByIds).allMatch(state -> state.getCode()!=null);
		assertThat(statesByIds).allMatch(state -> state.getName()!=null);
		assertThat(statesByIds).allMatch(state -> state.getCountryId()!=null);
	}
	
	@Test
	void testGetStatesByIdsForInvalidIds() {
		List<Long> stateIds=new ArrayList<>();
		stateIds.add(0l);
		List<StateDTO> statesByIds = stateService.getStatesByIds(stateIds);
		assertThat(statesByIds).isEmpty();
	}

	@Test
	void testGetStatesIdsByCountryId() {
		StateDTO savedState = stateService.save(stateDTO);
		List<Long> stateIds = stateService.getStatesIdsByCountryId(savedState.getCountryId());
		assertThat(stateIds).isNotEmpty();
		assertThat(stateIds).hasSizeGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testGetStatesIdsByCountryIdForInvalidCountry() {
		List<Long> stateIds = stateService.getStatesIdsByCountryId(0l);
		assertThat(stateIds).isEmpty();
	}

	@Test
	void testGetStateNameById() {
		StateDTO savedState = stateService.save(stateDTO);
		String stateName = stateService.getStateNameById(savedState.getId());
		assertThat(stateName).isEqualTo(savedState.getName());
	}
	
	@Test
	void testGetByCountryId() {
		StateDTO savedState = stateService.save(stateDTO);
		List<StateDTO> states = stateService.getByCountryId(savedState.getCountryId());
		assertThat(states).isNotEmpty();
		assertThat(states).allMatch(state -> state.getId() != null);
		assertThat(states).allMatch(state -> state.getCode() != null);
		assertThat(states).allMatch(state -> state.getName() != null);
		assertThat(states).allMatch(state -> state.getCountryId().equals(savedState.getCountryId()));
	}
	
	@Test
	void testGetByCountryIdForInvalidCountry() {
		List<StateDTO> states = stateService.getByCountryId(0L);
		assertThat(states).isEmpty();
	}
	
}
