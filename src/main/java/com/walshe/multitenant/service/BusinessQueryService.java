package com.walshe.multitenant.service;

import com.walshe.multitenant.domain.*; // for static metamodels
import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.service.criteria.BusinessCriteria;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.mapper.BusinessMapper;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Subquery;
import java.util.Optional;
import org.slf4j.Logger;
import com.walshe.multitenant.security.SecurityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Business} entities in the database.
 * The main input is a {@link BusinessCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BusinessDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BusinessQueryService extends QueryService<Business> {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessQueryService.class);

    private final BusinessRepository businessRepository;

    private final BusinessMapper businessMapper;

    private final UserService userService;

    public BusinessQueryService(BusinessRepository businessRepository, BusinessMapper businessMapper, UserService userService) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
        this.userService = userService;
    }

    /**
     * Return a {@link Page} of {@link BusinessDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BusinessDTO> findByCriteria(BusinessCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Business> specification = createSpecification(criteria);
        return businessRepository.findAll(specification, page).map(businessMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BusinessCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Business> specification = createSpecification(criteria);
        return businessRepository.count(specification);
    }

    /**
     * Function to convert {@link BusinessCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Business> createSpecification(BusinessCriteria criteria) {
        Specification<Business> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Business_.id),
                buildStringSpecification(criteria.getName(), Business_.name),
                buildStringSpecification(criteria.getSlug(), Business_.slug),
                buildRangeSpecification(criteria.getCreatedAt(), Business_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Business_.updatedAt),
                buildSpecification(criteria.getOwnerId(), root -> root.join(Business_.owner, JoinType.LEFT).get(User_.id))
            );
        }

        // Enforce membership-based visibility for authenticated users
        Optional<com.walshe.multitenant.domain.User> currentUserOpt = userService.getUserWithAuthorities();
        if (currentUserOpt.isPresent()) {
            Long userId = currentUserOpt.get().getId();
            specification = specification.and(membershipSpecificationForUser(userId));
        } else {
            // Fallback to login-based predicate if User entity is not resolved (e.g., some tests)
            Optional<String> loginOpt = SecurityUtils.getCurrentUserLogin();
            if (loginOpt.isPresent()) {
                specification = specification.and(membershipSpecificationForLogin(loginOpt.get()));
            } else {
                // If unauthenticated, return no results to avoid leakage
                specification = specification.and((root, query, cb) -> cb.disjunction());
            }
        }

        return specification;
    }

    private Specification<Business> membershipSpecificationForUser(Long userId) {
        return (root, query, cb) -> {
            // owner predicate
            var ownerPredicate = cb.equal(root.join(Business_.owner, JoinType.LEFT).get(User_.id), userId);

            // exists predicate on BusinessUser linking current user to the business
            Subquery<Long> sub = query.subquery(Long.class);
            var bu = sub.from(BusinessUser.class);
            sub.select(cb.literal(1L));
            var businessMatch = cb.equal(bu.get(BusinessUser_.business).get(Business_.id), root.get(Business_.id));
            var userMatch = cb.equal(bu.get(BusinessUser_.user).get(User_.id), userId);
            sub.where(cb.and(businessMatch, userMatch));

            return cb.or(ownerPredicate, cb.exists(sub));
        };
    }

    private Specification<Business> membershipSpecificationForLogin(String login) {
        return (root, query, cb) -> {
            var ownerPredicate = cb.equal(root.join(Business_.owner, JoinType.LEFT).get(User_.login), login);

            Subquery<Long> sub = query.subquery(Long.class);
            var bu = sub.from(BusinessUser.class);
            sub.select(cb.literal(1L));
            var businessMatch = cb.equal(bu.get(BusinessUser_.business).get(Business_.id), root.get(Business_.id));
            var userMatch = cb.equal(bu.get(BusinessUser_.user).get(User_.login), login);
            sub.where(cb.and(businessMatch, userMatch));

            return cb.or(ownerPredicate, cb.exists(sub));
        };
    }
}
