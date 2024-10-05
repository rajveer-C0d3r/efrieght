/**
 * 
 */
package com.grtship.core.dto;

import java.util.List;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ajay
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class UserSpecificCBResponse {
	List<CompanyBranchResponse> content;
}
