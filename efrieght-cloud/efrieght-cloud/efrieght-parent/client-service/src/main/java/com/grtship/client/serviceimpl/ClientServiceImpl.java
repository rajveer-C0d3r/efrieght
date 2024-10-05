package com.grtship.client.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.grtship.client.domain.Client;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.mapper.ClientMapper;
import com.grtship.client.mapper.DomainDeactivateMapper;
import com.grtship.client.mapper.DomainReactivateMapper;
import com.grtship.client.repository.ClientRepository;
import com.grtship.client.service.ClientService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;

/**
 * Service Implementation for managing {@link Client}.
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

	private static final String INVALID_ID = "Invalid Id";

	private static final String CLIENT_NOT_FOUND_FOR_THE_GIVEN_ID = "Client Not Found For The Given Id.";

	private final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientMapper clientMapper;

	@Autowired
	private OAuthModule oAutheClient;

	@Autowired
	private DomainDeactivateMapper domainDeactivateMapper;

	@Autowired
	private DomainReactivateMapper domainReactivateMapper;
	
	@Autowired
	private KafkaProducerService kafkaProducerService;

	@Override
	@Transactional
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.CLIENT)
	@Validate(validator = "clientValidatorServiceImpl", action = "save")
	public ClientDTO save(ClientDTO clientDto) {
		log.debug("Request to save Client : {}", clientDto);
		Client client = clientMapper.toEntity(clientDto);
		client.setStatus(DomainStatus.PENDING);
		Client savedClient = clientRepository.save(client);
		saveGsaDetails(clientDto, savedClient);
		return clientMapper.toDto(client);
	}

	@Override
	@Transactional
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.CLIENT)
	public ClientDTO update(ClientDTO clientDto) {

		log.debug("Request to update Client : {}", clientDto);
		Optional<Client> optionalClient = clientRepository.findById(clientDto.getId());
		if (optionalClient.isPresent()) {
			clientDto.setCode(optionalClient.get().getCode());
			Client client = clientMapper.toEntity(clientDto);
			Client savedClient = clientRepository.save(client);
			updateGsaDetails(clientDto, savedClient);
			return clientMapper.toDto(client);
		} else
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, INVALID_ID);
	
	}
	private void updateGsaDetails(ClientDTO clientDto, Client savedClient) {
		List<GsaDetailsDTO> gsaUserDetails = clientDto.getGsaDetails();
		if (!CollectionUtils.isEmpty(gsaUserDetails)) {
			gsaUserDetails = gsaUserDetails.stream().filter(obj -> obj.getId() == null).collect(Collectors.toList());
			gsaUserDetails.forEach(gsaDetailsDto -> gsaDetailsDto.setClientId(savedClient.getId()));
			oAutheClient.generateGsaUsers(gsaUserDetails);
		}
	}

	private void saveGsaDetails(ClientDTO clientDto, Client savedClient) {
		List<GsaDetailsDTO> gsaUserDetails = clientDto.getGsaDetails();
		if (!CollectionUtils.isEmpty(gsaUserDetails)) {
			gsaUserDetails.forEach(gsaDetailsDto -> gsaDetailsDto.setClientId(savedClient.getId()));
			kafkaProducerService.sendMessage(KafkaTopicConstant.KAFKA_GSA_USER_CREATION_TOPIC,
					new Gson().toJson(gsaUserDetails));
		}
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.CLIENT)
	public void delete(Long id) {
		log.debug("Request to delete Client : {}", id);
		clientRepository.deleteById(id);
	}

	@Override
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.CLIENT)
	@Validate(validator = "clientValidatorServiceImpl", action = "deactivate")
	public ClientDTO deactivate(DeactivationDTO deactivateDto) {
		Optional<Client> clientById = clientRepository.findById(deactivateDto.getReferenceId());
		if (!clientById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, CLIENT_NOT_FOUND_FOR_THE_GIVEN_ID);
		Client client = clientById.get();
		client.setDeactivateDtls(domainDeactivateMapper.toEntity(deactivateDto));
		client.setSubmittedForApproval(Boolean.TRUE);
		client = clientRepository.save(client);
		return clientMapper.toDto(client);
	}

	@Override
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.CLIENT)
	@Validate(validator = "clientValidatorServiceImpl", action = "reactivate")
	public ClientDTO activate(ReactivationDTO activateDto) {
		Optional<Client> clientById = clientRepository.findById(activateDto.getReferenceId());
		if (!clientById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, CLIENT_NOT_FOUND_FOR_THE_GIVEN_ID);
		Client client = clientById.get();
		client.setReactivateDtls(domainReactivateMapper.toEntity(activateDto));
		client.setSubmittedForApproval(Boolean.TRUE);
		client = clientRepository.save(client);
		return clientMapper.toDto(client);
	}
}
