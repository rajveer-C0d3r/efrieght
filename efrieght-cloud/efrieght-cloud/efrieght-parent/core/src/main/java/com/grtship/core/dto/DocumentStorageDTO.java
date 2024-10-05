package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.DocumentStorage} entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class DocumentStorageDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

	private Long documentDefinitionId;

	private String referenceName;

	private Long referenceId;

	private String fileType;

	private String fileStorage; // fileId

	private String originalFileName;

	private String storageLocation;

	private Long clientId;
	private Long companyId;

	private String documentCode;
	private String documentName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDocumentDefinitionId() {
		return documentDefinitionId;
	}

	public void setDocumentDefinitionId(Long documentDefinitionId) {
		this.documentDefinitionId = documentDefinitionId;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileStorage() {
		return fileStorage;
	}

	public void setFileStorage(String fileStorage) {
		this.fileStorage = fileStorage;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DocumentStorageDTO)) {
			return false;
		}

		return id != null && id.equals(((DocumentStorageDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "DocumentStorageDTO{" + "id=" + getId() + ", documentDefinitionId=" + getDocumentDefinitionId()
				+ ", referenceName='" + getReferenceName() + "'" + ", referenceId=" + getReferenceId() + ", fileType='"
				+ getFileType() + "'" + ", fileStorage='" + getFileStorage() + "'" + ", originalFileName='"
				+ getOriginalFileName() + "'" + ", storageLocation='" + getStorageLocation() + "'" + "}";
	}
}
