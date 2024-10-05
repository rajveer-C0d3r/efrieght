package com.grtship.core.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.constant.RegexConstant;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.domain.Currency} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class CurrencyDTO extends AbstractAuditingDTO {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	@NotNull(message = "Currency Code Is Mandatory, Please Enter Currency Code.")
    @NotEmpty(message = "Currency Code Is Mandatory, Please Enter Currency Code.")
	@Size(min = 3, max = 3, message = "Currency Code Should Have Three Characters")
    @Pattern(regexp = RegexConstant.ALPHABETS_REGEX, message = "Currency Code Should Contain Only alphabets.")
    private String code;

	@NotNull(message = "Currency Name Is Mandatory, Please Enter Currency Name.")
    @NotEmpty(message = "Currency Name Is Mandatory, Please Enter Currency Name.")
	@Size(max = 60, message = "Currency Name Exceeds Character Limits, Maximum 60 Characters Allowed.")
    @Pattern(regexp = RegexConstant.ALPHABETS_SPECIAL_CHAR_REGEX, message = "Currency Name Should Contain Only Alphabets and ( ) -")
    private String name;


}
