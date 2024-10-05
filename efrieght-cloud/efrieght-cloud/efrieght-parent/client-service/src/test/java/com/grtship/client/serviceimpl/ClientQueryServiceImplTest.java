package com.grtship.client.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.adaptor.UserModuleAdaptor;
import com.grtship.client.criteria.ClientCriteria;
import com.grtship.client.feignclient.MasterModule;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.service.ClientService;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.DomainStatus;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class ClientQueryServiceImplTest {

	@Autowired private ClientService clientService;

	@Autowired private ClientQueryServiceImpl clientQueryService;
	
	@MockBean private MasterModule masterModule;

	@MockBean private OAuthModule authModule;
	
	@MockBean private UserModuleAdaptor userModuleAdaptor;

	private ClientDTO clientDto;
	
	private ClientCriteria clientCriteria;
	
	@BeforeEach
	void setUp() {
		clientDto=ClientServiceImplTest.prepareClientDto();
	}

	@Test
	void testFindByCriteriaClientCriteriaName() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		clientCriteria=new ClientCriteria();
		clientCriteria.setName(savedClient.getName());
		List<ClientDTO> clientDTOs=clientQueryService.findByCriteria(clientCriteria);
		assertThat(clientDTOs).isNotEmpty();
		assertThat(clientDTOs).allMatch(client -> client.getId()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getName().equals(clientCriteria.getName()));
		assertThat(clientDTOs).allMatch(client -> client.getCode()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getMobileNo()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getEmailId()!=null);
	}
	
	@Test
	void testFindByCriteriaClientCriteriaCode() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		clientCriteria=new ClientCriteria();
		clientCriteria.setCode(savedClient.getCode());
		List<ClientDTO> clientDTOs=clientQueryService.findByCriteria(clientCriteria);
		assertThat(clientDTOs).isNotEmpty();
		assertThat(clientDTOs).allMatch(client -> client.getId()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getName()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getCode().equals(clientCriteria.getCode()));
		assertThat(clientDTOs).allMatch(client -> client.getMobileNo()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getEmailId()!=null);
	}
	
	@Test
	void testFindByCriteriaClientCriteriaStatus() {
		prepareMocking();
		clientService.save(clientDto);
		clientCriteria = new ClientCriteria();
		clientCriteria.setStatus("PENDING");
		List<ClientDTO> clientDTOs = clientQueryService.findByCriteria(clientCriteria);
		assertThat(clientDTOs).isNotEmpty();
		assertThat(clientDTOs).allMatch(client -> client.getId() != null);
		assertThat(clientDTOs).allMatch(client -> client.getName() != null);
		assertThat(clientDTOs).allMatch(client -> client.getCode() != null);
		assertThat(clientDTOs).allMatch(client -> client.getMobileNo() != null);
		assertThat(clientDTOs).allMatch(client -> client.getEmailId() != null);
		assertThat(clientDTOs)
				.allMatch(client -> client.getStatus().equals(DomainStatus.valueOf(clientCriteria.getStatus())));
	}
	
	@Test
	void testFindByCriteriaClientCriteriaId() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		clientCriteria=new ClientCriteria();
		clientCriteria.setId(savedClient.getId());
		List<ClientDTO> clientDTOs=clientQueryService.findByCriteria(clientCriteria);
		assertThat(clientDTOs).isNotEmpty();
		assertThat(clientDTOs).allMatch(client -> client.getId()==clientCriteria.getId());
		assertThat(clientDTOs).allMatch(client -> client.getName()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getCode()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getMobileNo()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getEmailId()!=null);
	}
	
	@Test
	void testFindByCriteriaClientCriteriaIds() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		clientCriteria=new ClientCriteria();
		clientCriteria.setIds(new LinkedList<>(Arrays.asList(savedClient.getId())));
		List<ClientDTO> clientDTOs=clientQueryService.findByCriteria(clientCriteria);
		assertThat(clientDTOs).isNotEmpty();
		assertThat(clientDTOs).allMatch(client -> client.getId()==clientCriteria.getIds().get(0));
		assertThat(clientDTOs).allMatch(client -> client.getName()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getCode()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getMobileNo()!=null);
		assertThat(clientDTOs).allMatch(client -> client.getEmailId()!=null);
	}

	@Test
	void testFindByCriteriaClientCriteriaPageable() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		clientCriteria=new ClientCriteria();
		clientCriteria.setName(savedClient.getName());
		Page<ClientDTO> clientDTOs=clientQueryService.findByCriteria(clientCriteria,PageRequest.of(0, 1));
		assertThat(clientDTOs.getSize()).isEqualTo(20);
		assertThat(clientDTOs.getContent()).allMatch(client -> client.getId()!=null);
		assertThat(clientDTOs.getContent()).allMatch(client -> client.getName().equals(clientCriteria.getName()));
		assertThat(clientDTOs.getContent()).allMatch(client -> client.getCode()!=null);
		assertThat(clientDTOs.getContent()).allMatch(client -> client.getMobileNo()!=null);
		assertThat(clientDTOs.getContent()).allMatch(client -> client.getEmailId()!=null);
	}

	@Test
	void testCountByCriteria() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		clientCriteria=new ClientCriteria();
		clientCriteria.setName(savedClient.getName());
		Long count=clientQueryService.countByCriteria(clientCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}

	@Test
	void testFindOne() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		ClientDTO getClientById = clientQueryService.findOne(savedClient.getId()).get();
		assertThat(getClientById.getId()).isNotNull();
		assertThat(getClientById.getId()).isEqualTo(savedClient.getId());
		assertThat(getClientById.getName()).isEqualTo(savedClient.getName());
		assertThat(getClientById.getCode()).isEqualTo(savedClient.getCode());
		assertThat(getClientById.getEmailId()).isEqualTo(savedClient.getEmailId());
		assertThat(getClientById.getMobileNo()).isEqualTo(savedClient.getMobileNo());
		assertThat(getClientById.getPanNo()).isEqualTo(savedClient.getPanNo());
		assertThat(getClientById.getSalesTaxId()).isEqualTo(savedClient.getSalesTaxId());
		assertThat(getClientById.getAddress()).isNotNull();
		assertThat(getClientById.getAddress().getCountryId()).isEqualTo(savedClient.getAddress().getCountryId());
		assertThat(getClientById.getAddress().getStateId()).isEqualTo(savedClient.getAddress().getStateId());
		assertThat(getClientById.getAddress().getCityId()).isEqualTo(savedClient.getAddress().getCityId());
	}

	@Test
	void testGetByIds() {
		prepareMocking();
		ClientDTO savedClient = clientService.save(clientDto);
		List<Long> clientIds = new LinkedList<>(Arrays.asList(savedClient.getId()));
		List<ClientDTO> clientDTOs = clientQueryService.getByIds(clientIds);
		assertThat(clientDTOs).isNotEmpty();
		assertThat(clientDTOs).allMatch(client -> client.getId() == clientIds.get(0));
		assertThat(clientDTOs).allMatch(client -> client.getName() != null);
		assertThat(clientDTOs).allMatch(client -> client.getCode() != null);
		assertThat(clientDTOs).allMatch(client -> client.getMobileNo() != null);
		assertThat(clientDTOs).allMatch(client -> client.getEmailId() != null);
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
