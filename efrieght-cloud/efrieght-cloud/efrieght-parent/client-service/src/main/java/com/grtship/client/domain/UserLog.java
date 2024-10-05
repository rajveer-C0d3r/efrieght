/**
 * 
 */
package com.grtship.client.domain;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.enumeration.UserLogType;

import lombok.Data;

/**
 * @author Ajay
 *
 */
@Entity
@Table(name = "user_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "client_id")
	private Long clientId;
	private Boolean status;
	@Column(name = "user_log_type")
	@Enumerated(EnumType.STRING)
	private UserLogType action;
	@Column(name = "user_id")
	private Long userId;
	private String email;
	private String message;
}
