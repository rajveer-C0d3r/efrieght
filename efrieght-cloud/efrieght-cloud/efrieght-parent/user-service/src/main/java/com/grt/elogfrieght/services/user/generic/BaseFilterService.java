package com.grt.elogfrieght.services.user.generic;

import java.util.Collection;
import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.domain.Specification;

import com.grtship.core.enumeration.FilterType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseFilterService<T> {

	protected <X> Specification<T> buildSpecification(X filter, SingularAttribute<? super T, X> field,
			FilterType filterType) {
		return buildSpecification(filter, root -> root.get(field), filterType);
	}

	protected <X> Specification<T> buildCollectionSpecification(Collection<X> filter,
			Function<Root<T>, Expression<X>> metaclassFunction, FilterType filterType) {
		if (filterType.equals(FilterType.IN)) {
			return valueIn(metaclassFunction, filter);
		} else if (filterType.equals(FilterType.NOT_IN)) {
			return valueNotIn(metaclassFunction, filter);
		}
		return null;
	}

	private <X> Specification<T> buildSpecification(X filter, Function<Root<T>, Expression<X>> metaclassFunction,
			FilterType filterType) {
		if (filterType.equals(FilterType.EQUALS)) {
			return equalsSpecification(metaclassFunction, filter);
		} else if (filterType.equals(FilterType.NOT_EQUALS)) {
			return notEqualsSpecification(metaclassFunction, filter);
		}
		return null;
	}

	protected <X> Specification<T> buildIgnoreCaseSpecification(final String value, String field) {
		log.debug("Ignore case specification {} {}", field, value);
		Function<Root<T>, Expression<String>> metaclassFunction = root -> root.get(field);
		return (root, query, builder) -> builder.like(builder.upper(metaclassFunction.apply(root)),
				wrapLikeQuery(value));
	}

	private String wrapLikeQuery(String txt) {
		return "%" + txt.toUpperCase() + '%';
	}

	private <X> Specification<T> equalsSpecification(Function<Root<T>, Expression<X>> metaclassFunction,
			final X value) {
		return (root, query, builder) -> builder.equal(metaclassFunction.apply(root), value);
	}

	private <X> Specification<T> valueIn(Function<Root<T>, Expression<X>> metaclassFunction,
			final Collection<X> values) {
		return (root, query, builder) -> {
			In<X> in = builder.in(metaclassFunction.apply(root));
			for (X value : values) {
				in = in.value(value);
			}
			return in;
		};
	}

	private <X> Specification<T> valueNotIn(Function<Root<T>, Expression<X>> metaclassFunction,
			final Collection<X> values) {
		return (root, query, builder) -> {
			In<X> in = builder.in(metaclassFunction.apply(root));
			for (X value : values) {
				in = in.value(value);
			}
			return builder.not(in);
		};
	}

	private <X> Specification<T> notEqualsSpecification(Function<Root<T>, Expression<X>> metaclassFunction,
			final X value) {
		return (root, query, builder) -> builder.not(builder.equal(metaclassFunction.apply(root), value));
	}

}
