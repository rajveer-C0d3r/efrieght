package com.grtship.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationContainerDTO {
    private AuthorizationObjectDTO authorizationObjectDTO;
    private Object object;
}
