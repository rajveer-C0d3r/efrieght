package com.grtship.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.Client;
import com.grtship.client.domain.Company;
import com.grtship.client.serviceimpl.CompanyBranchServiceImplTest;
import com.grtship.client.serviceimpl.CompanyServiceImplTest;
import com.grtship.core.dto.AliasDTO;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class AliasServiceTest {
	
	@Autowired
	private AliasService aliasService;

	@Autowired
	private EntityManager em;
	
	private AliasDTO aliasDTO;
	
	private static AliasDTO prepareAliasDto(EntityManager em) {
		Client client = CompanyServiceImplTest.prepareClient();
		em.persist(client);
		em.flush();

		Company company = CompanyBranchServiceImplTest.prepareCompany(em, client);

		AliasDTO aliasDTO = new AliasDTO();
		aliasDTO.setLabel("Test Alias");
		aliasDTO.setCompanyAliasId(company.getId());
		return aliasDTO;
	}
	
	@BeforeEach
	void setUp() {
		aliasDTO=prepareAliasDto(em);
	}

	@Test
	void testSave() {
		AliasDTO savedAlias = aliasService.save(aliasDTO);
		System.out.println("Alias Dto" + aliasDTO);
		System.out.println("Alias Dto" + savedAlias);
		assertThat(savedAlias.getId()).isNotEqualTo(aliasDTO.getCompanyAliasId());
		assertThat(savedAlias.getId()).isEqualTo(savedAlias.getCompanyAliasId());
		assertThat(savedAlias.getLabel()).isEqualTo(aliasDTO.getLabel());
		assertThat(savedAlias.getId()).isNotNull();
	}
	
	@Test // mapper problem
	void testUpdate() {
		AliasDTO savedAlias = aliasService.save(aliasDTO);
		savedAlias.setLabel("Test Update Alias");
		savedAlias.setCompanyAliasId(aliasDTO.getCompanyAliasId());
		AliasDTO updatedAlias = aliasService.save(savedAlias);
		assertThat(updatedAlias.getLabel()).isNotEqualTo(aliasDTO.getLabel());
		assertThat(updatedAlias.getId()).isNotNull();
		assertThat(updatedAlias.getId()).isEqualTo(savedAlias.getId());
	}

	@Test
	void testFindAll() {
		aliasService.save(aliasDTO);
		List<AliasDTO> aliasDTOs=aliasService.findAll();
		assertThat(aliasDTOs).isNotEmpty();
		assertThat(aliasDTOs).allMatch(alias -> alias.getCompanyAliasId()!=null);
		assertThat(aliasDTOs).allMatch(alias -> alias.getLabel()!=null);
		assertThat(aliasDTOs).allMatch(alias -> alias.getId()!=null);
	}

	@Test
	void testFindOne() {
		AliasDTO savedAlias=aliasService.save(aliasDTO);
		AliasDTO getAliasById=aliasService.findOne(savedAlias.getId()).get();
		assertThat(getAliasById.getCompanyAliasId()).isEqualTo(savedAlias.getCompanyAliasId());
		assertThat(getAliasById.getLabel()).isEqualTo(savedAlias.getLabel());
		assertThat(getAliasById.getId()).isNotNull();
		assertThat(getAliasById.getId()).isEqualTo(savedAlias.getId());
	}

	@Test
	void testDelete() {
		AliasDTO savedAlias=aliasService.save(aliasDTO);
		aliasService.delete(savedAlias.getId());
		Optional<AliasDTO> getAliasById=aliasService.findOne(savedAlias.getId());
		assertFalse(getAliasById.isPresent());
	}
}
