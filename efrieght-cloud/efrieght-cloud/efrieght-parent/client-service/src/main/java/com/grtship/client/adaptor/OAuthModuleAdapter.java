package com.grtship.client.adaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.grtship.client.feignclient.OAuthModule;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.UserAccessDTO;
import com.grtship.core.enumeration.UserType;

@Component
public class OAuthModuleAdapter {

	@Autowired
	private OAuthModule oAuthModule;

	public ResponseEntity<Void> generateGsaUsers(List<GsaDetailsDTO> gsaUserDetails) {
		return oAuthModule.generateGsaUsers(gsaUserDetails);
	}

	public ResponseEntity<List<UserAccessDTO>> getCurrentUserCompanyBranchIds(Long userId) {
		return oAuthModule.getCurrentUserCompanyBranchIds(userId);
	}

	public ResponseEntity<Void> generateCsaUsers(List<CsaDetailsDTO> csaUserDetails) {
		return oAuthModule.generateCsaUsers(csaUserDetails);
	}

	public Map<Long, List<GsaDetailsDTO>> getGsaUsersByClientIdList(List<Long> idList) {
		return oAuthModule.getGsaUsersByClientIdList(idList);
	}

	public Map<Long, List<CsaDetailsDTO>> getCsaUsersByCompanyIdList(List<Long> idList) {
		return oAuthModule.getCsaUsersByCompanyIdList(idList);
	}

	public Optional<UserType> getUserType(String login) {
		return oAuthModule.getUserType(login);
	}

}
