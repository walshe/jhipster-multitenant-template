package com.walshe.multitenant.service.dto;

import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.domain.enumeration.InvitationStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.walshe.multitenant.domain.BusinessInvitation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BusinessInvitationDTO implements Serializable {

    private Long id;

    @NotNull
    private BusinessRole role;

    @NotNull
    private String token;

    @NotNull
    private String invitedEmail;

    private Instant createdAt;

    private Instant updatedAt;

    private BusinessDTO business;

    private UserDTO invitedBy;

    @NotNull
    private InvitationStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessRole getRole() {
        return role;
    }

    public void setRole(BusinessRole role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BusinessDTO getBusiness() {
        return business;
    }

    public void setBusiness(BusinessDTO business) {
        this.business = business;
    }

    public UserDTO getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(UserDTO invitedBy) {
        this.invitedBy = invitedBy;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BusinessInvitationDTO)) {
            return false;
        }

        BusinessInvitationDTO businessInvitationDTO = (BusinessInvitationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, businessInvitationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BusinessInvitationDTO{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", token='" + getToken() + "'" +
            ", invitedEmail='" + getInvitedEmail() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", business=" + getBusiness() +
            ", invitedBy=" + getInvitedBy() +
            "}";
    }
}
