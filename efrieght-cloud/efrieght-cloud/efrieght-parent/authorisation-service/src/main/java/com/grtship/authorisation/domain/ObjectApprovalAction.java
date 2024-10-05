package com.grtship.authorisation.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ObjectApprovalAction implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    @Column(name = "approval_sequence")
    private Integer approvalSequence;
    
//    @Column(name = "role_id")
//    private Long roleId;
    @Column(name = "permission_code")
    private String permissionCode;
    
    @Column(name = "actioned_by")
    private Long actionedBy;
    
    @Column(name = "actioner_name")
    private String actioner;
    
    @Column(name = "action_datetime")
    private LocalDateTime actionDatetime;
    
    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private DomainStatus action;
    
    @Column(name = "action_comment")
    private String actionComment;
    
}
