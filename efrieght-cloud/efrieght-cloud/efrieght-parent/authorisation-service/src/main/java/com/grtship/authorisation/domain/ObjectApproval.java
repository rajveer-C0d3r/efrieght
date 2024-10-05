package com.grtship.authorisation.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class ObjectApproval implements Serializable {
  
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "obj_reference_id")
	private Long referenceId;
	
	@Type(type= "org.hibernate.type.NumericBooleanType")
	@Column(name = "is_parallel")
	private Boolean isParallel;
	
	@Column(name = "initiater_id")
	private Long initiaterId;
	
	@Column(name = "initaiter_name")
	private String initiaterEmail;
	
	@CreatedDate
    @Column(name = "approval_request_datetime", updatable = false)
    @JsonIgnore
    @Builder.Default
    private Instant requestDatetime = Instant.now();
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private DomainStatus status;
	
	@Column(name = "object_name")
	private String objectName;
	
	@Column(name = "object_version")
	private Long objectVersion;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "object_status")
	private DomainStatus objectStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status")
	private DomainStatus approvalStatus;
	
	@OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	private ObjectApprovalData approvalData;
	
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<ObjectApprovalAction> approvalActions=new HashSet<>();
	
}
