package com.grt.elogfrieght.services.user.criteria;

import java.io.Serializable;
import java.util.List;

import com.grt.elogfrieght.services.user.interfaces.Criteria;
import com.grtship.core.enumeration.PermissionType;
import com.grtship.core.filter.Filter;
import com.grtship.core.filter.StringFilter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionCriteria implements Serializable, Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2169645207347011100L;
	private StringFilter permissionCode;
	private StringFilter permissionLabel;
	private StringFilter moduleName;
	private String type;
	private List<String> types;

	public PermissionCriteria(PermissionCriteria other) {
		this.permissionCode = other.permissionCode == null ? null : other.permissionCode.copy();
		this.moduleName = other.moduleName == null ? null : other.moduleName.copy();
		this.permissionLabel = other.permissionLabel == null ? null : other.permissionLabel.copy();
	}

	@Override
	public Criteria copy() {
		return new PermissionCriteria(this);
	}

	public static class PermissionTypeFilter extends Filter<PermissionType> {

		private static final long serialVersionUID = 1270234425763275432L;

		public PermissionTypeFilter() {
		}

		public PermissionTypeFilter(PermissionTypeFilter filter) {
			super(filter);
		}

		@Override
		public PermissionTypeFilter copy() {
			return new PermissionTypeFilter(this);
		}

	}

}
