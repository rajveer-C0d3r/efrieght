package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.GstDTO;
import com.grtship.mdm.domain.Gst;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Gst} and its DTO {@link GstDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface GstMapper extends EntityMapper<GstDTO, Gst> {

	default Gst fromId(Long id) {
		if (id == null) {
			return null;
		}
		Gst gst = new Gst();
		gst.setId(id);
		return gst;
	}
}
