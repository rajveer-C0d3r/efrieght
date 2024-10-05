package com.grtship.authorisation.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grtship.authorisation.interfaces.Criteria;
import com.grtship.core.filter.Filter;
import com.grtship.core.filter.StringFilter;


/**
 * Criteria class for the {@link com.grt.efreight.domain.Module} entity. This class is used
 * in {@link com.ModuleController.efreight.web.rest.ModuleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /modules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ModuleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private StringFilter moduleName;

    public ModuleCriteria() {
    }

    public ModuleCriteria(ModuleCriteria other) {
        this.moduleName = other.moduleName == null ? null : other.moduleName.copy();
    }

    @Override
    public ModuleCriteria copy() {
        return new ModuleCriteria(this);
    }

    public StringFilter getModuleName() {
        return moduleName;
    }

    public void setModuleName(StringFilter moduleName) {
        this.moduleName = moduleName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ModuleCriteria that = (ModuleCriteria) o;
        return
            Objects.equals(moduleName, that.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        moduleName
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModuleCriteria{" +
                (moduleName != null ? "moduleName=" + moduleName + ", " : "") +
            "}";
    }

}
