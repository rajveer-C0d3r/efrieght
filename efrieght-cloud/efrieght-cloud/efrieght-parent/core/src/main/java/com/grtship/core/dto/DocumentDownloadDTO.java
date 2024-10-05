package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

@Data
@EnableCustomAudit
public class DocumentDownloadDTO implements Serializable{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private String name;
	private String fileName;
	private String originalFileName;
	private Long referenceId;
	private String referenceName;
}
