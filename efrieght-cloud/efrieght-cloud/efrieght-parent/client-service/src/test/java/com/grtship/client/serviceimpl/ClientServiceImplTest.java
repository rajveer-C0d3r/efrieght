package com.grtship.client.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.adaptor.UserModuleAdaptor;
import com.grtship.client.feignclient.MasterModule;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.service.ClientService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.DomainStatus;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class ClientServiceImplTest {
	
	@Autowired private ClientService clientService;

	@Autowired private ClientQueryServiceImpl clientQueryService;
	
	@MockBean private MasterModule masterModule;

	@MockBean private OAuthModule authModule;
	
	@MockBean private UserModuleAdaptor userModuleAdaptor;

	private ClientDTO clientDto;

	public static ClientDTO prepareClientDto() {
		AddressDTO addressDto= CompanyServiceImplTest.getAddress();	
		List<GsaDetailsDTO> gsaUsers = prepareGsaUsers();
		ClientDTO clientDto = new ClientDTO();
		clientDto.setActiveFlag(Boolean.FALSE);
		clientDto.setAddress(addressDto);
		clientDto.setStatus(DomainStatus.PENDING);
		clientDto.setCode("CC789");
		clientDto.setName("Junit Test Client");
		clientDto.setEmailId("test.client@localhost.com");
		clientDto.setSalesTaxId("HGT65TFSW3T");
		clientDto.setMobileNo("8898617911");
		clientDto.setPanNo("DFHT56FG4F");
		clientDto.setGsaDetails(gsaUsers);
		return clientDto;
	}

	private static List<GsaDetailsDTO> prepareGsaUsers() {
		List<GsaDetailsDTO> gsaUsers=new ArrayList<>();
		GsaDetailsDTO  gsaDetailsDto= new GsaDetailsDTO();
		gsaDetailsDto.setContactNo("9198786765");
		gsaDetailsDto.setEmail("gsa1@localhost.com");
		gsaDetailsDto.setName("gsa1");
		gsaUsers.add(gsaDetailsDto);
		return gsaUsers;
	}
	
	@BeforeEach
	void setUp() {
		clientDto=prepareClientDto();
	}

	@Test
	void testSave() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		assertThat(savedClient.getId()).isNotNull();
		assertThat(savedClient.getName()).isEqualTo(clientDto.getName());
		assertThat(savedClient.getCode()).isEqualTo(clientDto.getCode());
		assertThat(savedClient.getEmailId()).isEqualTo(clientDto.getEmailId());
		assertThat(savedClient.getMobileNo()).isEqualTo(clientDto.getMobileNo());
		assertThat(savedClient.getPanNo()).isEqualTo(clientDto.getPanNo());
		assertThat(savedClient.getSalesTaxId()).isEqualTo(clientDto.getSalesTaxId());
		assertThat(savedClient.getAddress()).isNotNull();
		assertThat(savedClient.getAddress().getCountryId()).isEqualTo(clientDto.getAddress().getCountryId());
		assertThat(savedClient.getAddress().getStateId()).isEqualTo(clientDto.getAddress().getStateId());
		assertThat(savedClient.getAddress().getCityId()).isEqualTo(clientDto.getAddress().getCityId());
		Map<Long, List<GsaDetailsDTO>> gsaUserMap = new HashMap<>();
		gsaUserMap.put(savedClient.getId(), clientDto.getGsaDetails());
		when(authModule.getGsaUsersByClientIdList(Arrays.asList(savedClient.getId()))).thenReturn(gsaUserMap);
		ClientDTO savedClientDTO = clientQueryService.findOne(savedClient.getId()).get();
		List<GsaDetailsDTO> savedGsaUsers = savedClientDTO.getGsaDetails();
		assertThat(savedGsaUsers).hasSize(1);
	}

	@Test 
	void testUpdate() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		savedClient.setName("Test Client Update");
		savedClient.setPanNo("RF849161KM");
		savedClient.setEmailId("test.update@localhost.com");
		savedClient.setMobileNo("8465165151");
		savedClient.setSubmittedForApproval(Boolean.FALSE);
		savedClient.setGsaDetails(prepareGsaUsers());
		ClientDTO updatedClient = clientService.update(savedClient);
		assertThat(updatedClient.getId()).isNotNull();
		assertThat(updatedClient.getId()).isEqualTo(savedClient.getId());
		assertThat(updatedClient.getName()).isNotEqualTo(clientDto.getName());
		assertThat(updatedClient.getCode()).isEqualTo(savedClient.getCode());
		assertThat(updatedClient.getEmailId()).isNotEqualTo(clientDto.getEmailId());
		assertThat(updatedClient.getMobileNo()).isNotEqualTo(clientDto.getMobileNo());
		assertThat(updatedClient.getPanNo()).isNotEqualTo(clientDto.getPanNo());
		assertThat(updatedClient.getSalesTaxId()).isEqualTo(savedClient.getSalesTaxId());
		assertThat(updatedClient.getAddress()).isNotNull();
		assertThat(updatedClient.getAddress().getCountryId()).isEqualTo(savedClient.getAddress().getCountryId());
		assertThat(updatedClient.getAddress().getStateId()).isEqualTo(savedClient.getAddress().getStateId());
		assertThat(updatedClient.getAddress().getCityId()).isEqualTo(savedClient.getAddress().getCityId());
	}
	
	@Test
	void testUpdateShouldThroughExceptionIfPreviousApprovalIsPending() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		savedClient.setName("Test Client Update");
		savedClient.setPanNo("RF849161KM");
		savedClient.setEmailId("test.update@localhost.com");
		savedClient.setMobileNo("8465165151");
		assertThrows(InvalidDataException.class,() -> {
			clientService.update(savedClient);
		});
	}

	@Test
	void checkClientCodeIsUnique() {
		prepareMocking();
		clientService.save(clientDto);
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void checkClientNameIsRequired() {
		prepareMocking();
		clientDto.setName(null);
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void checkClientNameIsUnique() {
		prepareMocking();
		clientService.save(clientDto);
		clientDto.setCode("CIO789");
		clientDto.setName("test Client Name Unique");
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void checkMobileNoIsRequired() {
		prepareMocking();
		clientDto.setMobileNo(null);
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkMobileNoShouldNotBeMoreThan10Characters() {
		prepareMocking();
		clientDto.setMobileNo("8989595951651");
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void checkEmailIsRequired() {
		prepareMocking();
		clientDto.setEmailId(null);
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkEmailIsValid() {
		prepareMocking();
		clientDto.setEmailId("gsa1");
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkHeadquarterAddressIsRequired() {
		prepareMocking();
		clientDto.getAddress().setAddress(null);
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkCityIsRequired() {
		prepareMocking();
		clientDto.getAddress().setCityId(null);
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkCityIsValid() {
		prepareMocking();
		clientDto.getAddress().setCityId(0L);
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkCountryIsRequired() {
		prepareMocking();
		clientDto.getAddress().setCountryId(null);
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkCountryIsValid() {
		prepareMocking();
		clientDto.getAddress().setCountryId(0L);
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void checkPanNoIsRequired() {
		prepareMocking();
		clientDto.setPanNo(null);
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkPanNoShouldNotBeMoreThan16Characters() {
		prepareMocking();
		clientDto.setPanNo("CVGHJNKLKM891695165KM");
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void checkPanNoIsUnique() {
		prepareMocking();
		clientService.save(clientDto);
		clientDto.setCode("COP123");
		clientDto.setName("test pan is Unique");
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void checkGSTSalesTaxIsUnique() {
		prepareMocking();
		clientService.save(clientDto);
		clientDto.setCode("COT789");
		clientDto.setName("test taxId is unique");
		clientDto.setPanNo("DDFDF565GHJHJ");
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkSalesTaxIdShouldNotBeMoreThan16Characters() {
		prepareMocking();
		clientDto.setSalesTaxId("CGFV84516516651KMKMMK");
		assertThrows(ConstraintViolationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkGsaEmailsAreDuplicate() {
		prepareMocking();
		GsaDetailsDTO  gsaDetailsDto= new GsaDetailsDTO();
		gsaDetailsDto.setContactNo("9198786764");
		gsaDetailsDto.setEmail("gsa1@localhost.com");
		gsaDetailsDto.setName("gsa2");
		clientDto.getGsaDetails().add(gsaDetailsDto);
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}
	
	@Test
	void checkGsaEmailsAreAlreadyExists() {
		prepareMocking();
		clientService.save(clientDto);
		clientDto.setCode("COT789");
		clientDto.setName("test taxId is unique");
		clientDto.setPanNo("DDFDF565GHJHJ");
		clientDto.setSalesTaxId("YFVUY87451KM");
		List<String> emailIdsAlreadyPresent = Arrays.asList(clientDto.getGsaDetails().get(0).getEmail());
		when(userModuleAdaptor
				.checkEmailPresent(new LinkedList<>(Arrays.asList(clientDto.getGsaDetails().get(0).getEmail()))))
						.thenReturn(new EmailPresentDto(emailIdsAlreadyPresent));
		assertThrows(ValidationException.class, () -> {
			clientService.save(clientDto);
		});
	}

	@Test
	void testDelete() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		clientService.delete(savedClient.getId());
		Optional<ClientDTO> getClientById = clientQueryService.findOne(savedClient.getId());
		assertFalse(getClientById.isPresent());
	}
	
	private DeactivationDTO prepareDeactivationDTO() {
		DeactivationDTO deactivateDto = new DeactivationDTO();
		deactivateDto.setDeactivationWefDate(LocalDate.now());
		deactivateDto.setDeactivationReason("Data is not properly Filled");
		deactivateDto.setType("DEACTIVATE");
		return deactivateDto;
	}

	@Test
	void testDeactivate() throws Exception {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedClient.getId());
		ClientDTO dto = clientService.deactivate(deactivateDto);
		assertThat(dto).isNotNull();
		assertThat(dto.getDeactivateDtls().getDeactivationReason()).isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(dto.getDeactivateDtls().getDeactivationWefDate()).isEqualTo(deactivateDto.getDeactivationWefDate());
	}

	@Test
	void testDeactivateShouldThrowExceptionForWefIsPast() throws Exception {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedClient.getId());
		deactivateDto.setDeactivationWefDate(LocalDate.now().minusDays(50));
		assertThrows(ValidationException.class, () -> {
			clientService.deactivate(deactivateDto);
		});
	}
	
	@Test
	void testDeactivateShouldThrowExceptionForInvalidCompany() {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			clientService.deactivate(deactivateDto);
		});
	}

	private ReactivationDTO prepareActivationDTO() {
		ReactivationDTO activateDto = new ReactivationDTO();
		activateDto.setReactivationWefDate(LocalDate.now());
		activateDto.setReactivationReason("I want to reactivate this Company");
		activateDto.setType("REACTIVATE");
		return activateDto;
	}

	@Test
	void testReactivate() throws Exception {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		ReactivationDTO activateDto = prepareActivationDTO();
		activateDto.setReferenceId(savedClient.getId());
		ClientDTO dto = clientService.activate(activateDto);
		assertThat(dto).isNotNull();
		assertThat(dto.getReactivateDtls().getReactivationReason())
				.isEqualTo(activateDto.getReactivationReason());
		assertThat(dto.getReactivateDtls().getReactivationWefDate())
				.isEqualTo(activateDto.getReactivationWefDate());

	}

	@Test
	void testReactivateShouldThrowExceptionForWefIsPast() throws Exception {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		ReactivationDTO activateDto = prepareActivationDTO();
		activateDto.setReferenceId(savedClient.getId());
		activateDto.setReactivationWefDate(LocalDate.now().minusDays(50));
		assertThrows(ValidationException.class, () -> {
			clientService.activate(activateDto);
		});
	}
	
	@Test
	void testReactivateShouldThrowExceptionForInvalidCompany() {
		ReactivationDTO activateDto = prepareActivationDTO();
		activateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			clientService.activate(activateDto);
		});
	}


	public void prepareMocking() {
		List<CountryDTO> countryDTOs = new ArrayList<CountryDTO>();
		CountryDTO countryDTO = new CountryDTO();
		countryDTO.setId(1L);
		countryDTO.setName("India");
		countryDTO.setCode("IND");
		countryDTOs.add(countryDTO);
		List<DestinationDTO> destinationDTOs = new ArrayList<>();
		DestinationDTO destinationDTO = new DestinationDTO();
		destinationDTO.setId(1L);
		destinationDTO.setType(DestinationType.CITY);
		destinationDTO.setCode("MU");
		destinationDTO.setName("Mumbai");
		destinationDTOs.add(destinationDTO);
		when(masterModule.getCountriesByIds(new HashSet<>(Arrays.asList(1L)))).thenReturn(countryDTOs);
		when(masterModule.isStateMandatoryForGivenCountry(1L)).thenReturn(true);
		when(masterModule.getStatesByIds(new ArrayList<>(Arrays.asList(1L))))
				.thenReturn(new LinkedList<BaseDTO>(Arrays.asList(new BaseDTO(1L, "Maharashtra"))));
		when(masterModule.getDestinationsByIds(Arrays.asList(1L))).thenReturn(destinationDTOs);
		prepareGsaMocking();
	}

	public void prepareGsaMocking() {
		when(userModuleAdaptor
				.checkEmailPresent(new LinkedList<>(Arrays.asList(clientDto.getGsaDetails().get(0).getEmail()))))
						.thenReturn(new EmailPresentDto());
	}
}
