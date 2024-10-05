package com.grtship.account.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/*this Entity and its services will move into common module*/
/**
 * A ObjectAlias.
 */
@Entity
@Table(name = "acc_object_alias")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ObjectAlias extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "reference_name")
	private String referenceName;

	@Column(name = "reference_id")
	private Long referenceId;

	// jhipster-needle-entity-add-field - JHipster will add fields here
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public ObjectAlias name(String name) {
		this.name = name;
		return this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public ObjectAlias referenceName(String referenceName) {
		this.referenceName = referenceName;
		return this;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public ObjectAlias referenceId(Long referenceId) {
		this.referenceId = referenceId;
		return this;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}
	// jhipster-needle-entity-add-getters-setters - JHipster will add getters and
	// setters here

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ObjectAlias)) {
			return false;
		}
		return id != null && id.equals(((ObjectAlias) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "ObjectAlias{" + "id=" + getId() + ", name='" + getName() + "'" + ", referenceName='"
				+ getReferenceName() + "'" + ", referenceId=" + getReferenceId() + "}";
	}
}
