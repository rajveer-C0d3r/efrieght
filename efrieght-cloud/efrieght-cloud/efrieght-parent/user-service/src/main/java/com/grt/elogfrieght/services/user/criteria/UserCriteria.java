package com.grt.elogfrieght.services.user.criteria;

import java.io.Serializable;
import java.util.List;

import com.grt.elogfrieght.services.user.interfaces.Criteria;
import com.grtship.core.enumeration.UserType;
import com.grtship.core.filter.Filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class UserCriteria implements Serializable, Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8561070585219398928L;

	private Long id;
	private List<Long> ids;
	private List<Long> companyIds;
	private List<Long> branchIds;
	private String name;
	private String email;
	private Long companyId;
	private Long branchId;
	private String userType;
	private Long rolesId;
	private String roleName;
	private Boolean activated;
	private String status;
	private String deactivationType;

	private boolean forUserList;

	public UserCriteria(UserCriteria other) {
		this.id = other.id == null ? null : other.id;
		this.name = other.name == null ? null : other.name;
		this.email = other.email == null ? null : other.email;
		this.userType = other.userType == null ? null : other.userType;
		this.rolesId = other.rolesId == null ? null : other.rolesId;
		this.roleName = other.roleName == null ? null : other.roleName;
		this.activated = other.activated == null ? null : other.activated;
		this.forUserList = other.forUserList;
	}

	@Override
	public Criteria copy() {
		return new UserCriteria(this);
	}

	public static class UserTypeFilter extends Filter<UserType> {

		private static final long serialVersionUID = 1270234425763275432L;

		public UserTypeFilter() {
		}

		public UserTypeFilter(UserTypeFilter filter) {
			super(filter);
		}

		@Override
		public UserTypeFilter copy() {
			return new UserTypeFilter(this);
		}

	}

}
