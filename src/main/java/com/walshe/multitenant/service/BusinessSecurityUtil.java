package com.walshe.multitenant.service;

import com.walshe.multitenant.service.BusinessAuthorizationService;
import com.walshe.multitenant.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility class for business-related security checks.
 * Provides methods that can be used with Spring Security's @PreAuthorize and @PostAuthorize annotations.
 */
@Component("businessSecurity")
public class BusinessSecurityUtil {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessSecurityUtil.class);

    private final BusinessAuthorizationService businessAuthorizationService;
    private final UserService userService;

    public BusinessSecurityUtil(BusinessAuthorizationService businessAuthorizationService, UserService userService) {
        this.businessAuthorizationService = businessAuthorizationService;
        this.userService = userService;
    }

    /**
     * Check if the current user is a member of the specified business.
     *
     * @param businessId the ID of the business to check
     * @return true if the current user is a member of the business, false otherwise
     */
    public boolean isBusinessMember(Long businessId) {
        LOG.debug("Checking if current user is a member of business ID: {}", businessId);
        if (businessId == null) {
            LOG.warn("Business ID is null, returning false for membership check");
            return false;
        }
        boolean isMember = businessAuthorizationService.isBusinessMember(businessId);
        LOG.debug("User is {}a member of business ID: {}", isMember ? "" : "NOT ", businessId);
        return isMember;
    }

    /**
     * Check if the current user is the owner of the specified business.
     *
     * @param businessId the ID of the business to check
     * @return true if the current user is the owner of the business, false otherwise
     */
    public boolean isBusinessOwner(Long businessId) {
        LOG.debug("Checking if current user is owner of business ID: {}", businessId);
        if (businessId == null) {
            LOG.warn("Business ID is null, returning false for ownership check");
            return false;
        }
        boolean isOwner = businessAuthorizationService.isBusinessOwner(businessId);
        LOG.debug("User is {}the owner of business ID: {}", isOwner ? "" : "NOT ", businessId);
        return isOwner;
    }

    /**
     * Check if the current user is authenticated.
     *
     * @return true if the current user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        var currentUserOpt = userService.getUserWithAuthorities();
        boolean isAuthenticated = currentUserOpt.isPresent();
        LOG.debug("User is {}authenticated", isAuthenticated ? "" : "NOT ");
        return isAuthenticated;
    }
}