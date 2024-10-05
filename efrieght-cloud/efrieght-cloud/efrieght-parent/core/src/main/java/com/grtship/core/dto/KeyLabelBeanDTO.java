package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EnableCustomAudit
public class KeyLabelBeanDTO {
	
	private String key;
	private String label;
	
	public KeyLabelBeanDTO(String key, String label) {
		this.key = key;
		this.label = label;
	}
	
}
