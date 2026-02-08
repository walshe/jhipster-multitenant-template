package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.*; // for static metamodels
import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.service.criteria.BusinessInvitationCriteria;
import com.walshe.multitenant.service.dto.BusinessInvitationDTO;
import com.walshe.multitenant.service.mapper.BusinessInvitationMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BusinessInvitation} entities in the database.
 * The main input is a {@link BusinessInvitationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BusinessInvitationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BusinessInvitationQueryService extends QueryService<BusinessInvitation> {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessInvitationQueryService.class);

    private final BusinessInvitationRepository businessInvitationRepository;

    private final BusinessInvitationMapper businessInvitationMapper;

    public BusinessInvitationQueryService(
        BusinessInvitationRepository businessInvitationRepository,
        BusinessInvitationMapper businessInvitationMapper
    ) {
        this.businessInvitationRepository = businessInvitationRepository;
        this.businessInvitationMapper = businessInvitationMapper;
    }

    /**
     * Return a {@link Page} of {@link BusinessInvitationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BusinessInvitationDTO> findByCriteria(BusinessInvitationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BusinessInvitation> specification = createSpecification(criteria);
        return businessInvitationRepository.findAll(specification, page).map(businessInvitationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BusinessInvitationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BusinessInvitation> specification = createSpecification(criteria);
        return businessInvitationRepository.count(specification);
    }

    /**
     * Function to convert {@link BusinessInvitationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BusinessInvitation> createSpecification(BusinessInvitationCriteria criteria) {
        Specification<BusinessInvitation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildSpecification(criteria.getRole(), BusinessInvitation_.role),
                buildStringSpecification(criteria.getToken(), BusinessInvitation_.token),
                buildStringSpecification(criteria.getInvitedEmail(), BusinessInvitation_.invitedEmail),
                buildRangeSpecification(criteria.getCreatedAt(), BusinessInvitation_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), BusinessInvitation_.updatedAt),
                buildSpecification(criteria.getBusinessId(), root ->
                    root.join(BusinessInvitation_.business, JoinType.LEFT).get(Business_.id)
                ),
                buildSpecification(criteria.getInvitedById(), root -> root.join(BusinessInvitation_.invitedBy, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getStatus(), BusinessInvitation_.status)
            );
        }
        return specification;
    }
}
