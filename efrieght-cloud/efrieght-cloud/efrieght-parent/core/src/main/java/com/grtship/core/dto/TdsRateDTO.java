package com.grtship.core.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.TdsRateStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.TdsRate} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class TdsRateDTO extends AbstractAuditingDTO{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull(message = "Effective from is mandatory.")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @NotNull(message = "Tds Percentage is mandatory.")
    private Double tdsPercentage;

    @NotNull(message = "Basic Rate is mandatory.")
    private Double basicRate;

    private Double surcharge;

    private Double cess;

    @NotNull(message = "Status is mandatory.")
    private TdsRateStatus status;

    private String reason;

    private Integer version;

    private Long tdsId;
}
