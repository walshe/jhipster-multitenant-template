package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.*; // for static metamodels
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.service.criteria.BusinessUserCriteria;
import com.walshe.multitenant.service.dto.BusinessUserDTO;
import com.walshe.multitenant.service.mapper.BusinessUserMapper;
import com.walshe.multitenant.security.SecurityUtils;
import jakarta.persistence.criteria.JoinType;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BusinessUser} entities in the database.
 * The main input is a {@link BusinessUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BusinessUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BusinessUserQueryService extends QueryService<BusinessUser> {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserQueryService.class);

    private final BusinessUserRepository businessUserRepository;

    private final BusinessUserMapper businessUserMapper;

    private final UserService userService;

    public BusinessUserQueryService(BusinessUserRepository businessUserRepository, BusinessUserMapper businessUserMapper, UserService userService) {
        this.businessUserRepository = businessUserRepository;
        this.businessUserMapper = businessUserMapper;
        this.userService = userService;
    }

    /**
     * Return a {@link Page} of {@link BusinessUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BusinessUserDTO> findByCriteria(BusinessUserCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BusinessUser> specification = createSpecification(criteria);
        return businessUserRepository.findAll(specification, page).map(businessUserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BusinessUserCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BusinessUser> specification = createSpecification(criteria);
        return businessUserRepository.count(specification);
    }

    /**
     * Function to convert {@link BusinessUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BusinessUser> createSpecification(BusinessUserCriteria criteria) {
        Specification<BusinessUser> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), BusinessUser_.id),
                buildSpecification(criteria.getRole(), BusinessUser_.role),
                buildRangeSpecification(criteria.getCreatedAt(), BusinessUser_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), BusinessUser_.updatedAt),
                buildSpecification(criteria.getBusinessId(), root -> root.join(BusinessUser_.business, JoinType.LEFT).get(Business_.id)),
                buildSpecification(criteria.getUserId(), root -> root.join(BusinessUser_.user, JoinType.LEFT).get(User_.id))
            );
        }

        // Enforce owner-only visibility: business.owner.id == current user
        Optional<com.walshe.multitenant.domain.User> currentUserOpt = userService.getUserWithAuthorities();
        if (currentUserOpt.isPresent()) {
            Long userId = currentUserOpt.get().getId();
            specification = specification.and((root, query, cb) -> cb.equal(root.join(BusinessUser_.business, JoinType.LEFT).join(Business_.owner, JoinType.LEFT).get(User_.id), userId));
        } else {
            Optional<String> loginOpt = SecurityUtils.getCurrentUserLogin();
            if (loginOpt.isPresent()) {
                String login = loginOpt.get();
                specification = specification.and((root, query, cb) -> cb.equal(root.join(BusinessUser_.business, JoinType.LEFT).join(Business_.owner, JoinType.LEFT).get(User_.login), login));
            } else {
                specification = specification.and((root, query, cb) -> cb.disjunction());
            }
        }

        return specification;
    }
}
