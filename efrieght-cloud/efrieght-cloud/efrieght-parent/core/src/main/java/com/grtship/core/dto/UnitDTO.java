package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

/**
 * A DTO for the {@link com.grt.efreight.domain.Unit} entity.
 */
@EnableCustomAudit
public class UnitDTO extends AbstractAuditingDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -5381251104955324896L;

	private Long id;

	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UnitDTO)) {
			return false;
		}

		return id != null && id.equals(((UnitDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "UnitDTO{" + "id=" + getId() + ", name='" + getName() + "'" + "}";
	}
}
