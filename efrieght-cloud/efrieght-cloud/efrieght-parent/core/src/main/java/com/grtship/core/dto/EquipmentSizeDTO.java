package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

/**
 * A DTO for the {@link com.grt.efreight.domain.EquipmentSize} entity.
 */
@EnableCustomAudit
public class EquipmentSizeDTO extends AbstractAuditingDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -187435414059491478L;

	private Long id;

	@NotNull
	private Integer size;

	@NotNull
	private String noOfTeu;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getNoOfTeu() {
		return noOfTeu;
	}

	public void setNoOfTeu(String noOfTeu) {
		this.noOfTeu = noOfTeu;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof EquipmentSizeDTO)) {
			return false;
		}

		return id != null && id.equals(((EquipmentSizeDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "EquipmentSizeDTO{" + "id=" + getId() + ", size=" + getSize() + ", noOfTeu='" + getNoOfTeu() + "'" + "}";
	}
}
