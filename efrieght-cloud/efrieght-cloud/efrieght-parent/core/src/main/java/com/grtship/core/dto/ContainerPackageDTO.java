package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.PackageType;

/**
 * A DTO for the {@link com.grt.efreight.domain.ContainerPackage} entity.
 */
@EnableCustomAudit
public class ContainerPackageDTO extends AbstractAuditingDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -7366742626849055197L;

	private Long id;

	@NotNull
	private String name;

	@NotNull
	private Boolean wooden;

	@NotNull
	private PackageType type;

	private String remarks;

	private Boolean palletizable;

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

	public Boolean isWooden() {
		return wooden;
	}

	public void setWooden(Boolean wooden) {
		this.wooden = wooden;
	}

	public PackageType getType() {
		return type;
	}

	public void setType(PackageType type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Boolean isPalletizable() {
		return palletizable;
	}

	public void setPalletizable(Boolean palletizable) {
		this.palletizable = palletizable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ContainerPackageDTO)) {
			return false;
		}

		return id != null && id.equals(((ContainerPackageDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "ContainerPackageDTO{" + "id=" + getId() + ", name='" + getName() + "'" + ", wooden='" + isWooden() + "'"
				+ ", type='" + getType() + "'" + ", remarks='" + getRemarks() + "'" + ", palletizable='"
				+ isPalletizable() + "'" + "}";
	}
}
