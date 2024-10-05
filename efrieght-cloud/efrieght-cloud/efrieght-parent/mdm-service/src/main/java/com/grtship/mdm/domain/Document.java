package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A Document.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mdm_document_definition")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Document extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "Document Code is Required")
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Size(max = 50, message = "Document Name Exceeds Character Limits, Maximum 50 Characters Allowed.")
    @NotNull(message = "Document Name is Required.")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private DocumentType type;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;
    
    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_name", length = 20)
    private String referenceName;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    
    public Document referenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }
    public Document referenceId(Long referenceId) {
        this.referenceId = referenceId;
        return this;
    }
    public Document code(String code) {
        this.code = code;
        return this;
    }

    public Document name(String name) {
        this.name = name;
        return this;
    }

    public Document type(DocumentType type) {
        this.type = type;
        return this;
    }

    public Document isMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
        return this;
    }
}
