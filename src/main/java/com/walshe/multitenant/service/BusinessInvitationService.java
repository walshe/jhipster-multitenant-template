package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.domain.enumeration.InvitationStatus;
import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.security.SecurityUtils;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

/**
 * Service Implementation for managing {@link BusinessInvitation}.
 */
@Service
@Transactional
public class BusinessInvitationService {

    private final Logger log = LoggerFactory.getLogger(BusinessInvitationService.class);

    private final BusinessInvitationRepository businessInvitationRepository;
    
    private final BusinessRepository businessRepository;
    
    private final BusinessUserService businessUserService;
    
    private final UserRepository userRepository;

    public BusinessInvitationService(
        BusinessInvitationRepository businessInvitationRepository,
        BusinessRepository businessRepository,
        BusinessUserService businessUserService,
        UserRepository userRepository
    ) {
        this.businessInvitationRepository = businessInvitationRepository;
        this.businessRepository = businessRepository;
        this.businessUserService = businessUserService;
        this.userRepository = userRepository;
    }

    /**
     * Create a business invitation.
     *
     * @param invitedEmail the email of the person being invited
     * @param role the role to assign to the invited person
     * @param businessId the ID of the business to invite to
     * @return the persisted entity
     */
    public BusinessInvitation createInvitation(String invitedEmail, BusinessRole role, Long businessId) {
        log.debug("Request to create BusinessInvitation : {}", invitedEmail);

        // Check if business exists and belongs to current user
        Business business = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Business not found"));
        
        // Verify that current user is the owner of the business
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (!business.getOwner().getLogin().equals(currentUserLogin)) {
            throw new SecurityException("Only business owners can create invitations");
        }

        // Check if there's already a pending invitation for this email and business
        List<BusinessInvitation> existingInvitations = businessInvitationRepository.findByInvitedEmailAndBusinessId(invitedEmail, businessId);
        for (BusinessInvitation existing : existingInvitations) {
            if (existing.getStatus() == InvitationStatus.PENDING) {
                throw new IllegalStateException("There is already a pending invitation for this email address and business");
            }
        }

        // Create the invitation
        BusinessInvitation businessInvitation = new BusinessInvitation();
        businessInvitation.setInvitedEmail(invitedEmail);
        businessInvitation.setRole(role);
        businessInvitation.setStatus(InvitationStatus.PENDING); // Initially PENDING
        businessInvitation.setBusiness(business);
        businessInvitation.setToken(generateSecureToken());
        businessInvitation.setCreatedAt(Instant.now());
        businessInvitation.setUpdatedAt(Instant.now());

        // Automatically set the inviting user to the current user
        User currentUser = getCurrentUser()
            .orElseThrow(() -> new IllegalStateException("Current user must be authenticated to create an invitation"));
        businessInvitation.setInvitedBy(currentUser);

        return businessInvitationRepository.save(businessInvitation);
    }

