package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

/**
 * A DTO for the {@link com.grt.efreight.domain.Container} entity.
 */
@EnableCustomAudit
public class ContainerDTO extends AbstractAuditingDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 3789650004214868185L;

	private Long id;

	private Boolean deactivate;

	private Long equipmentTypeId;

	private Long sizeId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean isDeactivate() {
		return deactivate;
	}

	public void setDeactivate(Boolean deactivate) {
		this.deactivate = deactivate;
	}

	public Long getEquipmentTypeId() {
		return equipmentTypeId;
	}

	public void setEquipmentTypeId(Long equipmentId) {
		this.equipmentTypeId = equipmentId;
	}

	public Long getSizeId() {
		return sizeId;
	}

	public void setSizeId(Long equipmentSizeId) {
		this.sizeId = equipmentSizeId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ContainerDTO)) {
			return false;
		}

		return id != null && id.equals(((ContainerDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "ContainerDTO{" + "id=" + getId() + ", deactivate='" + isDeactivate() + "'" + ", rquipmentTypeId="
				+ getEquipmentTypeId() + ", sizeId=" + getSizeId() + "}";
	}
}
