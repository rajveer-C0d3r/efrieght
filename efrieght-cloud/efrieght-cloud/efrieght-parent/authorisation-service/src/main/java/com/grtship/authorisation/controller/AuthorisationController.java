package com.grtship.authorisation.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.authorisation.service.AuthorisationService;
import com.grtship.core.dto.ApprovedRequestDataDTO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/approval")
@AllArgsConstructor
public class AuthorisationController {
	
	private final AuthorisationService authorisationService;
    
	@PostMapping
	public ResponseEntity<Boolean> approvalData(@Valid @RequestBody ApprovedRequestDataDTO approvedRequestDataDTO){
		log.debug("REST request to save approval response : {}", approvedRequestDataDTO);
		Boolean saved=authorisationService.saveApprovedData(approvedRequestDataDTO);
		return ResponseEntity.ok(saved);
	}
}
