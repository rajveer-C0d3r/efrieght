/**
 * 
 */
package com.grtship.core.filter;

/**
 * @author Ajay
 *
 */
public class BooleanFilter extends Filter<Boolean> {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * Constructor for BooleanFilter.
	 * </p>
	 */
	public BooleanFilter() {
	}

	/**
	 * <p>
	 * Constructor for BooleanFilter.
	 * </p>
	 *
	 * @param filter a {@link io.github.jhipster.service.filter.BooleanFilter}
	 *               object.
	 */
	public BooleanFilter(final BooleanFilter filter) {
		super(filter);
	}

	/**
	 * <p>
	 * copy.
	 * </p>
	 *
	 * @return a {@link io.github.jhipster.service.filter.BooleanFilter} object.
	 */
	@Override
	public BooleanFilter copy() {
		return new BooleanFilter(this);
	}

}
