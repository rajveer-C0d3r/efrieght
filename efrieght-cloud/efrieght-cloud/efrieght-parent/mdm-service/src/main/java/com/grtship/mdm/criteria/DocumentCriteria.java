package com.grtship.mdm.criteria;

import java.io.Serializable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.grtship.core.enumeration.DocumentType;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DocumentCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7228104192008559639L;
	private Long id;
	private Long referenceId;
	private String referenceName;
	@Enumerated(EnumType.STRING)
	private DocumentType documentType;

	public DocumentCriteria id(Long id) {
		this.id = id;
		return this;
	}

	public DocumentCriteria referenceId(Long referenceId) {
		this.referenceId = referenceId;
		return this;
	}

	public DocumentCriteria referenceName(String referenceName) {
		this.referenceName = referenceName;
		return this;
	}

	public DocumentCriteria documentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}

}
