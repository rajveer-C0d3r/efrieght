package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.AccessType;

import lombok.Data;

@Data
@Entity
@Table(name = "mdm_sub_module")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SubModule implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "cargo_level_flag")
	private Boolean cargoLevelFlag;
	
	@Column(name = "master_level_flag")
	private Boolean masterLevelFlag;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "access_type")
	private AccessType accessType;
	
	@Column(name = "access_code")
	private String accessCode;
	
	@Column(name = "main_module_id")
    private Long mainModuleId;

}
