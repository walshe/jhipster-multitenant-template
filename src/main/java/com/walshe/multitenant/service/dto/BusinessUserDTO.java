package com.walshe.multitenant.service.dto;

import com.walshe.multitenant.domain.enumeration.BusinessRole;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.walshe.multitenant.domain.BusinessUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BusinessUserDTO implements Serializable {

    private Long id;

    @NotNull
    private BusinessRole role;

    private Instant createdAt;

    private Instant updatedAt;

    private BusinessDTO business;

    private UserDTO user;

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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BusinessUserDTO)) {
            return false;
        }

        BusinessUserDTO businessUserDTO = (BusinessUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, businessUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BusinessUserDTO{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", business=" + getBusiness() +
            ", user=" + getUser() +
            "}";
    }
}
