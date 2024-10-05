package com.grtship.authorisation.service;

import com.grtship.core.dto.ApprovedRequestDataDTO;

public interface AuthorisationService {
   Boolean saveApprovedData(ApprovedRequestDataDTO approvedRequestDataDTO);  
}
