package com.grtship.authorisation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectApprovalDTO {
  private Long id;
  private String objectName;
  private String action;
  private Long clientId;
}
