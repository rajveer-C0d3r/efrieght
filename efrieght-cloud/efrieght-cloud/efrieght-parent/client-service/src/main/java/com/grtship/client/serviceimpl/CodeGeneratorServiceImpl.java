package com.grtship.client.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.Client;
import com.grtship.client.repository.ClientRepository;
import com.grtship.client.service.CodeGeneratorService;

@Service
@Transactional
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

	private static final String C = "C";
	private static final String CLIENT = "Client";
	private final Logger log = LoggerFactory.getLogger(CodeGeneratorServiceImpl.class);

	@Autowired
	private ClientRepository clientRepository;

	@Override
	public String generateCode(String name) {
		switch (name) {
		case CLIENT:
			log.debug("In Client generate Code");
			return generateClientCode();

		default:
			return "";
		}
	}

	private String generateClientCode() {
		Long clientCount = clientRepository.count();
		return getClientCode(clientCount);
	}

	private String getClientCode(Long clientCount) {
		Long totalClients = clientCount + 1;
		String code = C;
		code += String.format("%04d", totalClients);
		List<Client> clients = clientRepository.findByCode(code);
		return (!clients.isEmpty() && clients.get(0) != null) ? getClientCode(totalClients) : code;
	}

}
