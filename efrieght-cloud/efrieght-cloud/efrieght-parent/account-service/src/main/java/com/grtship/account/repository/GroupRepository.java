package com.grtship.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.Group;

/**
 * Spring Data repository for the Group entity.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

	List<Group> findByParentGroupIsNull();

	List<Group> findByTreeIdContaining(String id);

	Group findByCodeAndClientIdAndCompanyId(String code, Long clientId, Long companyId);

	Group findByIdNotAndCodeAndClientIdAndCompanyId(Long id, String code, Long clientId, Long companyId);

	Group findByNameAndClientIdAndCompanyId(String name, Long clientId, Long companyId);

	Group findByIdNotAndNameAndClientIdAndCompanyId(Long id, String name, Long clientId, Long companyId);

	List<Group> findByCodeInAndClientIdAndCompanyId(List<String> aliasesToSave, Long clientId, Long companyId);

	List<Group> findByNameInAndClientIdAndCompanyId(List<String> aliasesToSave, Long clientId, Long companyId);
}
