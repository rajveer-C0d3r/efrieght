package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.SectorDTO;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class SectorServiceImplTest {
	
	@Autowired
	private SectorServiceImpl sectorServiceImpl;
	
	@Autowired
	private CountryServiceImpl countryServiceImpl;
	
	@Autowired
	private EntityManager em;
	
	private SectorDTO sectorDTO;
	
	public static SectorDTO prepareSectorDto() {
		SectorDTO sectorDTO=new SectorDTO();
		sectorDTO.setCode("ASP");
		sectorDTO.setName("Asia Pacific");
		sectorDTO.setCompanyId(1L);
		sectorDTO.setClientId(1L);
		return sectorDTO;
	}
	
	@BeforeEach
	void setUp() {
		sectorDTO=prepareSectorDto();
	}

	@Test
	void testSave() {
		SectorDTO savedSector = sectorServiceImpl.save(sectorDTO);
		assertThat(savedSector.getId()).isNotNull();
		assertThat(savedSector.getCode()).isEqualTo(sectorDTO.getCode());
		assertThat(savedSector.getName()).isEqualTo(sectorDTO.getName());
		assertThat(savedSector.getCompanyId()).isEqualTo(sectorDTO.getCompanyId());
		assertThat(savedSector.getClientId()).isEqualTo(sectorDTO.getClientId());
	}
	
	@Test
	void checkCodeIsUnique() {
		sectorServiceImpl.save(sectorDTO);
		assertThrows(ValidationException.class,() -> {
			sectorServiceImpl.save(sectorDTO);
		});
	}
	
	@Test
	void checkNameIsUnique() {
		sectorServiceImpl.save(sectorDTO);
		sectorDTO.setCode("NJK");
		assertThrows(ValidationException.class,() -> {
			sectorServiceImpl.save(sectorDTO);
		});
	}
	
	@Test
	void testUpdate() {
		SectorDTO savedSector = sectorServiceImpl.save(sectorDTO);
		savedSector.setName("Asia North");
		SectorDTO updatedSector = sectorServiceImpl.save(savedSector);
		assertThat(savedSector.getId()).isEqualTo(updatedSector.getId());
		assertThat(savedSector.getCode()).isEqualTo(updatedSector.getCode());
		assertThat(sectorDTO.getName()).isNotEqualTo(updatedSector.getName());
		assertThat(savedSector.getCompanyId()).isEqualTo(updatedSector.getCompanyId());
		assertThat(savedSector.getClientId()).isEqualTo(updatedSector.getClientId());
	}

	@Test
	void testFindAll() {
		sectorServiceImpl.save(sectorDTO);
		List<SectorDTO> sectorList = sectorServiceImpl.findAll();
		assertThat(sectorList).isNotEmpty();
		assertThat(sectorList).allMatch(sector -> sector.getId() != null);
		assertThat(sectorList).allMatch(sector -> sector.getCode() != null);
		assertThat(sectorList).allMatch(sector -> sector.getName() != null);
	}

	@Test
	void testFindOne() {
		SectorDTO savedSector = sectorServiceImpl.save(sectorDTO);
		Optional<SectorDTO> getSectorById = sectorServiceImpl.findOne(savedSector.getId());
		assertTrue(getSectorById.isPresent());
		assertThat(savedSector.getId()).isEqualTo(getSectorById.get().getId());
		assertThat(savedSector.getCode()).isEqualTo(getSectorById.get().getCode());
		assertThat(savedSector.getName()).isEqualTo(getSectorById.get().getName());
		assertThat(savedSector.getCompanyId()).isEqualTo(getSectorById.get().getCompanyId());
		assertThat(savedSector.getClientId()).isEqualTo(getSectorById.get().getClientId());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<SectorDTO> getSectorById = sectorServiceImpl.findOne(0l);
		assertFalse(getSectorById.isPresent());
	}

	@Test
	void testFindSectorNameByIdList() {
		SectorDTO savedSector1 = sectorServiceImpl.save(sectorDTO);
		sectorDTO.setCode("ASN");
		sectorDTO.setName("Asia North");
		SectorDTO savedSector2 = sectorServiceImpl.save(sectorDTO);
		Set<Long> ids = new TreeSet<>();
		ids.add(savedSector1.getId());
		ids.add(savedSector2.getId());
		Map<Long, String> sectorNameByIdList = sectorServiceImpl.findSectorNameByIdList(ids);
		assertThat(sectorNameByIdList).isNotEmpty();
		assertThat(sectorNameByIdList).hasSize(2);
		assertThat(sectorNameByIdList.get(savedSector1.getId())).isEqualTo(savedSector1.getName());
		assertThat(sectorNameByIdList.get(savedSector2.getId())).isEqualTo(savedSector2.getName());
	}
	
	@Test
	void testFindSectorNameByIdListForInvalidIds() {
		Set<Long> ids = new TreeSet<>();
		ids.add(0l);
		Map<Long, String> sectorNameByIdList = sectorServiceImpl.findSectorNameByIdList(ids);
		assertThat(sectorNameByIdList).isEmpty();
	}

	@Test
	void testDelete() {
		SectorDTO savedSector = sectorServiceImpl.save(sectorDTO);
		sectorServiceImpl.delete(savedSector.getId());
		Optional<SectorDTO> getSectorById = sectorServiceImpl.findOne(savedSector.getId());
		assertFalse(getSectorById.isPresent());
	}

	@Test
	void testGetByCountryId() {
		CountryDTO countryDTO = CountryServiceImplTest.createEntity(em);
		SectorDTO savedSector = sectorServiceImpl.save(sectorDTO);
		countryDTO.setSectorId(savedSector.getId());
		CountryDTO savedCountry = countryServiceImpl.save(countryDTO);
		Optional<SectorDTO> sectorByCountryId = sectorServiceImpl.getByCountryId(savedCountry.getId());
		assertTrue(sectorByCountryId.isPresent());
		assertThat(sectorByCountryId.get().getId()).isEqualTo(savedSector.getId());
		assertThat(sectorByCountryId.get().getCode()).isEqualTo(savedSector.getCode());
		assertThat(sectorByCountryId.get().getName()).isEqualTo(savedSector.getName());
		assertThat(sectorByCountryId.get().getCompanyId()).isEqualTo(savedSector.getCompanyId());
		assertThat(sectorByCountryId.get().getClientId()).isEqualTo(savedSector.getClientId());
	}
	
	@Test
	void testGetByCountryIdForInvalidCountry() {
		Optional<SectorDTO> sectorByCountryId = sectorServiceImpl.getByCountryId(0l);
		assertFalse(sectorByCountryId.isPresent());
	}

}
