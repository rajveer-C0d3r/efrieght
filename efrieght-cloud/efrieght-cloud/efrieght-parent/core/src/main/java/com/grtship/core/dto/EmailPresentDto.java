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
 * @author jazzc
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class EmailPresentDto {
	List<String> emailIds;
}
