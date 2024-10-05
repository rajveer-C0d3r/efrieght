package com.grtship.client.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.Client;
import com.grtship.client.repository.ClientRepository;
import com.grtship.client.service.CodeGeneratorService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class CodeGeneratorServiceImplTest {
	
	private static final String C = "C";
	private static final String CLIENT = "Client";
	
	@Autowired CodeGeneratorService codeGeneratorService;
	
	@Autowired ClientRepository clientRepository;

	@Test
	void testGenerateCode() {
		String code = codeGeneratorService.generateCode(CLIENT);
		List<Client> clients = clientRepository.findByCode(code);
		String codeAlreadyPresent = !clients.isEmpty() && clients.get(0) != null ? clients.get(0).getCode() : null;
		assertThat(code).isNotEmpty();
		assertThat(code).isNotEqualTo(codeAlreadyPresent);
		assertTrue(code.startsWith(C));
	}
	
	@Test
	void testGenerateCodeShouldReturnEmptyStringForInvalidName() {
		String code = codeGeneratorService.generateCode("COMPANY");
		assertThat(code).isEmpty();
	}
}
