package com.grt.elogfrieght.services.user.mapper;

import org.mapstruct.Mapper;

import com.grt.elogfrieght.services.user.domain.UserApprovalRequest;
import com.grtship.core.dto.UserApprovalRequestDTO;

@Mapper(componentModel = "spring", uses = {})
public interface UserApprovalRequestMapper extends EntityMapper<UserApprovalRequestDTO,UserApprovalRequest> {

}
