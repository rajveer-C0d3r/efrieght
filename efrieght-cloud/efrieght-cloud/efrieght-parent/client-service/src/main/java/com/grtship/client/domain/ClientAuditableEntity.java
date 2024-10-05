package com.grtship.client.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
//this is duplicate class in all microservices which should move into common module.
@MappedSuperclass
public abstract class ClientAuditableEntity /* extends AbstractAuditingEntity */{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "client_id")
	private Long clientId;
    
    @Column(name = "company_id")
	private Long companyId;

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

}
