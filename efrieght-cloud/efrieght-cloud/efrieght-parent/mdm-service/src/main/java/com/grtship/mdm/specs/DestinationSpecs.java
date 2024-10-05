package com.grtship.mdm.specs;

import org.springframework.data.jpa.domain.Specification;

import com.grtship.mdm.domain.Destination;
import com.grtship.mdm.domain.Destination_;

public class DestinationSpecs {
	
	public static Specification<Destination> getDestinationByNameSpec(String name) {
		return (root, query, criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get(Destination_.name), name);
		};
	}

	public static Specification<Destination> getDestinationByCodeSpec(String code) {
		return (root, query, criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get(Destination_.code), code);
		};
	}
}
