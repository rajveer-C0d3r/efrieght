package com.grtship.authorisation.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A ObjectApprovalSequence.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "obj_approval_sequence")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ObjectApprovalSequence implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "role_id")
//    private Long roleId;
    @Column(name = "permission_code")
    private String permissionCode;

    @Column(name = "approval_sequence")
    private Integer approvalSequence;

    @Column(name = "approval_count")
    private Integer approvalCount;

    @Column(name = "designation_id")
    private Long designationId;

    @Column(name = "department_id")
    private Long departmentId;

//    @ManyToOne
//    @JsonIgnoreProperties(value = "approvalSequences", allowSetters = true)
//    private ObjectModule objectModule;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ObjectApprovalSequence roleId(String permissionCode) {
        this.permissionCode = permissionCode;
        return this;
    }

    public ObjectApprovalSequence approvalSequence(Integer approvalSequence) {
        this.approvalSequence = approvalSequence;
        return this;
    }

    public ObjectApprovalSequence approvalCount(Integer approvalCount) {
        this.approvalCount = approvalCount;
        return this;
    }

    public ObjectApprovalSequence designationId(Long designationId) {
        this.designationId = designationId;
        return this;
    }

    public ObjectApprovalSequence departmentId(Long departmentId) {
        this.departmentId = departmentId;
        return this;
    }
    
//    public Long getObjectModuleId() {
//        return (this.objectModule!=null && this.objectModule.getId()!=null) ? this.objectModule.getId() : null;
//    }
//    
//    public void setObjectModuleId(Long objectModuleId) {
//        if(this.objectModule==null) {
//        	this.objectModule = new ObjectModule();
//        }
//        this.objectModule.setId(objectModuleId); 
//    }
//
//
//    public ObjectApprovalSequence objectModule(ObjectModule objectModule) {
//        this.objectModule = objectModule;
//        return this;
//    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

}
