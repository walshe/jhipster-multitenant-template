package com.walshe.multitenant.service.util;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.service.UserService;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Utility class for business authorization checks.
 */
@Component
public class BusinessAuthorizationUtil {

    private final UserService userService;
    private final BusinessUserRepository businessUserRepository;

    public BusinessAuthorizationUtil(UserService userService, BusinessUserRepository businessUserRepository) {
        this.userService = userService;
        this.businessUserRepository = businessUserRepository;
    }

    /**
     * Check if the current user is the owner of the specified business.
     *
     * @param businessId the ID of the business to check
     * @return true if the current user is the owner of the business, false otherwise
     */
    public boolean isBusinessOwner(Long businessId) {
        Optional<User> currentUserOpt = userService.getUserWithAuthorities();
        if (currentUserOpt.isEmpty()) {
            return false;
        }

        User currentUser = currentUserOpt.get();
        return isBusinessOwner(businessId, currentUser.getId());
    }

    /**
     * Check if the specified user is the owner of the specified business.
     *
     * @param businessId the ID of the business to check
     * @param userId the ID of the user to check
     * @return true if the user is the owner of the business, false otherwise
     */
    public boolean isBusinessOwner(Long businessId, Long userId) {
        Optional<Business> businessOpt = getBusinessById(businessId);
        if (businessOpt.isEmpty()) {
            return false;
        }

        Business business = businessOpt.get();
        return business.getOwner() != null && business.getOwner().getId().equals(userId);
    }

    /**
     * Check if the current user is a member of the specified business.
     *
     * @param businessId the ID of the business to check
     * @return true if the current user is a member of the business, false otherwise
     */
    public boolean isBusinessMember(Long businessId) {
        Optional<User> currentUserOpt = userService.getUserWithAuthorities();
        if (currentUserOpt.isEmpty()) {
            return false;
        }

        User currentUser = currentUserOpt.get();
        return isBusinessMember(businessId, currentUser.getId());
    }

    /**
     * Check if the specified user is a member of the specified business.
     *
     * @param businessId the ID of the business to check
     * @param userId the ID of the user to check
     * @return true if the user is a member of the business, false otherwise
     */
    public boolean isBusinessMember(Long businessId, Long userId) {
        // Check if the user is the owner of the business
        if (isBusinessOwner(businessId, userId)) {
            return true;
        }

        // Check if the user is a member of the business through BusinessUser relationship
        return businessUserRepository.findByBusinessIdAndUserId(businessId, userId).isPresent();
    }

    /**
     * Get a business by its ID.
     * This is a helper method that would need to be implemented based on your repository setup.
     *
     * @param businessId the ID of the business to retrieve
     * @return an Optional containing the business if found, empty otherwise
     */
    private Optional<Business> getBusinessById(Long businessId) {
        // This would typically be implemented by injecting BusinessRepository
        // For now, this is a placeholder - the actual implementation would depend on how
        // you want to access the BusinessRepository from this utility class
        return Optional.empty();
    }
}