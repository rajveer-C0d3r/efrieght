package com.grtship.authorisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.authorisation.domain.ObjectApproval;
import com.grtship.authorisation.domain.ObjectModule;
import com.grtship.core.enumeration.ActionType;

/**
 * Spring Data  repository for the ObjectModule entity.
 */
@Repository
public interface ObjectModuleRepository extends JpaRepository<ObjectModule, Long>, JpaSpecificationExecutor<ObjectModule> {

	Optional<ObjectModule> findByModuleNameAndCompanyId(String moduleName, Long companyId);
	Optional<ObjectModule> findByModuleNameAndActionAndCompanyId(String moduleName ,ActionType action ,Long companyId);
	Optional<ObjectModule> findByModuleNameAndCompanyIdAndIdNot(String moduleName, Long companyId, Long id);
	Optional<ObjectModule> findByModuleNameAndActionAndClientId(String string, ActionType action, Long clientId);
	Optional<ObjectModule> findByModuleNameAndAction(String name, ActionType action);
	ObjectApproval save(ObjectApproval objectApproval);
	Optional<ObjectModule> findByModuleNameAndActionAndClientIdAndCompanyId(String moduleName, ActionType action,
			Long clientId, Long companyId);
	Optional<ObjectModule> findByModuleNameAndActionAndClientIdAndCompanyIdAndIdNot(String moduleName,
			ActionType action, Long clientId, Long companyId, Long id);
}
