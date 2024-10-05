/**
 * 
 */
package com.grtship.audit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.audit.domain.AuditPropertyKey;
import com.grtship.audit.repository.AuditPropertyKeyRepository;

/**
 * @author hp
 *
 */
@Component
public class AuditPropertyKeyHolder {

	private static final Logger log = LoggerFactory.getLogger(AuditPropertyKeyHolder.class);

	private final List<AuditPropertyKey> auditPropertyKeys = new ArrayList<AuditPropertyKey>();

	@Autowired
	private AuditPropertyKeyRepository keyRepository;

	public AuditPropertyKeyHolder() {
	}

	public void addauditPropertyKeys() {
		this.auditPropertyKeys.addAll(this.keyRepository.findAll());
		log.info("auditPropertyList on startup : {} ", this.auditPropertyKeys);
	}

	public List<AuditPropertyKey> getAuditPropertyKeyByReference(String referenceType) {
		log.info("getting audit property key based on referenceType passed : {} ", referenceType);
		List<AuditPropertyKey> propertyList = auditPropertyKeys.stream()
				.filter(obj -> obj.getReferenceType().equalsIgnoreCase(referenceType)).collect(Collectors.toList());
		log.info("getting audit property key based on referenceType passed after fetching from the list {}  {} ",
				this.auditPropertyKeys, propertyList);
		return (propertyList.isEmpty() ? null: propertyList);
	}

	@PostConstruct
	public void register() {
		log.info("PostConstruct for loading audit-propertykeys called ");
//		if (null == auditPropertyKeys || auditPropertyKeys.isEmpty()) {
//			return;
//		}
		this.addauditPropertyKeys();
	}
}
