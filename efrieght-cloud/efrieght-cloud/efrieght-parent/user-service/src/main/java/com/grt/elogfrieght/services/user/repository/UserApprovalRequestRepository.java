package com.grt.elogfrieght.services.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grt.elogfrieght.services.user.domain.UserApprovalRequest;

@Repository
public interface UserApprovalRequestRepository extends JpaRepository<UserApprovalRequest, Long> {

	Optional<UserApprovalRequest> findByEmailAndReferenceIdAndModuleNameAndPermissionCode(String email, Long referenceId,
			String moduleName, String permissionCode);

}
