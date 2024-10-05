package com.grt.elogfrieght.services.user.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageableEntityDTO<T> {

	private List<T> contents;
	private Long totalRecords;

	public PageableEntityDTO(List<T> contents, Long totalRecords) {
		super();
		this.contents = contents;
		this.totalRecords = totalRecords;
	}
}
