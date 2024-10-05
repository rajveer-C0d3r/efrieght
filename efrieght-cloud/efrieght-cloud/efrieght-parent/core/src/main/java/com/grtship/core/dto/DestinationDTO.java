package com.grtship.core.dto;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.PortType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.Destination} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class DestinationDTO extends ClientAuditableEntityDTO {

	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;
	
	@NotNull(message = "Destination Code Is Mandatory, Please Enter Destination Code.")
	@NotEmpty(message = "Destination Code Is Mandatory, Please Enter Destination Code.")
	private String code;

	@NotNull(message = "Destination Name Is Mandatory, Please Enter Destination Name.")
	@NotEmpty(message = "Destination Name Is Mandatory, Please Enter Destination Name.")
	private String name;

	@NotNull(message = "Destination Type Is Mandatory, Please Select Destination Type.")
	private DestinationType type;
	private PortType portType;

	private String iataAirportCode;

	private Boolean isAdminCreated;
	
	private Boolean isReworkingPort;

	private Long portId;
	private String portName;

	private Long cityId;
	private String cityName;

	private Long countryId;
	private String countryName;

	private Long stateId;
	private String stateName;
	
	private String sectorName;

	@EnableAuditLevel(idOnly = true)
	private Set<ObjectAliasDTO> aliases;

	@EnableAuditLevel(idOnly = true)
	private Set<BaseDTO> terminals;
	
	private Boolean isPublic;
	
	public void setIsReworkingPort(Boolean isReworkingPort) {
		if(isReworkingPort==null) {
			this.isReworkingPort = Boolean.FALSE;
		}
		this.isReworkingPort  = isReworkingPort;
	}

}
