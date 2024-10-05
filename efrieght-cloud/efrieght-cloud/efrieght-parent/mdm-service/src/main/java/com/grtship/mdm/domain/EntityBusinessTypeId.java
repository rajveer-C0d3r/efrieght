package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.grtship.core.enumeration.EntityType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
public class EntityBusinessTypeId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;
    
    @ManyToOne
    @JoinColumn(name = "external_entity_id")
    private ExternalEntity externalEntity;


public EntityBusinessTypeId(EntityType entityType, ExternalEntity externalEntityId) {
	this.entityType = entityType;
	this.externalEntity = externalEntityId;
}

}
