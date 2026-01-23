package com.walshe.multitenant.service.criteria;

import com.walshe.multitenant.domain.enumeration.BusinessRole;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.walshe.multitenant.domain.BusinessInvitation} entity. This class is used
 * in {@link com.walshe.multitenant.web.rest.BusinessInvitationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /business-invitations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BusinessInvitationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering BusinessRole
     */
    public static class BusinessRoleFilter extends Filter<BusinessRole> {

        public BusinessRoleFilter() {}

        public BusinessRoleFilter(BusinessRoleFilter filter) {
            super(filter);
        }

        @Override
        public BusinessRoleFilter copy() {
            return new BusinessRoleFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BusinessRoleFilter role;

    private StringFilter token;

    private StringFilter invitedEmail;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter businessId;

    private LongFilter invitedById;

    private Boolean distinct;

    public BusinessInvitationCriteria() {}

    public BusinessInvitationCriteria(BusinessInvitationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.role = other.optionalRole().map(BusinessRoleFilter::copy).orElse(null);
        this.token = other.optionalToken().map(StringFilter::copy).orElse(null);
        this.invitedEmail = other.optionalInvitedEmail().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.businessId = other.optionalBusinessId().map(LongFilter::copy).orElse(null);
        this.invitedById = other.optionalInvitedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BusinessInvitationCriteria copy() {
        return new BusinessInvitationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BusinessRoleFilter getRole() {
        return role;
    }

    public Optional<BusinessRoleFilter> optionalRole() {
        return Optional.ofNullable(role);
    }

    public BusinessRoleFilter role() {
        if (role == null) {
            setRole(new BusinessRoleFilter());
        }
        return role;
    }

    public void setRole(BusinessRoleFilter role) {
        this.role = role;
    }

    public StringFilter getToken() {
        return token;
    }

    public Optional<StringFilter> optionalToken() {
        return Optional.ofNullable(token);
    }

    public StringFilter token() {
        if (token == null) {
            setToken(new StringFilter());
        }
        return token;
    }

    public void setToken(StringFilter token) {
        this.token = token;
    }

    public StringFilter getInvitedEmail() {
        return invitedEmail;
    }

    public Optional<StringFilter> optionalInvitedEmail() {
        return Optional.ofNullable(invitedEmail);
    }

    public StringFilter invitedEmail() {
        if (invitedEmail == null) {
            setInvitedEmail(new StringFilter());
        }
        return invitedEmail;
    }

    public void setInvitedEmail(StringFilter invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getBusinessId() {
        return businessId;
    }

    public Optional<LongFilter> optionalBusinessId() {
        return Optional.ofNullable(businessId);
    }

    public LongFilter businessId() {
        if (businessId == null) {
            setBusinessId(new LongFilter());
        }
        return businessId;
    }

    public void setBusinessId(LongFilter businessId) {
        this.businessId = businessId;
    }

    public LongFilter getInvitedById() {
        return invitedById;
    }

    public Optional<LongFilter> optionalInvitedById() {
        return Optional.ofNullable(invitedById);
    }

    public LongFilter invitedById() {
        if (invitedById == null) {
            setInvitedById(new LongFilter());
        }
        return invitedById;
    }

    public void setInvitedById(LongFilter invitedById) {
        this.invitedById = invitedById;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BusinessInvitationCriteria that = (BusinessInvitationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(role, that.role) &&
            Objects.equals(token, that.token) &&
            Objects.equals(invitedEmail, that.invitedEmail) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(invitedById, that.invitedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, token, invitedEmail, createdAt, updatedAt, businessId, invitedById, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BusinessInvitationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalToken().map(f -> "token=" + f + ", ").orElse("") +
            optionalInvitedEmail().map(f -> "invitedEmail=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalBusinessId().map(f -> "businessId=" + f + ", ").orElse("") +
            optionalInvitedById().map(f -> "invitedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
