package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.service.dto.BusinessInvitationDTO;
import com.walshe.multitenant.service.mapper.BusinessInvitationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.walshe.multitenant.domain.BusinessInvitation}.
 */
@Service
@Transactional
public class BusinessInvitationService {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessInvitationService.class);

    private final BusinessInvitationRepository businessInvitationRepository;

    private final BusinessInvitationMapper businessInvitationMapper;

    public BusinessInvitationService(
        BusinessInvitationRepository businessInvitationRepository,
        BusinessInvitationMapper businessInvitationMapper
    ) {
        this.businessInvitationRepository = businessInvitationRepository;
        this.businessInvitationMapper = businessInvitationMapper;
    }

    /**
     * Save a businessInvitation.
     *
     * @param businessInvitationDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessInvitationDTO save(BusinessInvitationDTO businessInvitationDTO) {
        LOG.debug("Request to save BusinessInvitation : {}", businessInvitationDTO);
        BusinessInvitation businessInvitation = businessInvitationMapper.toEntity(businessInvitationDTO);
        businessInvitation = businessInvitationRepository.save(businessInvitation);
        return businessInvitationMapper.toDto(businessInvitation);
    }

    /**
     * Update a businessInvitation.
     *
     * @param businessInvitationDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessInvitationDTO update(BusinessInvitationDTO businessInvitationDTO) {
        LOG.debug("Request to update BusinessInvitation : {}", businessInvitationDTO);
        BusinessInvitation businessInvitation = businessInvitationMapper.toEntity(businessInvitationDTO);
        businessInvitation = businessInvitationRepository.save(businessInvitation);
        return businessInvitationMapper.toDto(businessInvitation);
    }

    /**
     * Partially update a businessInvitation.
     *
     * @param businessInvitationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BusinessInvitationDTO> partialUpdate(BusinessInvitationDTO businessInvitationDTO) {
        LOG.debug("Request to partially update BusinessInvitation : {}", businessInvitationDTO);

        return businessInvitationRepository
            .findById(businessInvitationDTO.getId())
            .map(existingBusinessInvitation -> {
                businessInvitationMapper.partialUpdate(existingBusinessInvitation, businessInvitationDTO);

                return existingBusinessInvitation;
            })
            .map(businessInvitationRepository::save)
            .map(businessInvitationMapper::toDto);
    }

    /**
     * Get all the businessInvitations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BusinessInvitationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return businessInvitationRepository.findAllWithEagerRelationships(pageable).map(businessInvitationMapper::toDto);
    }

    /**
     * Get one businessInvitation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BusinessInvitationDTO> findOne(Long id) {
        LOG.debug("Request to get BusinessInvitation : {}", id);
        return businessInvitationRepository.findOneWithEagerRelationships(id).map(businessInvitationMapper::toDto);
    }

    /**
     * Delete the businessInvitation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BusinessInvitation : {}", id);
        businessInvitationRepository.deleteById(id);
    }
}
