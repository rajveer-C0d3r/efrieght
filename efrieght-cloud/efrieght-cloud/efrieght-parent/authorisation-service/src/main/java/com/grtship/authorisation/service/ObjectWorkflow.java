package com.grtship.authorisation.service;

import com.grtship.authorisation.dto.ObjectApprovalDTO;

public interface ObjectWorkflow {
    void validateObjectApprovalRequirement(String message);
}
