package com.walshe.multitenant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.domain.enumeration.InvitationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

/**
 * An invitation that allows a person identified by email
 * to join a Business with a specific role.
 */
@Entity
@Table(name = "business_invitation")
public class BusinessInvitation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @NotNull
    @Column(name = "invited_email", nullable = false)
    private String invitedEmail;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private BusinessRole role;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvitationStatus status;

    @NotNull
    @Column(name = "token", nullable = false, unique = true)
    private String token;


    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnoreProperties(value = { "owner" }, allowSetters = true)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties(value = { "authorities" }, allowSetters = true)
    private User invitedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BusinessInvitation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return this.businessId;
    }

    public BusinessInvitation businessId(Long businessId) {
        this.setBusinessId(businessId);
        return this;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getInvitedEmail() {
        return this.invitedEmail;
    }

    public BusinessInvitation invitedEmail(String invitedEmail) {
        this.setInvitedEmail(invitedEmail);
        return this;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public BusinessRole getRole() {
        return this.role;
    }

    public BusinessInvitation role(BusinessRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(BusinessRole role) {
        this.role = role;
    }

    public InvitationStatus getStatus() {
        return this.status;
    }

    public BusinessInvitation status(InvitationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public String getToken() {
        return this.token;
    }

    public BusinessInvitation token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public BusinessInvitation createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public BusinessInvitation updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Business getBusiness() {
        return this.business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public BusinessInvitation business(Business business) {
        this.setBusiness(business);
        return this;
    }

    public User getInvitedBy() {
        return this.invitedBy;
    }

    public void setInvitedBy(User invitedBy) {
        this.invitedBy = invitedBy;
    }

    public BusinessInvitation invitedBy(User invitedBy) {
        this.setInvitedBy(invitedBy);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BusinessInvitation)) {
            return false;
        }
        BusinessInvitation businessInvitation = (BusinessInvitation) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, businessInvitation.id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BusinessInvitation{" +
            "id=" + getId() +
            ", businessId='" + getBusinessId() + "'" +
            ", invitedEmail='" + getInvitedEmail() + "'" +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", token='" + getToken() + "'" +
            ", invitedBy=" + getInvitedBy() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}