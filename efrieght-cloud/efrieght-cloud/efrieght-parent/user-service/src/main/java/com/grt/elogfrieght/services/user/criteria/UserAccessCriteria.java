package com.grt.elogfrieght.services.user.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grt.elogfrieght.services.user.interfaces.Criteria;
import com.grtship.core.filter.Filter;
import com.grtship.core.filter.LongFilter;

/**
 * Criteria class for the {@link com.grt.oath2.domain.UserAccess} entity. This class is used
 * in {@link com.grt.oath2.web.rest.UserAccessResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-accesses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserAccessCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter branchId;

    private LongFilter companyId;

    private LongFilter userId;

    public UserAccessCriteria() {
    }

    public UserAccessCriteria(UserAccessCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.branchId = other.branchId == null ? null : other.branchId.copy();
        this.companyId = other.companyId == null ? null : other.companyId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public UserAccessCriteria copy() {
        return new UserAccessCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getBranchId() {
        return branchId;
    }

    public void setBranchId(LongFilter branchId) {
        this.branchId = branchId;
    }

    public LongFilter getCompanyId() {
        return companyId;
    }

    public void setCompanyId(LongFilter companyId) {
        this.companyId = companyId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserAccessCriteria that = (UserAccessCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(branchId, that.branchId) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        branchId,
        companyId,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAccessCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (branchId != null ? "branchId=" + branchId + ", " : "") +
                (companyId != null ? "companyId=" + companyId + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
