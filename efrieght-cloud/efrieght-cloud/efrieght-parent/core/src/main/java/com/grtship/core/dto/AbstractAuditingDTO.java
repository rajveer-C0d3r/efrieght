package com.grtship.core.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base abstract class for DTO which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractAuditingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String createdBy;
    private Instant createdDate = Instant.now();
    private String lastModifiedBy;
    private Instant lastModifiedDate = Instant.now();
}
