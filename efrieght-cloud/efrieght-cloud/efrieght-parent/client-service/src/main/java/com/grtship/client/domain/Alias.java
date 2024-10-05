package com.grtship.client.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Alias.
 */
@Entity
@Table(name = "alias")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class Alias extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "label")
	private String label;

	@ManyToOne
	@JsonIgnoreProperties(value = "aliases", allowSetters = true)
	private Company companyAlias;

	// jhipster-needle-entity-add-field - JHipster will add fields here

	public Alias label(String label) {
		this.label = label;
		return this;
	}

	public Alias companyAlias(Company company) {
		this.companyAlias = company;
		return this;
	}
}
