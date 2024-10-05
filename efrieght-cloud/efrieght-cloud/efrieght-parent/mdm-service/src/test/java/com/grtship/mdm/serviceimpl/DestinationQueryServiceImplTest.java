package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.mdm.criteria.DestinationCriteria;
import com.grtship.mdm.domain.Destination;
import com.grtship.mdm.service.DestinationService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test"})
public class DestinationQueryServiceImplTest {
	@Autowired
	private DestinationService destinationService;

	@Autowired
	private DestinationQueryServiceImpl destinationQueryServiceImpl;
	
	@Autowired private EntityManager em;

	private DestinationDTO destinationDTO;
	
	private DestinationCriteria destinationCriteria;
	
	@BeforeEach
	public void setUp() {
		destinationDTO=DestinationServiceImplTest.prepareDestination(em);
	}

	@Test
	void testFindByCriteriaDestinationCriteriaName() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		destinationCriteria=new DestinationCriteria();
		destinationCriteria.setName(savedDestination.getName());
		List<Destination> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId()!=null);
		assertThat(destinations).allMatch(destination -> destination.getName().contains(destinationCriteria.getName()));
		assertThat(destinations).allMatch(destination -> destination.getCode()!=null);
		assertThat(destinations).allMatch(destination -> destination.getType()!=null);
	}
	
	@Test
	void testFindByCriteriaDestinationCriteriaNameForInvalidName() {
		destinationCriteria=new DestinationCriteria();
		destinationCriteria.setName("Invalid Name");
		List<Destination> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isEmpty();
	}
	
	@Test
	void testFindByCriteriaDestinationCriteriaCode() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		destinationCriteria=new DestinationCriteria();
		destinationCriteria.setCode(savedDestination.getCode());
		List<Destination> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId()!=null);
		assertThat(destinations).allMatch(destination -> destination.getName()!=null);
		assertThat(destinations).allMatch(destination -> destination.getCode().contains(destinationCriteria.getCode()));
		assertThat(destinations).allMatch(destination -> destination.getType()!=null);
	}
	
	@Test
	void testFindByCriteriaDestinationCriteriaNameForInvalidCode() {
		destinationCriteria=new DestinationCriteria();
		destinationCriteria.setCode("Invalid Code");
		List<Destination> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isEmpty();
	}
	
	@Test
	void testFindByCriteriaDestinationCriteriaCountryName() {
		destinationService.save(destinationDTO);
		destinationCriteria = new DestinationCriteria();
		destinationCriteria.setCountry(destinationDTO.getCountryName());
		List<Destination> destinations = destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId() != null);
		assertThat(destinations).allMatch(destination -> destination.getName() != null);
		assertThat(destinations).allMatch(destination -> destination.getType() != null);
	}
	
	@Test
	void testFindByCriteriaDestinationCriteriaNameForInvalidCountryName() {
		destinationCriteria = new DestinationCriteria();
		destinationCriteria.setCountry("Invalid Country");
		List<Destination> destinations = destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isEmpty();
	}
	
