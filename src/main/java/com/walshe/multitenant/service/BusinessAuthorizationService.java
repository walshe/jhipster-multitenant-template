package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.service.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service class for business authorization checks.
 */
@Component
public class BusinessAuthorizationService {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessAuthorizationService.class);

    private final UserService userService;
    private final BusinessUserRepository businessUserRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public BusinessAuthorizationService(
        UserService userService,
        BusinessUserRepository businessUserRepository,
        BusinessRepository businessRepository,
        UserRepository userRepository
    ) {
        this.userService = userService;
        this.businessUserRepository = businessUserRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
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
        Optional<Business> businessOpt = businessRepository.findById(businessId);

        if (businessOpt.isEmpty() || businessOpt.get().getOwner() == null) {
            return false;
        }

        User owner = businessOpt.get().getOwner();
        return owner.getId().equals(userId);
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
}