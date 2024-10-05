package com.grtship.account.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.ReferenceType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A ShipmentReference.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_shipment_reference")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ShipmentReference extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "reference_type", length = 60)
	private ReferenceType referenceType;

	@Column(name = "reference_no", length = 60)
	private String referenceNo;

}
