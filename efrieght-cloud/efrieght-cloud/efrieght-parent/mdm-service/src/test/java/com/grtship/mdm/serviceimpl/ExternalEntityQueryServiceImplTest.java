package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.grtship.core.dto.EntityMultiDropDownDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.criteria.ExternalEntityCriteria;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.domain.EntityBusinessType;
import com.grtship.mdm.domain.EntityBusinessTypeId;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.repository.CreditTermsRepository;
import com.grtship.mdm.repository.EntityBusinessTypeRepository;
import com.grtship.mdm.repository.ExternalEntityRepository;
import com.grtship.mdm.service.ExternalEntityQueryService;
import com.grtship.mdm.service.ExternalEntityService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class ExternalEntityQueryServiceImplTest {
	
    private static final String ERRORERROR = "ERRORERROR";

	private static final String EXTERNAL_ENTITY = "External Entity";

	@Autowired private EntityManager em;
	@Autowired private ExternalEntityService entityService;
	@Autowired private ExternalEntityRepository entityRepository;
	@Autowired private CreditTermsRepository creditTermsRepository;
	@Mock private Pageable pageable;
	@Autowired private ExternalEntityQueryService entityFilterService;
	@Autowired private EntityBusinessTypeRepository entityBusinessDetailsRepository;

	private ExternalEntityRequestDTO entityReqDto;
	private ExternalEntityCriteria entityCriteria;
	
	@BeforeEach
	public void setUp() {
		entityReqDto=ExternalEntityServiceImplTest.createEntityRequestDto(em);
	}

	@Test
	void testFindAll() {
		entityService.save(entityReqDto);
		List<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(null, pageable).getContent();
		assertThat(entityDTOs).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
	}
	
	@Test
	void testFindAllByCriteriaName() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setName(savedEntity.getName());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent())
				.allMatch(entityDTO -> entityDTO.getName().contains(entityCriteria.getName()));
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
	}
	
	@Test
	void testFindAllByCriteriaNameForInvalidName() {
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setName("ERRORERRORERROR");
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getContent()).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaCode() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		System.out.println("Saved Entity"+savedEntity.getAddress());
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setCode(savedEntity.getCode());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent())
				.allMatch(entityDTO -> entityDTO.getCode().contains(entityCriteria.getCode()));
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
	}
	
	@Test
	void testFindAllByCriteriaCodeForInvalidCode() {
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setCode(ERRORERROR);
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getContent()).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaActiveFlag() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setActiveFlag(savedEntity.getActiveFlag());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
		assertThat(entityDTOs.getContent())
				.allMatch(entityDTO -> entityDTO.getActiveFlag().equals(entityCriteria.getActiveFlag()));
	}
	
	@Test
	void testFindAllByCriteriaCustomerFlag() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setCustomerFlag(savedEntity.getCustomerFlag());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
		assertThat(entityDTOs.getContent())
				.allMatch(entityDTO -> entityDTO.getCustomerFlag().equals(entityCriteria.getCustomerFlag()));
	}
	
	@Test
	void testFindAllByCriteriaVendorFlag() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setVendorFlag(savedEntity.getVendorFlag());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
		assertThat(entityDTOs.getContent())
				.allMatch(entityDTO -> entityDTO.getVendorFlag().equals(entityCriteria.getVendorFlag()));
	}
	
	@Test
	void testFindAllByCriteriaId() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setId(savedEntity.getId());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getId() == entityCriteria.getId());
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
	}
	
	@Test
	void testFindAllByCriteriaIdForInvalidId() {
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setId(0L);
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getContent()).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaStatus() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setStatus(savedEntity.getStatus().name());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
		assertThat(entityDTOs.getContent())
				.allMatch(entityDTO -> entityDTO.getStatus().equals(DomainStatus.valueOf(entityCriteria.getStatus())));
	}
	
	
	@Test
	void testFindAllByCriteriaAliasName() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		List<ObjectAliasDTO> aliasDTOs = savedEntity.getExternalEntityAlias().stream().collect(Collectors.toList());
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setAlias(aliasDTOs.get(0).getName());
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getExternalEntityAlias().stream()
				.map(ObjectAliasDTO::getName).collect(Collectors.toSet()).contains(entityCriteria.getAlias()));
	}
	
	@Test
	void testFindAllByCriteriaIdForInvalidAliasName() {
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setAlias("ERRORERRORERROR");
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getContent()).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaEntityType() {
		entityService.save(entityReqDto);
		List<String> entityDetails = entityReqDto.getEntityDetails().stream().collect(Collectors.toList());
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setEntityBusinessType(entityDetails.get(0));
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getSize()).isEqualTo(20);
		assertThat(entityDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCode() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getName() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCompanyId() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getEmail() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getCellNo() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getAddress() != null);
		assertThat(entityDTOs.getContent()).allMatch(entityDTO -> entityDTO.getAddress().getCountryId() != null);
		assertThat(entityDTOs.getContent())
				.allMatch(entityDTO -> getEntityDetailsById(entityDTO).contains(entityCriteria.getEntityBusinessType()));
	}

	private List<String> getEntityDetailsById(ExternalEntityDTO entityDTO) {
		return entityBusinessDetailsRepository
				.findByIdExternalEntityId(entityDTO.getId()).stream().map(EntityBusinessType::getId).map(EntityBusinessTypeId::getEntityType).map(EntityType::name).collect(Collectors.toList());
	}
	
	@Test
	void testFindAllByCriteriaIdForInvalidEntityBussinessType() {
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setEntityBusinessType(ERRORERROR);
		Page<ExternalEntityDTO> entityDTOs = entityFilterService.findAll(entityCriteria, PageRequest.of(0, 20));
		assertThat(entityDTOs.getContent()).isEmpty();
	}

	@Test
	void testFindByCriteriaName() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setName(savedEntity.getName());
		entityCriteria.setId(savedEntity.getId());
		ExternalEntityDTO entityDTOByCriteria = entityFilterService.findByCriteria(entityCriteria);
		assertThat(entityDTOByCriteria).isNotNull();
		assertThat(entityDTOByCriteria.getId()).isEqualTo(savedEntity.getId());
		assertThat(entityDTOByCriteria.getName()).isNotBlank();
		assertThat(entityDTOByCriteria.getCode()).isNotBlank();
		assertThat(entityDTOByCriteria.getCode()).isEqualTo(savedEntity.getCode());
		assertThat(entityDTOByCriteria.getCompanyType()).isEqualTo(savedEntity.getCompanyType());
		assertThat(entityDTOByCriteria.getCompanyId()).isEqualTo(savedEntity.getClientId());
		assertThat(entityDTOByCriteria.getGstType()).isNotNull();
		assertThat(entityDTOByCriteria.getExternalEntityAlias().size())
				.isEqualTo(entityDTOByCriteria.getExternalEntityAlias().size());
		List<EntityBusinessType> savedEntityDetails = entityBusinessDetailsRepository
				.findByIdExternalEntityId(savedEntity.getId());
		Set<CreditTerms> savedCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(EXTERNAL_ENTITY,
				Arrays.asList(savedEntity.getId()));
		List<EntityBusinessType> entityDetailsForCriteria = entityBusinessDetailsRepository
				.findByIdExternalEntityId(entityDTOByCriteria.getId());
		Set<CreditTerms> creditTermsForCriteria = creditTermsRepository
				.findByReferenceNameAndReferenceIdIn(EXTERNAL_ENTITY, Arrays.asList(entityDTOByCriteria.getId()));
		assertThat(entityDetailsForCriteria.size()).isEqualTo(savedEntityDetails.size());
		assertThat(creditTermsForCriteria.size()).isEqualTo(savedCreditTerms.size());
	}

	@Test
	void testFindOne() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		ExternalEntityDTO entityById = entityFilterService.findOne(savedEntity.getId()).get();
		assertThat(entityById).isNotNull();
		assertThat(entityById.getId()).isEqualTo(savedEntity.getId());
		assertThat(entityById.getName()).isNotBlank();
		assertThat(entityById.getCode()).isNotBlank();
		assertThat(entityById.getCode()).isEqualTo(savedEntity.getCode());
		assertThat(entityById.getCompanyType()).isEqualTo(savedEntity.getCompanyType());
		assertThat(entityById.getCompanyId()).isEqualTo(savedEntity.getClientId());
		assertThat(entityById.getGstType()).isNotNull();
		assertThat(entityById.getExternalEntityAlias().size()).isEqualTo(entityById.getExternalEntityAlias().size());
		List<EntityBusinessType> savedEntityDetails = entityBusinessDetailsRepository
				.findByIdExternalEntityId(savedEntity.getId());
		Set<CreditTerms> savedCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(EXTERNAL_ENTITY,
				Arrays.asList(savedEntity.getId()));
		List<EntityBusinessType> entityDetailsForId = entityBusinessDetailsRepository
				.findByIdExternalEntityId(entityById.getId());
		Set<CreditTerms> creditTermsForId = creditTermsRepository.findByReferenceNameAndReferenceIdIn(EXTERNAL_ENTITY,
				Arrays.asList(entityById.getId()));
		assertThat(entityDetailsForId.size()).isEqualTo(savedEntityDetails.size());
		assertThat(creditTermsForId.size()).isEqualTo(savedCreditTerms.size());
	}

	@Test
	void testGetAllExternalEntitiesForMultiDD() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		ExternalEntity findById = entityRepository.findById(savedEntity.getId()).get();
		findById.setActiveFlag(Boolean.TRUE);
		entityRepository.save(findById);
		entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setCustomerFlag(savedEntity.getCustomerFlag());
		Page<EntityMultiDropDownDTO> externalEntities = entityFilterService
				.getAllExternalEntitiesForMultiDD(entityCriteria, PageRequest.of(0,20));
		assertThat(externalEntities.getContent()).hasSizeGreaterThanOrEqualTo(1);
		assertThat(externalEntities.getContent()).allMatch(entity -> entity.getId() != null);
		assertThat(externalEntities.getContent()).allMatch(entity -> entity.getCode() != null);
	}

	@Test
	void testExistById() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		boolean exists=entityFilterService.existById(savedEntity.getId());
		assertTrue(exists);
	}
	
	@Test
	void testExistByIdForInvalidId() {
		boolean exists=entityFilterService.existById(0L);
		assertFalse(exists);
	}

}
