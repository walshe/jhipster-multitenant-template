package com.walshe.multitenant.security;

import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.security.SecurityUtils;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("businessInvitationSecurity")
public class BusinessInvitationSecurity {

    private final Logger log = LoggerFactory.getLogger(BusinessInvitationSecurity.class);

    private final BusinessInvitationRepository businessInvitationRepository;
    private final UserRepository userRepository;

    public BusinessInvitationSecurity(BusinessInvitationRepository businessInvitationRepository, UserRepository userRepository) {
        this.businessInvitationRepository = businessInvitationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Check if the current user can modify an invitation.
     * Only the business owner can modify invitations for their business.
     *
     * @param invitationId the ID of the invitation to check
     * @return true if the current user can modify the invitation, false otherwise
     */
    public boolean canModifyInvitation(Long invitationId) {
        log.debug("Checking if user can modify invitation with ID: {}", invitationId);

        Optional<BusinessInvitation> invitationOpt = businessInvitationRepository.findById(invitationId);
        if (invitationOpt.isEmpty()) {
            log.debug("Invitation with ID {} not found", invitationId);
            return false;
        }

        BusinessInvitation invitation = invitationOpt.get();
        Long businessId = invitation.getBusiness().getId();

        // Check if the current user is the owner of the business
        Optional<String> currentLogin = SecurityUtils.getCurrentUserLogin();
        if (currentLogin.isEmpty()) {
            return false;
        }

        Optional<User> currentUserOpt = userRepository.findOneByLogin(currentLogin.get());
        if (currentUserOpt.isEmpty()) {
            return false;
        }

        User currentUser = currentUserOpt.get();
        Optional<com.walshe.multitenant.domain.Business> businessOpt = 
            Optional.ofNullable(invitation.getBusiness());
        if (businessOpt.isEmpty()) {
            return false;
        }

        com.walshe.multitenant.domain.Business business = businessOpt.get();
        return business.getOwner().getId().equals(currentUser.getId());
    }
}