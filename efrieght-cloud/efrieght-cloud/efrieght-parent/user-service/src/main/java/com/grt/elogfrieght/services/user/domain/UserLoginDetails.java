/**
 * 
 */
package com.grt.elogfrieght.services.user.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ER Ajay Sharma
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UserLoginDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long userId;
	private String url;
	@Lob
	private String token;
	private Boolean loggedOut;
	private String ipAddress;
	private LocalDateTime loggedInTimestamp;
	private LocalDateTime loggedOutTimeStamp;
}
