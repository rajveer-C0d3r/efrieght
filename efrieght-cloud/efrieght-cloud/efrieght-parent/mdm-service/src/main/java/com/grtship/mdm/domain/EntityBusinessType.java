package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A EntityBusinessDetails.
 */
@Entity
@Table(name = "mdm_entity_business_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@NoArgsConstructor
@EnableCustomAudit
public class EntityBusinessType implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EntityBusinessTypeId id;


	public EntityBusinessType(EntityBusinessTypeId id) {
		this.id = id;
	}

}