package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.service.dto.BusinessUserDTO;
import com.walshe.multitenant.service.mapper.BusinessUserMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.walshe.multitenant.domain.BusinessUser}.
 */
@Service
@Transactional
public class BusinessUserService {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserService.class);

    private final BusinessUserRepository businessUserRepository;

    private final BusinessUserMapper businessUserMapper;

    private final BusinessAuthorizationService businessAuthorizationService;

    public BusinessUserService(BusinessUserRepository businessUserRepository, BusinessUserMapper businessUserMapper, BusinessAuthorizationService businessAuthorizationService) {
        this.businessUserRepository = businessUserRepository;
        this.businessUserMapper = businessUserMapper;
        this.businessAuthorizationService = businessAuthorizationService;
    }

    /**
     * Save a businessUser.
     *
     * @param businessUserDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessUserDTO save(BusinessUserDTO businessUserDTO) {
        LOG.debug("Request to save BusinessUser : {}", businessUserDTO);
        BusinessUser businessUser = businessUserMapper.toEntity(businessUserDTO);
        businessUser = businessUserRepository.save(businessUser);
        return businessUserMapper.toDto(businessUser);
    }

    /**
     * Update a businessUser.
     *
     * @param businessUserDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessUserDTO update(BusinessUserDTO businessUserDTO) {
        LOG.debug("Request to update BusinessUser : {}", businessUserDTO);
        BusinessUser businessUser = businessUserMapper.toEntity(businessUserDTO);
        businessUser = businessUserRepository.save(businessUser);
        return businessUserMapper.toDto(businessUser);
    }

    /**
     * Partially update a businessUser.
     *
     * @param businessUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BusinessUserDTO> partialUpdate(BusinessUserDTO businessUserDTO) {
        LOG.debug("Request to partially update BusinessUser : {}", businessUserDTO);

        return businessUserRepository
            .findById(businessUserDTO.getId())
            .map(existingBusinessUser -> {
                businessUserMapper.partialUpdate(existingBusinessUser, businessUserDTO);

                return existingBusinessUser;
            })
            .map(businessUserRepository::save)
            .map(businessUserMapper::toDto);
    }

    /**
     * Get all the businessUsers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BusinessUserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return businessUserRepository.findAllWithEagerRelationships(pageable).map(businessUserMapper::toDto);
    }

    /**
     * Get one businessUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BusinessUserDTO> findOne(Long id) {
        LOG.debug("Request to get BusinessUser : {}", id);
        return businessUserRepository
            .findOneWithEagerRelationships(id)
            .filter(entity -> entity.getBusiness() != null && businessAuthorizationService.isBusinessOwner(entity.getBusiness().getId()))
            .map(businessUserMapper::toDto);
    }

    /**
     * Delete the businessUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BusinessUser : {}", id);
        businessUserRepository.deleteById(id);
    }
    
    /**
     * Create a business-user relationship.
     *
     * @param business the business to join
     * @param user the user joining the business
     * @param role the role to assign to the user in the business
     * @return the created BusinessUser entity
     */
    public BusinessUser createBusinessUser(Business business, User user, BusinessRole role) {
        LOG.debug("Request to create BusinessUser relationship for user {} in business {} with role {}", 
                 user.getLogin(), business.getName(), role);
        
        // Check if the relationship already exists
        Optional<BusinessUser> existing = businessUserRepository.findByBusinessIdAndUserId(business.getId(), user.getId());
        if (existing.isPresent()) {
            // If it exists, just update the role
            BusinessUser businessUser = existing.get();
            businessUser.setRole(role);
            return businessUserRepository.save(businessUser);
        } else {
            // Create new relationship
            BusinessUser businessUser = new BusinessUser();
            businessUser.setBusiness(business);
            businessUser.setUser(user);
            businessUser.setRole(role);
            return businessUserRepository.save(businessUser);
        }
    }
}
