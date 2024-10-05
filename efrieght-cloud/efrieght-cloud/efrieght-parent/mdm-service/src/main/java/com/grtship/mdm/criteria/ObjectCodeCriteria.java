package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grtship.core.filter.Filter;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.interfaces.Criteria;

/**
 * Criteria class for the {@link com.grt.efreight.domain.ObjectCode} entity. This class is used
 * in {@link com.ObjectCodeController.efreight.web.rest.ObjectCodeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /object-codes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ObjectCodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter objectName;

    private StringFilter prefix;

    private LongFilter padding;

    private LongFilter counter;

    public ObjectCodeCriteria() {
    }

    public ObjectCodeCriteria(ObjectCodeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.objectName = other.objectName == null ? null : other.objectName.copy();
        this.prefix = other.prefix == null ? null : other.prefix.copy();
        this.padding = other.padding == null ? null : other.padding.copy();
        this.counter = other.counter == null ? null : other.counter.copy();
    }

    @Override
    public ObjectCodeCriteria copy() {
        return new ObjectCodeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getObjectName() {
        return objectName;
    }

    public void setObjectName(StringFilter objectName) {
        this.objectName = objectName;
    }

    public StringFilter getPrefix() {
        return prefix;
    }

    public void setPrefix(StringFilter prefix) {
        this.prefix = prefix;
    }

    public LongFilter getPadding() {
        return padding;
    }

    public void setPadding(LongFilter padding) {
        this.padding = padding;
    }

    public LongFilter getCounter() {
        return counter;
    }

    public void setCounter(LongFilter counter) {
        this.counter = counter;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ObjectCodeCriteria that = (ObjectCodeCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(objectName, that.objectName) &&
            Objects.equals(prefix, that.prefix) &&
            Objects.equals(padding, that.padding) &&
            Objects.equals(counter, that.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        objectName,
        prefix,
        padding,
        counter
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjectCodeCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (objectName != null ? "objectName=" + objectName + ", " : "") +
                (prefix != null ? "prefix=" + prefix + ", " : "") +
                (padding != null ? "padding=" + padding + ", " : "") +
                (counter != null ? "counter=" + counter + ", " : "") +
            "}";
    }

}
