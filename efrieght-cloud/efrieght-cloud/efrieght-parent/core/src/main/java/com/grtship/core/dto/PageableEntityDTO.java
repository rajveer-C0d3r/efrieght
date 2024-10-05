package com.grtship.core.dto;

import java.util.List;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EnableCustomAudit
public class PageableEntityDTO<T> {

	private List<T> contents;
	private Long totalRecords;

	public PageableEntityDTO(List<T> contents, Long totalRecords) {
		super();
		this.contents = contents;
		this.totalRecords = totalRecords;
	}

}
