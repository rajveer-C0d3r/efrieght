package com.grtship.client.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A BranchGstDetails.
 */
@Entity
@Table(name = "branch_gst_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class BranchGstDetails extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

    @Column(name = "gst_no")
    @NotBlank(message = "GST Number is required.")
    @Size(max = 16,message = "Maximum Size of GST No must be 16.")
    private String gstNo;

    @Column(name = "nature_of_business_activity")
    @Size(max = 50, message = "maximum size of Nature Of Business Activity must be 50.")
    private String natureOfBusinessActivity;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public BranchGstDetails gstNo(String gstNo) {
        this.gstNo = gstNo;
        return this;
    }

    public BranchGstDetails natureOfBusinessActivity(String natureOfBusinessActivity) {
        this.natureOfBusinessActivity = natureOfBusinessActivity;
        return this;
    }
}
