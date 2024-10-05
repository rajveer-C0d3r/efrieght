package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.ObjectCode;

/**
 * Spring Data  repository for the ObjectCode entity.
 */
@Repository
public interface ObjectCodeRepository extends JpaRepository<ObjectCode, Long>, JpaSpecificationExecutor<ObjectCode> {
	
	ObjectCode findByObjectNameAndParentCode(String objectName, String parentCode);
	
	List<ObjectCode> findByObjectNameAndPrefixNotNullOrderByLastModifiedDateDesc(String objectName);
}
