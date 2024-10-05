package com.grtship.mdm.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.core.dto.KeyLabelBeanDTO;
import com.grtship.core.enumeration.AccountType;
import com.grtship.core.enumeration.ActionType;
import com.grtship.core.enumeration.CompanyType;
import com.grtship.core.enumeration.DebitCredit;
import com.grtship.core.enumeration.DepartmentType;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.DocumentType;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.Domicile;
import com.grtship.core.enumeration.EntityCriteria;
import com.grtship.core.enumeration.EntityGstType;
import com.grtship.core.enumeration.EntityLevel;
import com.grtship.core.enumeration.EntityType;
import com.grtship.core.enumeration.GstType;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.core.enumeration.NatureOfGroup;
import com.grtship.core.enumeration.PortType;
import com.grtship.core.enumeration.TdsExemption;
import com.grtship.mdm.service.StaticListService;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Sector}.
 */

@RestController
@RequestMapping("/api")
public class StaticListController {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private StaticListService staticListService;

	/**
	 * service to get static list of companytType
	 */
	@GetMapping("/getListOfCompanytype")
	public List<KeyLabelBeanDTO> getListOfCompanytype() {
		return staticListService.getList(CompanyType.class);
	}

	/**
	 * service to get static list of domicile
	 */
	@GetMapping("/getListOfDomicile")
	public List<KeyLabelBeanDTO> getListOfDomicile() {
		return staticListService.getList(Domicile.class);
	}

	/**
	 * service to get list of entityGstType
	 */
	@GetMapping("/getListOfEntityGstType")
	public List<KeyLabelBeanDTO> getListOfEntityGstType() {
		return staticListService.getList(EntityGstType.class);
	}

	/**
	 * service to get list of entityLevel
	 */
	@GetMapping("/getListOfEntityLevel")
	public List<KeyLabelBeanDTO> getListOfEntityLevel() {
		return staticListService.getList(EntityLevel.class);
	}

	/**
	 * service to get list of tdsexemption
	 */
	@GetMapping("/getListOfTdsExemption")
	public List<KeyLabelBeanDTO> getListOfTdsExemption() {
		return staticListService.getList(TdsExemption.class);
	}

	/**
	 * service to get list of GstVatType
	 */
	@GetMapping("/getListOfGstVatType")
	public List<KeyLabelBeanDTO> getListOfGstVatType() {
		return staticListService.getList(GstVatType.class);
	}

	/**
	 * service to get list of DocumentType
	 */
	@GetMapping("/getListOfDocumentType")
	public List<KeyLabelBeanDTO> getListOfDocumentType() {
		return staticListService.getList(DocumentType.class);
	}

	@GetMapping("destination/destinationTypeList")
	public List<KeyLabelBeanDTO> getDesintationTypeList() {
		return staticListService.getList(DestinationType.class);
	}

	@GetMapping("destinations/portTypeList")
	public List<KeyLabelBeanDTO> getPortTypeList() {
		return staticListService.getList(PortType.class);
	}

	@GetMapping("/getListOfDepartmentType")
	public List<KeyLabelBeanDTO> getListOfDepartmentType() {
		return staticListService.getList(DepartmentType.class);
	}

	@GetMapping("/getListOfNatureOfGroup")
	public List<KeyLabelBeanDTO> getListOfNatureOfGroup() {
		return staticListService.getList(NatureOfGroup.class);
	}

	@GetMapping("/getGstTypeList")
	public List<KeyLabelBeanDTO> getGstTypeList() {
		return staticListService.getList(GstType.class);
	}

	@GetMapping("/getAccountTypeList")
	public List<KeyLabelBeanDTO> getAccountTypeList() {
		return staticListService.getList(AccountType.class);
	}

	@GetMapping("/getStatusList")
	public List<KeyLabelBeanDTO> getAllStatus() {
		return staticListService.getList(DomainStatus.class);
	}
	
	@GetMapping("/getEntityCriteria")
	public List<KeyLabelBeanDTO> getEntityCriteria() {
		return staticListService.getList(EntityCriteria.class);
	}
	
	@GetMapping("/getEntityTypeList")
	public List<String> getEntityTypeList() {
		return Stream.of(EntityType.values())
                .map(EntityType::name)
                .collect(Collectors.toList());
	}
		
	@GetMapping("/getDebitCreditList")
	public List<KeyLabelBeanDTO> getDebitCreditList(){
		return staticListService.getList(DebitCredit.class);
	}
	
	@GetMapping("/getActionTypeList")
	public List<KeyLabelBeanDTO> getActionTypeList(){
		return staticListService.getList(ActionType.class);
	}
}
