package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.grtship.core.dto.AddressDTO;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.mdm.domain.Address;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.domain.Destination;
import com.grtship.mdm.domain.Sector;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class AddressServiceImplTest {
	
	@Autowired private EntityManager em;
		
	@Autowired private AddressServiceImpl addressService;

	private AddressDTO addressDTO;
	
	public static AddressDTO setUp(EntityManager em) {
		Currency currency = new Currency();
		currency.setCode("IND");
		currency.setName("Rupee");
		em.persist(currency);

		Sector sector = new Sector();
		sector.setCode("Sec");
		sector.setName("Sector1");
		em.persist(sector);

		Country country = new Country();
		country.setName("India");
		country.setCode("IND");
		country.setGstOrVatType(GstVatType.GST);
		country.setIsStateMandatory(Boolean.FALSE);
		country.setCurrency(currency);
		country.setSector(sector);
		em.persist(country);

		Destination destination = new Destination();
		destination.setCode("Des");
		destination.setName("Mumbai");
		destination.setType(DestinationType.CITY);
		destination.setCountry(country);
		em.persist(destination);

		Set<String> landmarks = new HashSet<>();
		landmarks.add("Landmark");
		
		AddressDTO addressDTO=new AddressDTO();
		addressDTO.setAddress("Andheri East Mumbai");
        addressDTO.setCountryId(country.getId());
        addressDTO.setCityId(destination.getId());
        addressDTO.setLandMarkSet(landmarks);
        addressDTO.setLocation("Kondivita Road");
        addressDTO.setPincode("400059");
		return addressDTO;
	}

	@BeforeEach
	void initTest() {
		addressDTO = setUp(em);
	}

	@Test
	void testSave() {
		AddressDTO savedAddressDTO = addressService.save(addressDTO);
		assertThat(savedAddressDTO).isNotNull();
		assertThat(savedAddressDTO.getAddress()).isEqualTo(addressDTO.getAddress());
		assertThat(savedAddressDTO.getCityId()).isEqualTo(addressDTO.getCityId());
		assertThat(savedAddressDTO.getCountryId()).isEqualTo(addressDTO.getCountryId());
		assertThat(savedAddressDTO.getLocation()).isEqualTo(addressDTO.getLocation());
		assertThat(savedAddressDTO.getPincode()).isEqualTo(addressDTO.getPincode());
	}
	
	@Test
	void checkCityIsRequired() {
		addressDTO.setCityId(null);
		assertThrows(ConstraintViolationException.class,() -> {
			addressService.save(addressDTO);
		});
	}
	
	@Test
	public void shouldUpdateAddress() {
		AddressDTO savedAddress= addressService.save(addressDTO);
		savedAddress.setAddress("Marol Andheri East");
		savedAddress.setPincode("456152");
		savedAddress.setLocation("Marol Maroshi road");
		AddressDTO updatedAddress = addressService.save(savedAddress);
		assertThat(updatedAddress).isNotNull();
		assertThat(updatedAddress.getAddress()).isNotEqualTo(addressDTO.getAddress());
		assertThat(updatedAddress.getPincode()).isNotEqualTo(addressDTO.getPincode());
		assertThat(updatedAddress.getLocation()).isNotEqualTo(addressDTO.getLocation());
		assertThat(updatedAddress.getCityId()).isEqualTo(addressDTO.getCityId());
		assertThat(updatedAddress.getCountryId()).isEqualTo(addressDTO.getCountryId());
	}
	
	@Test
	void testFindAll() {
		addressService.save(addressDTO);
		List<AddressDTO> addressDTOs = addressService.findAll();
		assertThat(addressDTOs).isNotEmpty();
		assertThat(addressDTOs).allMatch(addObj -> addObj.getAddress()!=null);
		assertThat(addressDTOs).allMatch(addObj -> addObj.getId()!=null);
	}

	@Test
	void testFindOne() {
		AddressDTO savedAddress = addressService.save(addressDTO);
		AddressDTO getAddtressById = addressService.findOne(savedAddress.getId()).get();
		assertThat(getAddtressById).isNotNull();
		assertThat(getAddtressById.getId()).isEqualTo(getAddtressById.getId());
		assertThat(getAddtressById.getLocation()).isEqualTo(getAddtressById.getLocation());
		assertThat(getAddtressById.getPincode()).isEqualTo(getAddtressById.getPincode());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<AddressDTO> getAddtressById = addressService.findOne(0L);
		assertFalse(getAddtressById.isPresent());
	}

	@Test
	void testDelete() {
		AddressDTO savedAddress = addressService.save(addressDTO);
		addressService.delete(savedAddress.getId());
		Optional<AddressDTO> getAddtressById = addressService.findOne(savedAddress.getId());
		assertFalse(getAddtressById.isPresent());
	}

	@Test
	void testFindAllById() {
		AddressDTO savedAddress = addressService.save(addressDTO);
		List<Long> addressIdList = Arrays.asList(savedAddress.getId());
		List<Address> findAllById = addressService.findAllById(addressIdList);
		assertThat(findAllById).isNotEmpty();
		assertThat(findAllById).allMatch(addObj -> addObj.getFullAddress() != null);
		assertThat(findAllById).allMatch(addObj -> addObj.getId().equals(addressIdList.get(0)));
	}
	
	@Test
	void testFindAllByIdForInvalidId() {
		List<Long> addressIdList = Arrays.asList(0L);
		List<Address> findAllById = addressService.findAllById(addressIdList);
		assertThat(findAllById).isEmpty();
	}

	@Test
	void testGetAllAddressesByIdList() {
		AddressDTO savedAddress = addressService.save(addressDTO);
		List<Long> addressIdList = Arrays.asList(savedAddress.getId());
		Map<Long, AddressDTO> allAddressesByIdList=addressService.getAllAddressesByIdList(addressIdList);
		assertThat(allAddressesByIdList).isNotEmpty();
		assertThat(allAddressesByIdList.get(addressIdList.get(0))).isNotNull();
	}
	
	@Test
	void testGetAllAddressesByIdListForInvalidId() {
		List<Long> addressIdList = Arrays.asList(0l);
		Map<Long, AddressDTO> allAddressesByIdList=addressService.getAllAddressesByIdList(addressIdList);
		assertThat(allAddressesByIdList).isEmpty();;
	}

}
