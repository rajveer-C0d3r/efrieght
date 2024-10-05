package com.grtship.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSequentialDTO {
   private List<Long> roleIds;
   private Long companyId;
   private Long clientId;
}
