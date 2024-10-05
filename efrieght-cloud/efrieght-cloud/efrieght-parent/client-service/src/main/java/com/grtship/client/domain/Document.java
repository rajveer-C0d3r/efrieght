package com.grtship.client.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Document extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;
    
    @Column(name = "document_code")
    private String documentCode;

    @Column(name = "document_name")
    private String documentName;

    @Lob
    @Column(name = "upload_document")
    private byte[] uploadDocument;

    @Column(name = "upload_document_content_type")
    private String uploadDocumentContentType;
    
    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_name")
    private String referenceName;

    // jhipster-needle-entity-add-field - JHipster will add fields here

	public Document documentCode(String documentCode) {
        this.documentCode = documentCode;
        return this;
    }

    public Document documentName(String documentName) {
        this.documentName = documentName;
        return this;
    }

    public Document uploadDocument(byte[] uploadDocument) {
        this.uploadDocument = uploadDocument;
        return this;
    }

    public Document uploadDocumentContentType(String uploadDocumentContentType) {
        this.uploadDocumentContentType = uploadDocumentContentType;
        return this;
    }

}
