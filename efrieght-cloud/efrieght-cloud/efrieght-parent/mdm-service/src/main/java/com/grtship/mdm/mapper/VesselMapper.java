package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.VesselCreationRequest;
import com.grtship.core.dto.VesselDTO;
import com.grtship.core.dto.VesselUpdateRequest;
import com.grtship.mdm.domain.Vessel;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Vessel} and its DTO {@link VesselDTO}.
 */
@Mapper(componentModel = "spring")
@Component
public interface VesselMapper extends EntityMapper<VesselDTO, Vessel> {

	@Mapping(source = "operator.id", target = "operatorId")
	@Mapping(source = "deactivate.wefDate", target = "deactivationDetails.deactivationWefDate")
	@Mapping(source = "deactivate.deactivatedDate", target = "deactivationDetails.deactivatedDate")
	@Mapping(source = "deactivate.reason", target = "deactivationDetails.deactivationReason")
	@Mapping(source = "reactivate.wefDate", target = "reactivationDetails.reactivationWefDate")
	@Mapping(source = "reactivate.reactivatedDate", target = "reactivationDetails.reactivatedDate")
	@Mapping(source = "operator.name", target = "operatorName")
	@Mapping(source = "active", target = "active", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(target = "submittedForApproval", defaultExpression = "java(Boolean.FALSE)")
	VesselDTO toDto(Vessel vessel);

	@Mapping(source = "deactivationDetails.deactivationWefDate", target = "deactivate.wefDate")
	@Mapping(source = "deactivationDetails.deactivatedDate", target = "deactivate.deactivatedDate")
	@Mapping(source = "deactivationDetails.deactivationReason", target = "deactivate.reason")
	@Mapping(source = "reactivationDetails.reactivationWefDate", target = "reactivate.wefDate")
	@Mapping(source = "reactivationDetails.reactivatedDate", target = "reactivate.reactivatedDate")
	@Mapping(source = "operatorId", target = "operator.id")
	@Mapping(target = "active", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(target = "submittedForApproval", defaultExpression = "java(Boolean.FALSE)")
	Vessel toEntity(VesselDTO vesselDTO);

	default Vessel fromId(Long id) {
		if (id == null) {
			return null;
		}
		Vessel vessel = new Vessel();
		vessel.setId(id);
		return vessel;
	}

	@Mapping(source = "operatorId", target = "operator.id")
	@Mapping(target = "active", constant = "false")
	@Mapping(target = "status", constant = "PENDING")
	@Mapping(target = "submittedForApproval", constant = "true")
	Vessel toEntity(VesselCreationRequest vesselDTO);

	@Mapping(source = "operatorId", target = "operator.id")
	@Mapping(target = "active", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(source = "deactivationDetails.deactivationWefDate", target = "deactivate.wefDate")
	@Mapping(source = "deactivationDetails.deactivatedDate", target = "deactivate.deactivatedDate")
	@Mapping(source = "deactivationDetails.deactivationReason", target = "deactivate.reason")
	@Mapping(source = "reactivationDetails.reactivationWefDate", target = "reactivate.wefDate")
	@Mapping(source = "reactivationDetails.reactivatedDate", target = "reactivate.reactivatedDate")
	@Mapping(target = "submittedForApproval", constant = "true")
	@Mapping(target = "status", constant = "PENDING")
	Vessel toEntity(VesselUpdateRequest vesselDTO);

}
