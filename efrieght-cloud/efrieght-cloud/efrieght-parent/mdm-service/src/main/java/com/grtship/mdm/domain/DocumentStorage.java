package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DocumentStorage.
 */

@Entity
@Table(name = "mdm_document_storage")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DocumentStorage extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "document_definition_id")
    private Long documentDefinitionId;

    @NotNull
    @Column(name = "reference_name")
    private String referenceName;

    @NotNull
    @Column(name = "reference_id")
    private Long referenceId;

    @NotNull
    @Column(name = "file_type")
    private String fileType;

    @NotNull
    @Column(name = "file_storage")
    private String fileStorage;

    @NotNull
    @Column(name = "original_file_name")
    private String originalFileName;

    @NotNull
    @Column(name = "storage_location")
    private String storageLocation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public DocumentStorage documentDefinitionId(Long documentDefinitionId) {
        this.documentDefinitionId = documentDefinitionId;
        return this;
    }

    public DocumentStorage referenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }

    public DocumentStorage referenceId(Long referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public DocumentStorage fileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public DocumentStorage fileStorage(String fileStorage) {
        this.fileStorage = fileStorage;
        return this;
    }

    public DocumentStorage originalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
        return this;
    }

    public DocumentStorage storageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
        return this;
    }
}
