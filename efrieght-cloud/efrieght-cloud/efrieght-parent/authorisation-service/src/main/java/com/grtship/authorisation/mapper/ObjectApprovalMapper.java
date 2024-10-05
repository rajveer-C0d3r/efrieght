package com.grtship.authorisation.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.authorisation.domain.ObjectApproval;
import com.grtship.authorisation.domain.ObjectApprovalAction;
import com.grtship.authorisation.domain.ObjectModule;
import com.grtship.core.enumeration.DomainStatus;

@Mapper(componentModel = "spring")
public abstract class ObjectApprovalMapper {


   @Mapping(source = "moduleName",target = "objectName")
   @Mapping(source = "parallelApprover",target = "isParallel")
   @Mapping(target = "approvalActions",expression = "java(this.setApprovalActions(module))")
   @Mapping(target = "objectStatus",expression = "java(this.setObjectStatus())")
   @Mapping(target = "approvalStatus",expression = "java(this.setObjectStatus())")
   @Mapping(target = "id",ignore = true)
   public abstract ObjectApproval toObjectApproval(ObjectModule module);
   
   public Set<ObjectApprovalAction> setApprovalActions(ObjectModule module){
	   Set<ObjectApprovalAction> approvalActions=new HashSet<>();
		module.getObjectApprovalSequences().stream().forEach(approvalObj -> {
			approvalActions.add(
					ObjectApprovalAction.builder().action(DomainStatus.PENDING).permissionCode(approvalObj.getPermissionCode())
							.approvalSequence(approvalObj.getApprovalSequence()).build());
		});
		return approvalActions;
   }
   
   public DomainStatus setObjectStatus() {
	   return DomainStatus.PENDING;
   }
}
