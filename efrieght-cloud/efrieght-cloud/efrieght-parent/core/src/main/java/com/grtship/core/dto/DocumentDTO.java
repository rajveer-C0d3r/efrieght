package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.Document} entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class DocumentDTO extends AbstractAuditingDTO {

	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
    private String code;

    private String name;
    @IgnoreAuditField
    private byte[] uploadDocument;
    
    private String uploadDocumentContentType;

    private DocumentType type;

    private Boolean isMandatory;

    private Long referenceId;

    private String referenceName;
    
    private Long clientId;
    private Long companyId;
    
    
}
