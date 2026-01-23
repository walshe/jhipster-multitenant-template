package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.mapper.BusinessMapper;
import java.util.Optional;
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

    private final BusinessRepository businessRepository;

    private final BusinessMapper businessMapper;

    public BusinessService(BusinessRepository businessRepository, BusinessMapper businessMapper) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
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
        Business business = businessMapper.toEntity(businessDTO);
        business = businessRepository.save(business);
        return businessMapper.toDto(business);
    }

    /**
     * Partially update a business.
     *
     * @param businessDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BusinessDTO> partialUpdate(BusinessDTO businessDTO) {
        LOG.debug("Request to partially update Business : {}", businessDTO);

        return businessRepository
            .findById(businessDTO.getId())
            .map(existingBusiness -> {
                businessMapper.partialUpdate(existingBusiness, businessDTO);

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
     * Get one business by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BusinessDTO> findOne(Long id) {
        LOG.debug("Request to get Business : {}", id);
        return businessRepository.findOneWithEagerRelationships(id).map(businessMapper::toDto);
    }

    /**
     * Delete the business by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Business : {}", id);
        businessRepository.deleteById(id);
    }
}
