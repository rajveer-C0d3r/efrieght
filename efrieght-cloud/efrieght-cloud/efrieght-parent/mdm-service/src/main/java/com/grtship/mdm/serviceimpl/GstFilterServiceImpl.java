package com.grtship.mdm.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.GstDTO;
import com.grtship.core.enumeration.GstType;
import com.grtship.mdm.criteria.GstCriteria;
import com.grtship.mdm.domain.Gst;
import com.grtship.mdm.domain.Gst_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.GstMapper;
import com.grtship.mdm.repository.GstRepository;
import com.grtship.mdm.service.GstFilterService;

@Service
@Transactional(readOnly = true)
public class GstFilterServiceImpl extends QueryService<Gst> implements GstFilterService {

	private final Logger log = LoggerFactory.getLogger(GstFilterServiceImpl.class);

	@Autowired
	private GstRepository gstRepository;

	@Autowired
	private GstMapper gstMapper;

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	public List<GstDTO> findByCriteria(GstCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Gst> specification = createSpecification(criteria);
		return gstMapper.toDto(gstRepository.findAll(specification));
	}

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	public Page<GstDTO> findByCriteria(GstCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Gst> specification = createSpecification(criteria);
		return gstRepository.findAll(specification, page).map(gstMapper::toDto);
	}

	protected Specification<Gst> createSpecification(GstCriteria criteria) {
		Specification<Gst> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getId() != null) {
				specification = specification.and((root, query, criteriaBuilder) -> {
					return criteriaBuilder.and(criteriaBuilder.equal(root.get(Gst_.id), criteria.getId()));
				});
			}
			if (criteria.getHsnSacCode() != null) {
				specification = specification.and((root, query, criteriaBuilder) -> {
					return criteriaBuilder
							.and(criteriaBuilder.like(root.get(Gst_.hsnSacCode), "%" + criteria.getHsnSacCode() + "%"));
				});
			}
			if (criteria.getType() != null) {
				specification = specification.and((root, query, criteriaBuilder) -> {
					return criteriaBuilder
							.and(criteriaBuilder.equal(root.get(Gst_.type), GstType.valueOf(criteria.getType())));
				});
			}
		}
		return specification;
	}
}