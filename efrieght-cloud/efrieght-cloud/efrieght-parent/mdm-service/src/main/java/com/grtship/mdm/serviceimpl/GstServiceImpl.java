package com.grtship.mdm.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.GstDTO;
import com.grtship.mdm.domain.Gst;
import com.grtship.mdm.mapper.GstMapper;
import com.grtship.mdm.repository.GstRepository;
import com.grtship.mdm.service.GstService;

/**
 * Service Implementation for managing {@link Gst}.
 */
@Service
@Transactional
public class GstServiceImpl implements GstService{

	private final Logger log = LoggerFactory.getLogger(GstServiceImpl.class);

	@Autowired
	private GstRepository gstRepository;

	@Autowired
	private GstMapper gstMapper;

	@Override
	@Validate(validator = "gstValidatorImpl",action = "save")
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.GST)
	public GstDTO save(GstDTO gstDto) {
		log.debug("Request to save Gst : {}", gstDto);
		Gst gst = gstMapper.toEntity(gstDto);
		gst = gstRepository.save(gst);
		return gstMapper.toDto(gst);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GstDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Gsts");
		return gstRepository.findAll(pageable).map(gstMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GstDTO> findOne(Long id) {
		log.debug("Request to get Gst : {}", id);
		return gstRepository.findById(id).map(gstMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.GST)
	public void delete(Long id) {
		log.debug("Request to delete Gst : {}", id);
		gstRepository.deleteById(id);
	}

}
