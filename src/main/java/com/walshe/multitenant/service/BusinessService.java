package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.security.SecurityUtils;
import com.walshe.multitenant.service.criteria.BusinessCriteria;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.dto.BusinessMemberDTO;
import com.walshe.multitenant.service.BusinessQueryService;
import com.walshe.multitenant.service.mapper.BusinessMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.walshe.multitenant.domain.Business}.
 */
@Service
@Transactional
public class BusinessService {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessService.class);

    private static final String ENTITY_NAME = "business";

    private final BusinessRepository businessRepository;

    private final BusinessUserRepository businessUserRepository;

    private final UserRepository userRepository;

    private final BusinessMapper businessMapper;

    private final BusinessQueryService businessQueryService;

    private final BusinessAuthorizationService businessAuthorizationService;

    public BusinessService(
        BusinessRepository businessRepository,
        BusinessUserRepository businessUserRepository,
        UserRepository userRepository,
        BusinessMapper businessMapper,
        BusinessQueryService businessQueryService,
        BusinessAuthorizationService businessAuthorizationService
    ) {
        this.businessRepository = businessRepository;
        this.businessUserRepository = businessUserRepository;
        this.userRepository = userRepository;
        this.businessMapper = businessMapper;
        this.businessQueryService = businessQueryService;
        this.businessAuthorizationService = businessAuthorizationService;
    }

    /**
     * Create a business with the specified owner.
     *
     * @param businessDTO the entity to save.
     * @param owner the owner of the business.
     * @return the persisted entity.
     */
    public BusinessDTO createWithOwner(BusinessDTO businessDTO, User owner) {
        LOG.debug("Request to save Business with owner: {}", businessDTO);
        Business business = businessMapper.toEntity(businessDTO);
        business.setOwner(owner);
        business = businessRepository.save(business);

        // Ensure the owner is added as a BusinessUser for this business if not already one
        ensureOwnerIsBusinessMember(business, owner);

        return businessMapper.toDto(business);
    }

    /**
     * Validates that the user is a member of the business.
     *
     * @param businessId the ID of the business.
     * @param userId the ID of the user.
     * @throws com.walshe.multitenant.web.rest.errors.InvalidBusinessOwnershipException if the user is not a member of the business.
     */
    public void validateBusinessOwnership(Long businessId, Long userId) {
        Optional<BusinessUser> businessUser = businessUserRepository.findByBusinessIdAndUserId(businessId, userId);
        if (businessUser.isEmpty()) {
            throw new com.walshe.multitenant.service.errors.InvalidBusinessOwnershipException(
                businessId.toString(), userId.toString());
        }
    }

    /**
     * Helper method to find a user by ID.
     *
     * @param userId the ID of the user.
     * @return the User entity.
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * Check if a user is a member of the specified business.
     *
     * @param businessId the ID of the business.
     * @param userId the ID of the user.
     * @return true if the user is a member of the business, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean isUserBusinessMember(Long businessId, Long userId) {
        return businessUserRepository.findByBusinessIdAndUserId(businessId, userId).isPresent();
    }

    /**
     * Get all users who are members of the specified business.
     *
     * @param businessId the ID of the business.
     * @return a list of BusinessMemberDTO objects representing users who are members of the business.
     */
    @Transactional(readOnly = true)
    public List<BusinessMemberDTO> getBusinessMembers(Long businessId) {
        // First verify that the business exists
        if (!businessRepository.existsById(businessId)) {
            throw new IllegalArgumentException("Business not found with ID: " + businessId);
        }

        // Find all BusinessUser records for this business
        List<BusinessUser> businessUsers = businessUserRepository.findByBusinessId(businessId);

        // Convert to BusinessMemberDTO
        return businessUsers.stream()
            .map(businessUser -> {
                User user = businessUser.getUser();
                BusinessMemberDTO dto = new BusinessMemberDTO();
                dto.setId(user.getId());
                dto.setLogin(user.getLogin());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Ensure the owner is added as a BusinessUser for this business if not already one.
     *
     * @param business the business entity.
     * @param owner the owner of the business.
     */
    private void ensureOwnerIsBusinessMember(Business business, User owner) {
        // Check if a BusinessUser relationship already exists between the business and owner
        Optional<BusinessUser> existingBusinessUser = businessUserRepository.findByBusinessIdAndUserId(business.getId(), owner.getId());

        if (existingBusinessUser.isEmpty()) {
            // Create a new BusinessUser relationship with MEMBER role
            BusinessUser businessUser = new BusinessUser();
            businessUser.setBusiness(business);
            businessUser.setUser(owner);
            businessUser.setRole(com.walshe.multitenant.domain.enumeration.BusinessRole.MEMBER);
            businessUser.setCreatedAt(java.time.Instant.now());
            businessUser.setUpdatedAt(java.time.Instant.now());

            businessUserRepository.save(businessUser);
        }
    }

    /**
     * Save a business.
     *
     * @param businessDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessDTO save(BusinessDTO businessDTO) {
        LOG.debug("Request to save Business : {}", businessDTO);
        Business business = businessMapper.toEntity(businessDTO);

        // Ensure the owner is set for new businesses
        if (business.getId() == null && business.getOwner() == null) {
            throw new IllegalArgumentException("Owner must be set when creating a new business");
        }

        business = businessRepository.save(business);
        return businessMapper.toDto(business);
    }

    /**
     * Update a business.
     *
     * @param businessDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessDTO update(BusinessDTO businessDTO) {
        LOG.debug("Request to update Business : {}", businessDTO);

        // Check if the current user is the owner of the business
        if (!businessAuthorizationService.isBusinessOwner(businessDTO.getId())) {
            throw new com.walshe.multitenant.service.errors.InvalidBusinessOwnershipException(
                businessDTO.getId().toString(), "User is not authorized to update this business");
        }

        Business existingBusiness = businessRepository.findById(businessDTO.getId()).orElse(null);
        if (existingBusiness != null) {
            // Check if owner is being changed and validate ownership
            if (businessDTO.getOwner() != null &&
                existingBusiness.getOwner() != null &&
                !existingBusiness.getOwner().getId().equals(businessDTO.getOwner().getId())) {
                validateBusinessOwnership(businessDTO.getId(), businessDTO.getOwner().getId());
                // If validation passes, allow the owner change
                existingBusiness.setOwner(findUserById(businessDTO.getOwner().getId()));
            } else if (businessDTO.getOwner() != null && existingBusiness.getOwner() == null) {
                // If setting owner for the first time, validate ownership
                validateBusinessOwnership(businessDTO.getId(), businessDTO.getOwner().getId());
                // If validation passes, allow the owner change
                existingBusiness.setOwner(findUserById(businessDTO.getOwner().getId()));
            }

            // Update other fields from DTO
            existingBusiness.setName(businessDTO.getName());
            existingBusiness.setSlug(businessDTO.getSlug());
            existingBusiness.setCreatedAt(businessDTO.getCreatedAt());
            existingBusiness.setUpdatedAt(businessDTO.getUpdatedAt());

            existingBusiness = businessRepository.save(existingBusiness);

            // If the owner was changed or set, ensure they are added as a BusinessUser for this business
            if (businessDTO.getOwner() != null) {
                User owner = findUserById(businessDTO.getOwner().getId());
                if (owner != null) {
                    ensureOwnerIsBusinessMember(existingBusiness, owner);
                }
            }

            return businessMapper.toDto(existingBusiness);
        } else {
            // Business doesn't exist, proceed with normal save
            Business business = businessMapper.toEntity(businessDTO);
            business = businessRepository.save(business);
            return businessMapper.toDto(business);
        }
    }

    /**
     * Partially update a business.
     *
     * @param businessDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BusinessDTO> partialUpdate(BusinessDTO businessDTO) {
        LOG.debug("Request to partially update Business : {}", businessDTO);

        // Check if the current user is the owner of the business
        if (!businessAuthorizationService.isBusinessOwner(businessDTO.getId())) {
            throw new com.walshe.multitenant.service.errors.InvalidBusinessOwnershipException(
                businessDTO.getId().toString(), "User is not authorized to partially update this business");
        }

        return businessRepository
            .findById(businessDTO.getId())
            .map(existingBusiness -> {
                // Check if owner is being changed and validate ownership
                if (businessDTO.getOwner() != null &&
                    existingBusiness.getOwner() != null &&
                    !existingBusiness.getOwner().getId().equals(businessDTO.getOwner().getId())) {
                    validateBusinessOwnership(businessDTO.getId(), businessDTO.getOwner().getId());
                    // If validation passes, allow the owner change
                    existingBusiness.setOwner(findUserById(businessDTO.getOwner().getId()));
                } else if (businessDTO.getOwner() != null && existingBusiness.getOwner() == null) {
                    // If setting owner for the first time, validate ownership
                    validateBusinessOwnership(businessDTO.getId(), businessDTO.getOwner().getId());
                    // If validation passes, allow the owner change
                    existingBusiness.setOwner(findUserById(businessDTO.getOwner().getId()));
                } else {
                    // Preserve the original owner during partial updates
                    // Apply partial updates to other fields
                    businessMapper.partialUpdate(existingBusiness, businessDTO);
                }

                // If the owner was changed or set, ensure they are added as a BusinessUser for this business
                if (businessDTO.getOwner() != null) {
                    User owner = findUserById(businessDTO.getOwner().getId());
                    if (owner != null) {
                        ensureOwnerIsBusinessMember(existingBusiness, owner);
                    }
                }

                return existingBusiness;
            })
            .map(businessRepository::save)
            .map(businessMapper::toDto);
    }

    /**
     * Get all the businesses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BusinessDTO> findAllWithEagerRelationships(Pageable pageable) {
        return businessRepository.findAllWithEagerRelationships(pageable).map(businessMapper::toDto);
    }

    /**
     * Get businesses filtered to only those the current user is a member of.
     *
     * @param criteria the criteria which the requested entities should match.
     * @param pageable the pagination information.
     * @return the list of entities that the current user is a member of.
     */
    @Transactional(readOnly = true)
    public Page<BusinessDTO> findBusinessesForMember(BusinessCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get businesses for member with criteria: {}", criteria);

        // The BusinessQueryService already implements membership-based filtering in its createSpecification method
        // This method essentially delegates to the existing filtering logic
        return businessQueryService.findByCriteria(criteria, pageable);
    }
    
    /**
     * Get all businesses owned by the current user.
     *
     * @param criteria the criteria which the requested entities should match.
     * @param pageable the pagination information.
     * @return the page of businesses owned by the current user.
     */
    @Transactional(readOnly = true)
    public Page<BusinessDTO> findBusinessesOwnedByCurrentUser(BusinessCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get businesses owned by current user with criteria: {}", criteria);

        // Get current user's ID
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            // Return empty page if no user is logged in
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // Create a modified criteria to filter by current user as owner
        BusinessCriteria ownedCriteria = new BusinessCriteria();
        if (criteria != null) {
            // Copy existing criteria
            ownedCriteria = new BusinessCriteria(criteria);
        }
        
        // Add owner filter for current user
        ownedCriteria.ownerId().setEquals(currentUserId);

        // Use a custom query method that doesn't apply membership filtering
        return findBusinessesByOwnerId(currentUserId, pageable);
    }
    
    /**
     * Custom method to find businesses by owner ID without membership filtering.
     *
     * @param ownerId the ID of the owner
     * @param pageable the pagination information
     * @return the page of businesses owned by the specified user
     */
    @Transactional(readOnly = true)
    public Page<BusinessDTO> findBusinessesByOwnerId(Long ownerId, Pageable pageable) {
        LOG.debug("Request to get businesses by owner ID: {}", ownerId);
        
        // Create criteria to filter by owner ID
        BusinessCriteria criteria = new BusinessCriteria();
        criteria.ownerId().setEquals(ownerId);
        
        // Use the basic specification without membership filtering
        Specification<Business> specification = businessQueryService.createBasicSpecification(criteria);
        
        Page<Business> businesses = businessRepository.findAll(specification, pageable);
        return businesses.map(businessMapper::toDto);
    }
    
    /**
     * Helper method to get the current user's ID.
     *
     * @return the ID of the current user, or null if not authenticated
     */
    private Long getCurrentUserId() {
        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentLogin != null) {
            Optional<User> currentUser = userRepository.findOneByLogin(currentLogin);
            return currentUser.map(User::getId).orElse(null);
        }
        return null;
    }

    /**
     * Get one business by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BusinessDTO> findOne(Long id) {
        LOG.debug("Request to get Business : {}", id);
        // Enforce membership/ownership visibility for single-entity fetch
        if (!businessAuthorizationService.isBusinessMember(id)) {
            return Optional.empty();
        }
        return businessRepository.findOneWithEagerRelationships(id).map(businessMapper::toDto);
    }

    /**
     * Delete the business by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Business : {}", id);

        // Check if the current user is the owner of the business
        if (!businessAuthorizationService.isBusinessOwner(id)) {
            throw new com.walshe.multitenant.service.errors.InvalidBusinessOwnershipException(
                id.toString(), "User is not authorized to delete this business");
        }

        businessRepository.deleteById(id);
    }
}
