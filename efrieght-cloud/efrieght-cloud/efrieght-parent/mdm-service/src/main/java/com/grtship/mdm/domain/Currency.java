package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.constant.Constants;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Currency.
 */
@Entity
@Table(name = "mdm_currency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Currency extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Currency Code Is Mandatory, Please Enter Currency Code.")
    @NotEmpty(message = "Currency Code Is Mandatory, Please Enter Currency Code.")
    @Size(min = 3, max = 3, message = "Currency Code Should Have 3 Characters.")
    @Pattern(regexp = Constants.ALPHABETS_REGEX, message = "Currency Code Should Contain Only alphabets.")
    @Column(name = "code",length = 3, nullable = false)
    private String code;

    @NotNull(message = "Currency Name Is Mandatory, Please Enter Currency Name.")
    @NotEmpty(message = "Currency Name Is Mandatory, Please Enter Currency Name.")
    @Size(max = 60, message = "Currency Name Exceeds Character Limits, Maximum 60 Characters Allowed.")
    @Pattern(regexp = Constants.ALPHABETS_SPECIAL_CHAR_REGEX, message = "Currecny Name Should Contain Only Alphabets and ( ) -")
    @Column(name = "name",length = 60, nullable = false)
    private String name;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Currency code(String code) {
        this.code = code;
        return this;
    }

    public Currency name(String name) {
        this.name = name;
        return this;
    }
}
