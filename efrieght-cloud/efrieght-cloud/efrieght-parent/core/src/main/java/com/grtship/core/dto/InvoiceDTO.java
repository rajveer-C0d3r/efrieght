package com.grtship.core.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.TransactionStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.Invoice} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class InvoiceDTO extends ClientAuditableEntityDTO{
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private String refNo;

    private LocalDate date;

    private Long currencyId;
    
    @NotNull(message = "Version is mandatory.")
    private Integer version;
    
    private String currencyCode;

    private Double amount;

    private Long externalEntityId;

    private TransactionStatus invoiceStatus;
    
    @NotNull(message = "Adusted amount is mandatory.")
    private Double adjustedAmount;
    
    @NotNull(message = "Balance amount is mandatory.")
    private Double balanceAmount;
    
    @NotNull(message = "Narration is mandatory.")
    private String narration;
    
    private Long branchId;

}