//	@Test
//	void testFindByCriteriaDestinationCriteriaSector() {
//		DestinationDTO savedDestination = destinationService.save(destinationDTO);
//		destinationCriteria=new DestinationCriteria();
//		destinationCriteria.setSector(destinationDTO.getSectorName());
//		List<Destination> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria);
//		assertThat(destinations).isNotEmpty();
//		assertThat(destinations).allMatch(destination -> destination.getId()!=null);
//		assertThat(destinations).allMatch(destination -> destination.getName()!=null);
//		assertThat(destinations).allMatch(destination -> destination.getCode()!=null);
//		assertThat(destinations).allMatch(destination -> destination.getType()!=null);
//		assertThat(destinations).allMatch(destination -> destination.getCountry().getSector().getName().equals(destinationCriteria.getSector()));
//	}
	
	@Test
	void testFindByCriteriaDestinationCriteriaType() {
		destinationService.save(destinationDTO);
		destinationCriteria = new DestinationCriteria();
		destinationCriteria.setType("CITY");
		List<Destination> destinations = destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId() != null);
		assertThat(destinations).allMatch(destination -> destination.getName() != null);
		assertThat(destinations).allMatch(destination -> destination.getCode() != null);
		assertThat(destinations).allMatch(destination -> destination.getType() != null);
		assertThat(destinations)
				.allMatch(destination -> destination.getType().equals(DestinationType.valueOf(destinationCriteria.getType())));
	}
	
	
	@Test
	void testFindByCriteriaDestinationCriteriaIsReworkingPort() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		destinationCriteria=new DestinationCriteria();
		destinationCriteria.setIsReworkingPort(savedDestination.getIsReworkingPort());;
		List<Destination> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId()!=null);
		assertThat(destinations).allMatch(destination -> destination.getName()!=null);
		assertThat(destinations).allMatch(destination -> destination.getCode()!=null);
		assertThat(destinations).allMatch(destination -> destination.getType()!=null);
		assertThat(destinations).allMatch(destination -> destination.getIsReworkingPort().equals(destinationCriteria.getIsReworkingPort()));
	}

	@Test
	void testFindByCriteriaDestinationCriteriaPageable() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		destinationCriteria=new DestinationCriteria();
		destinationCriteria.setName(savedDestination.getName());
		Page<DestinationDTO> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria, PageRequest.of(0, 20));
		assertThat(destinations.getSize()).isEqualTo(20);
		assertThat(destinations.getContent()).allMatch(destination -> destination.getId()!=null);
		assertThat(destinations.getContent()).allMatch(destination -> destination.getName().contains(destinationCriteria.getName()));
		assertThat(destinations.getContent()).allMatch(destination -> destination.getCode()!=null);
		assertThat(destinations.getContent()).allMatch(destination -> destination.getType()!=null);
	}

	@Test
	void testCountByCriteria() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		destinationCriteria = new DestinationCriteria();
		destinationCriteria.setCountryId(savedDestination.getCountryId());
		long count = destinationQueryServiceImpl.countByCriteria(destinationCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}

	@Test
	void testFindOne() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		DestinationDTO getDestinationById=destinationQueryServiceImpl.findOne(savedDestination.getId()).get();
		assertThat(savedDestination.getCode()).isEqualTo(getDestinationById.getCode());
		assertThat(savedDestination.getName()).isEqualTo(getDestinationById.getName());
		assertThat(savedDestination.getType()).isEqualTo(getDestinationById.getType());
		assertThat(savedDestination.getCompanyId()).isEqualTo(getDestinationById.getCompanyId());
		assertThat(savedDestination.getClientId()).isEqualTo(getDestinationById.getClientId());
		assertThat(savedDestination.getBranchId()).isEqualTo(getDestinationById.getBranchId());
		assertThat(savedDestination.getIsReworkingPort()).isEqualTo(getDestinationById.getIsReworkingPort());
		assertThat(savedDestination.getCountryId()).isEqualTo(getDestinationById.getCountryId());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<DestinationDTO> getDestinationById=destinationQueryServiceImpl.findOne(0L);
		assertFalse(getDestinationById.isPresent());
	}
	
	

	@Test
	void testGetCities() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		List<DestinationDTO> destinations=destinationQueryServiceImpl.getCities(savedDestination.getCountryId());
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId()!=null);
		assertThat(destinations).allMatch(destination -> destination.getName()!=null);
		assertThat(destinations).allMatch(destination -> destination.getCode()!=null);
		assertThat(destinations).allMatch(destination -> destination.getType().toString().equals(DestinationType.CITY.toString()));
		assertThat(destinations).allMatch(destination -> destination.getCountryId().equals(savedDestination.getCountryId()));
	}

	@Test
	void testGetPorts() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		List<BaseDTO> baseDTOs = destinationQueryServiceImpl.getPorts(savedDestination.getCityId());
		assertThat(baseDTOs).isEmpty();
	}

	@Test
	void testGetDestinationsByIdList() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		List<Long> ids = new ArrayList<>();
		ids.add(savedDestination.getId());
		List<DestinationDTO> destinations = destinationQueryServiceImpl.getDestinationsByIdList(ids);
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId().equals(savedDestination.getId()));
		assertThat(destinations).allMatch(destination -> destination.getName() != null);
		assertThat(destinations).allMatch(destination -> destination.getCode() != null);
		assertThat(destinations).allMatch(destination -> destination.getType() != null);
	}
	
	@Test
	void testFindByCriteriaDestinationCriteriaNameForInvalidIdList() {
		List<Long> ids = new ArrayList<>();
		ids.add(0L);
		List<DestinationDTO> destinations = destinationQueryServiceImpl.getDestinationsByIdList(ids);
		assertThat(destinations).isEmpty();
	}

	@Test
	void testGetDestinationByCountryId() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		List<DestinationDTO> destinations=destinationQueryServiceImpl.getDestinationByCountryId(savedDestination.getCountryId());
		assertThat(destinations).isNotEmpty();
		assertThat(destinations).allMatch(destination -> destination.getId()!=null);
		assertThat(destinations).allMatch(destination -> destination.getName()!=null);
		assertThat(destinations).allMatch(destination -> destination.getCode()!=null);
		assertThat(destinations).allMatch(destination -> destination.getType()!=null);
		assertThat(destinations).allMatch(destination -> destination.getCountryId().equals(savedDestination.getCountryId()));
	}
	
	@Test
	void testGetDestinationByCountryIdForInvalidCountryId() {
		List<DestinationDTO> destinations=destinationQueryServiceImpl.getDestinationByCountryId(0L);
		assertThat(destinations).isEmpty();
	}

	@Test
	void testGetDestinationStateId() {
		DestinationDTO savedDestination = destinationService.save(destinationDTO);
		List<DestinationDTO> destinationDTOs = destinationQueryServiceImpl
				.getDestinationStateId(savedDestination.getStateId());
		assertThat(destinationDTOs).isNotEmpty();
		assertThat(destinationDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(destinationDTOs).allMatch(destination -> destination.getId() != null);
		assertThat(destinationDTOs).allMatch(destination -> destination.getName() != null);
		assertThat(destinationDTOs).allMatch(destination -> destination.getCode() != null);
		assertThat(destinationDTOs).allMatch(destination -> destination.getType() != null);
		assertThat(destinationDTOs)
				.allMatch(destination -> destination.getStateId().equals(savedDestination.getStateId()));
	}

	@Test
	void testFindByCriteriaDestinationCriteriaNameForInvalidStateId() {
		destinationCriteria=new DestinationCriteria();
		destinationCriteria.setStateId(0L);
		List<Destination> destinations=destinationQueryServiceImpl.findByCriteria(destinationCriteria);
		assertThat(destinations).isEmpty();
	}
}