    /**
     * Get all invitations for a specific business.
     *
     * @param businessId the ID of the business
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BusinessInvitation> findAllByBusiness(Long businessId, Pageable pageable) {
        log.debug("Request to get all BusinessInvitations for business : {}", businessId);

        // Verify that current user is the owner of the business
        Business business = businessRepository.findById(businessId)
            .orElseThrow(() -> new IllegalArgumentException("Business not found"));
        
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (!business.getOwner().getLogin().equals(currentUserLogin)) {
            throw new SecurityException("Access denied: Only business owners can view invitations");
        }

        return businessInvitationRepository.findByBusinessIdAndStatus(businessId, InvitationStatus.PENDING, pageable);
    }

    /**
     * Get one business invitation by ID.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<BusinessInvitation> findOne(Long id) {
        log.debug("Request to get BusinessInvitation : {}", id);
        return businessInvitationRepository.findById(id);
    }

    /**
     * Delete the business invitation by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete BusinessInvitation : {}", id);

        BusinessInvitation invitation = businessInvitationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        // Verify that current user is the owner of the business
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (!invitation.getBusiness().getOwner().getLogin().equals(currentUserLogin)) {
            throw new SecurityException("Access denied: Only business owners can delete invitations");
        }

        // Only allow deletion of PENDING invitations
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalStateException("Only pending invitations can be deleted");
        }

        businessInvitationRepository.deleteById(id);
    }

    /**
     * Find invitation by token (for public access).
     *
     * @param token the invitation token
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<BusinessInvitation> findByToken(String token) {
        log.debug("Request to get BusinessInvitation by token: {}", token);
        return businessInvitationRepository.findByToken(token);
    }

    /**
     * Accept an invitation.
     *
     * @param token the invitation token
     * @return the updated invitation
     */
    public BusinessInvitation acceptInvitation(String token) {
        log.debug("Request to accept BusinessInvitation with token: {}", token);

        BusinessInvitation invitation = businessInvitationRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));

        // Only allow accepting PENDING invitations
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation is not in PENDING state");
        }

        // Update status to ACCEPTED
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setUpdatedAt(Instant.now());

        // Save the updated invitation
        BusinessInvitation savedInvitation = businessInvitationRepository.save(invitation);

        // Create the business user relationship
        User currentUser = getCurrentUser()
            .orElseThrow(() -> new IllegalStateException("User must be authenticated to accept invitation"));

        // Create the business-user relationship
        businessUserService.createBusinessUser(savedInvitation.getBusiness(), currentUser, savedInvitation.getRole());

        return savedInvitation;
    }

    /**
     * Generate a cryptographically secure random token.
     *
     * @return a unique token string
     */
    private String generateSecureToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Get the current authenticated user.
     *
     * @return the current user wrapped in an Optional
     */
    private Optional<User> getCurrentUser() {
        return SecurityUtils.getCurrentUserLogin().flatMap(login ->
            userRepository.findOneByLogin(login)
        );
    }

    /**
     * Get all invitations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BusinessInvitation> findAll(Pageable pageable) {
        log.debug("Request to get all BusinessInvitations");
        return businessInvitationRepository.findAll(pageable);
    }

    /**
     * Update a businessInvitation.
     *
     * @param businessInvitation the entity to save
     * @return the persisted entity
     */
    public BusinessInvitation update(BusinessInvitation businessInvitation) {
        log.debug("Request to update BusinessInvitation : {}", businessInvitation);
        
        // Retrieve the existing invitation to preserve certain fields
        BusinessInvitation existingInvitation = businessInvitationRepository.findById(businessInvitation.getId())
            .orElseThrow(() -> new IllegalArgumentException("Business invitation not found"));
            
        // Preserve the invitedBy field (cannot be changed after creation)
        businessInvitation.setInvitedBy(existingInvitation.getInvitedBy());
        
        return businessInvitationRepository.save(businessInvitation);
    }

    /**
     * Partially update a businessInvitation.
     *
     * @param businessInvitation the entity to update partially
     * @return the persisted entity
     */
    public Optional<BusinessInvitation> partialUpdate(BusinessInvitation businessInvitation) {
        log.debug("Request to partially update BusinessInvitation : {}", businessInvitation);

        return businessInvitationRepository
            .findById(businessInvitation.getId())
            .map(existingBusinessInvitation -> {
                // Only update fields that are not null
                if (businessInvitation.getRole() != null) {
                    existingBusinessInvitation.setRole(businessInvitation.getRole());
                }
                if (businessInvitation.getToken() != null) {
                    existingBusinessInvitation.setToken(businessInvitation.getToken());
                }
                if (businessInvitation.getInvitedEmail() != null) {
                    existingBusinessInvitation.setInvitedEmail(businessInvitation.getInvitedEmail());
                }
                if (businessInvitation.getStatus() != null) {
                    existingBusinessInvitation.setStatus(businessInvitation.getStatus());
                }
                if (businessInvitation.getCreatedAt() != null) {
                    existingBusinessInvitation.setCreatedAt(businessInvitation.getCreatedAt());
                }
                if (businessInvitation.getUpdatedAt() != null) {
                    existingBusinessInvitation.setUpdatedAt(businessInvitation.getUpdatedAt());
                }
                if (businessInvitation.getBusiness() != null) {
                    existingBusinessInvitation.setBusiness(businessInvitation.getBusiness());
                }
                // Skip updating invitedBy field (preserves original value)

                return businessInvitationRepository.save(existingBusinessInvitation);
            });
    }

    /**
     * Check if the current user can modify an invitation.
     * Only the business owner can modify invitations for their business.
     *
     * @param invitationId the ID of the invitation to check
     * @return true if the current user can modify the invitation, false otherwise
     */
    @Transactional(readOnly = true)
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
        Optional<Business> businessOpt = businessRepository.findById(businessId);
        if (businessOpt.isEmpty()) {
            return false;
        }

        Business business = businessOpt.get();
        return business.getOwner().getId().equals(currentUser.getId());
    }
}