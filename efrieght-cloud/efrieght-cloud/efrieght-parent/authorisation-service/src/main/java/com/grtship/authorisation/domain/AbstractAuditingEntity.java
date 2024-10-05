package com.grtship.authorisation.domain;


import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@Data
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    
    private Instant lastModifiedDate = Instant.now();
    
    @Column(name = "is_public")
    private Boolean isPublic=Boolean.FALSE;
}
