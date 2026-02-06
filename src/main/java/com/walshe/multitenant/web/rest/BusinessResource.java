package com.walshe.multitenant.web.rest;

import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.service.BusinessQueryService;
import com.walshe.multitenant.service.BusinessService;
import com.walshe.multitenant.service.UserService;
import com.walshe.multitenant.service.criteria.BusinessCriteria;
import com.walshe.multitenant.service.dto.BusinessCreateDTO;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.errors.InvalidBusinessOwnershipException;
import com.walshe.multitenant.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.walshe.multitenant.domain.Business}.
 */
@RestController
@RequestMapping("/api/businesses")
public class BusinessResource {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessResource.class);

    private static final String ENTITY_NAME = "business";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessService businessService;

    private final BusinessRepository businessRepository;

    private final BusinessQueryService businessQueryService;

    private final UserService userService;

    public BusinessResource(
        BusinessService businessService,
        BusinessRepository businessRepository,
        BusinessQueryService businessQueryService,
        UserService userService
    ) {
        this.businessService = businessService;
        this.businessRepository = businessRepository;
        this.businessQueryService = businessQueryService;
        this.userService = userService;
    }

    /**
     * {@code POST  /businesses} : Create a new business.
     *
     * @param businessDTO the businessDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessDTO, or with status {@code 400 (Bad Request)} if the business has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BusinessDTO> createBusiness(@Valid @RequestBody BusinessCreateDTO businessCreateDTO) throws URISyntaxException {
        LOG.debug("REST request to save Business : {}", businessCreateDTO);
        if (businessCreateDTO.getId() != null) {
            throw new BadRequestAlertException("A new business cannot already have an ID", ENTITY_NAME, "idexists");
        }
        // Get the authenticated user
        Optional<User> currentUserOpt = userService.getUserWithAuthorities();
        if (currentUserOpt.isEmpty()) {
            throw new BadRequestAlertException("User not authenticated", ENTITY_NAME, "notauthenticated");
        }

        // Convert to BusinessDTO and set the owner to the authenticated user
        BusinessDTO businessDTO = new BusinessDTO();
        businessDTO.setName(businessCreateDTO.getName());
        businessDTO.setSlug(businessCreateDTO.getSlug());
        businessDTO.setCreatedAt(businessCreateDTO.getCreatedAt());
        businessDTO.setUpdatedAt(businessCreateDTO.getUpdatedAt());

        businessDTO = businessService.createWithOwner(businessDTO, currentUserOpt.get());
        return ResponseEntity.created(new URI("/api/businesses/" + businessDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, businessDTO.getId().toString()))
            .body(businessDTO);
    }

    /**
     * {@code PUT  /businesses/:id} : Updates an existing business.
     *
     * @param id the id of the businessDTO to save.
     * @param businessDTO the businessDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessDTO,
     * or with status {@code 400 (Bad Request)} if the businessDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessSecurity.isBusinessOwner(#id)")
    public ResponseEntity<BusinessDTO> updateBusiness(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BusinessDTO businessDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Business : {}, {}", id, businessDTO);
        if (businessDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        try {
            businessDTO = businessService.update(businessDTO);
        } catch (InvalidBusinessOwnershipException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidowner");
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessDTO.getId().toString()))
            .body(businessDTO);
    }

    /**
     * {@code PATCH  /businesses/:id} : Partial updates given fields of an existing business, field will ignore if it is null
     *
     * @param id the id of the businessDTO to save.
     * @param businessDTO the businessDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessDTO,
     * or with status {@code 400 (Bad Request)} if the businessDTO is not valid,
     * or with status {@code 404 (Not Found)} if the businessDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the businessDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessSecurity.isBusinessOwner(#id)")
    public ResponseEntity<BusinessDTO> partialUpdateBusiness(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BusinessDTO businessDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Business partially : {}, {}", id, businessDTO);
        if (businessDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BusinessDTO> result;
        try {
            result = businessService.partialUpdate(businessDTO);
        } catch (InvalidBusinessOwnershipException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidowner");
        }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /businesses} : get all the businesses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businesses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BusinessDTO>> getAllBusinesses(
        BusinessCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Businesses by criteria: {}", criteria);

        // Filter businesses to only show those the current user is a member of
        Page<BusinessDTO> page = businessService.findBusinessesForMember(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    /**
     * {@code GET  /businesses/owned-by-current-user} : get all the businesses owned by the current user.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businesses in body.
     */
    @GetMapping("/owned-by-current-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BusinessDTO>> getBusinessesOwnedByCurrentUser(
        BusinessCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Businesses owned by current user by criteria: {}", criteria);

        Page<BusinessDTO> page = businessService.findBusinessesOwnedByCurrentUser(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /businesses/:id/members} : get all the members of a specific business.
     *
     * @param id the id of the business to retrieve members for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of business members.
     */
    @GetMapping("/{id}/members")
    @PreAuthorize("@businessSecurity.isBusinessOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<com.walshe.multitenant.service.dto.BusinessMemberDTO>> getBusinessMembers(@PathVariable("id") Long id) {
        LOG.debug("REST request to get members of Business : {}", id);

        List<com.walshe.multitenant.service.dto.BusinessMemberDTO> members = businessService.getBusinessMembers(id);
        return ResponseEntity.ok().body(members);
    }

    /**
     * {@code GET  /businesses/count} : count all the businesses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBusinesses(BusinessCriteria criteria) {
        LOG.debug("REST request to count Businesses by criteria: {}", criteria);
        return ResponseEntity.ok().body(businessQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /businesses/:id} : get the "id" business.
     *
     * @param id the id of the businessDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessSecurity.isBusinessMember(#id)")
    public ResponseEntity<BusinessDTO> getBusiness(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Business : {}", id);
        Optional<BusinessDTO> businessDTO = businessService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessDTO);
    }

    /**
     * {@code DELETE  /businesses/:id} : delete the "id" business.
     *
     * @param id the id of the businessDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessSecurity.isBusinessOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBusiness(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Business : {}", id);

        businessService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
