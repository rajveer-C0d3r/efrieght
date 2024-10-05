package com.grtship.mdm.serviceimpl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.criteria.ObjectAliasCriteria;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.mapper.ObjectAliasMapper;
import com.grtship.mdm.repository.ObjectAliasRepository;
import com.grtship.mdm.service.ObjectAliasQueryService;
import com.grtship.mdm.specs.ObjectAliasSpecs;

@Service
@Transactional(readOnly = true)
public class ObjectAliasQueryServiceImpl implements ObjectAliasQueryService {

	@Autowired
	private ObjectAliasMapper aliasMapper;

	@Autowired
	private ObjectAliasRepository aliasRepository;

	@Override
	public List<ObjectAliasDTO> findByCriteria(ObjectAliasCriteria aliasCriteria) {
		List<ObjectAlias> aliasList = aliasRepository.findAll(ObjectAliasSpecs.getAliasBySpecs(aliasCriteria));
		return (!CollectionUtils.isEmpty(aliasList)) ? aliasMapper.toDto(aliasList) : Collections.emptyList();
	}

	@Override
	public List<ObjectAliasDTO> getListOfAliasByReferanceIdAndReferenceName(Long referenceId, String referenceName) {
		ObjectAliasCriteria criteria = new ObjectAliasCriteria();
		criteria.setReferenceId(referenceId);
		criteria.setReferenceName(referenceName);
		return findByCriteria(criteria);
	}

	@Override
	public List<ObjectAliasDTO> getListOfAliasByReferanceIdListAndReferenceName(List<Long> referenceIds,
			String referenceName) {
		ObjectAliasCriteria criteria = new ObjectAliasCriteria();
		criteria.setReferenceIds(referenceIds);
		criteria.setReferenceName(referenceName);
		return findByCriteria(criteria);
	}

}
