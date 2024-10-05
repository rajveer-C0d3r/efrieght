package com.grtship.client.mapper;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.client.adaptor.MasterModuleAdapter;
import com.grtship.client.domain.Client;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.GsaDetailsDTO;

/**
 * Mapper for the entity {@link Client} and its DTO {@link ClientDTO}.
 */
@Mapper(componentModel = "spring", uses = {AddressMapper.class})
@Component
public abstract class ClientMapper implements EntityMapper<ClientDTO, Client> {
	
	
	private static final String CLIENT = "Client";

	@Autowired
	private OAuthModule oAutheClient;
	
	@Autowired
	public MasterModuleAdapter masterModuleAdapter;
	
    @Mapping(source = "address.city", target = "address.cityId")
    @Mapping(source = "address.state", target = "address.stateId")
    public abstract ClientDTO toDto(Client client);
    
    @Override
    public List<ClientDTO> toDto(List<Client> clients) {
		if (!CollectionUtils.isEmpty(clients)) {
			List<ClientDTO> clientDtos = clients.stream().filter(client -> client.getId() != null).map(this::toDto)
					.collect(Collectors.toList());
			prepareClientGsaDetails(clientDtos);
			return clientDtos;
		}
    	return Collections.emptyList();
    }

	private void prepareClientGsaDetails(List<ClientDTO> clientDtos){
		List<Long> clientIds = clientDtos.stream().filter(clientDto -> clientDto.getId() != null)
				.map(ClientDTO::getId).collect(Collectors.toList());
		Map<Long, List<GsaDetailsDTO>> gsaUserMap = oAutheClient.getGsaUsersByClientIdList(clientIds);
		for(ClientDTO clientDto :clientDtos) {
			clientDto.setGsaDetails(gsaUserMap.get(clientDto.getId()));
		}
	}

    @Mapping(source = "address.cityId", target = "address.city")
    @Mapping(source = "address.stateId", target = "address.state")
    @Mapping(target = "submittedForApproval", constant = "true")
    @Mapping(target = "code", expression ="java(this.setCode(clientDto.getCode(),clientDto.getId()))")
    public abstract Client toEntity(ClientDTO clientDto);
    
    public String setCode(String code, Long id) {
		if (StringUtils.isEmpty(code) && id==null)
			return masterModuleAdapter.generateCode(CLIENT, null);
		return code;
	}

    public Client fromId(Long id) {
        if (id == null) {
            return null;
        }
        Client client = new Client();
        client.setId(id);
        return client;
    }
}
