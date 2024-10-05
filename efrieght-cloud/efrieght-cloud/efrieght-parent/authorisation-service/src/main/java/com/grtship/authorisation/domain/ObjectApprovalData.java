package com.grtship.authorisation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ObjectApprovalData {
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    @Column(name = "property_name")
    private String propertyName;
    
    @Column(name = "old_value")
    private String oldValue;
    
    @Column(name = "new_value")
    private String newValue;
    
    @Column(name = "datatype")
    private String datatype;
    
    @Column(name = "action")
    private String action;
}
