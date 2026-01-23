package com.walshe.multitenant.web.rest;

import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.service.BusinessUserQueryService;
import com.walshe.multitenant.service.BusinessUserService;
import com.walshe.multitenant.service.criteria.BusinessUserCriteria;
import com.walshe.multitenant.service.dto.BusinessUserDTO;
import com.walshe.multitenant.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.walshe.multitenant.domain.BusinessUser}.
 */
@RestController
@RequestMapping("/api/business-users")
public class BusinessUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserResource.class);

    private static final String ENTITY_NAME = "businessUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessUserService businessUserService;

    private final BusinessUserRepository businessUserRepository;

    private final BusinessUserQueryService businessUserQueryService;

    public BusinessUserResource(
        BusinessUserService businessUserService,
        BusinessUserRepository businessUserRepository,
        BusinessUserQueryService businessUserQueryService
    ) {
        this.businessUserService = businessUserService;
        this.businessUserRepository = businessUserRepository;
        this.businessUserQueryService = businessUserQueryService;
    }

    /**
     * {@code POST  /business-users} : Create a new businessUser.
     *
     * @param businessUserDTO the businessUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessUserDTO, or with status {@code 400 (Bad Request)} if the businessUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BusinessUserDTO> createBusinessUser(@Valid @RequestBody BusinessUserDTO businessUserDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BusinessUser : {}", businessUserDTO);
        if (businessUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new businessUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        businessUserDTO = businessUserService.save(businessUserDTO);
        return ResponseEntity.created(new URI("/api/business-users/" + businessUserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, businessUserDTO.getId().toString()))
            .body(businessUserDTO);
    }

    /**
     * {@code PUT  /business-users/:id} : Updates an existing businessUser.
     *
     * @param id the id of the businessUserDTO to save.
     * @param businessUserDTO the businessUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessUserDTO,
     * or with status {@code 400 (Bad Request)} if the businessUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BusinessUserDTO> updateBusinessUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BusinessUserDTO businessUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BusinessUser : {}, {}", id, businessUserDTO);
        if (businessUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        businessUserDTO = businessUserService.update(businessUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessUserDTO.getId().toString()))
            .body(businessUserDTO);
    }

    /**
     * {@code PATCH  /business-users/:id} : Partial updates given fields of an existing businessUser, field will ignore if it is null
     *
     * @param id the id of the businessUserDTO to save.
     * @param businessUserDTO the businessUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessUserDTO,
     * or with status {@code 400 (Bad Request)} if the businessUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the businessUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the businessUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BusinessUserDTO> partialUpdateBusinessUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BusinessUserDTO businessUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BusinessUser partially : {}, {}", id, businessUserDTO);
        if (businessUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BusinessUserDTO> result = businessUserService.partialUpdate(businessUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /business-users} : get all the businessUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessUsers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BusinessUserDTO>> getAllBusinessUsers(
        BusinessUserCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BusinessUsers by criteria: {}", criteria);

        Page<BusinessUserDTO> page = businessUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /business-users/count} : count all the businessUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBusinessUsers(BusinessUserCriteria criteria) {
        LOG.debug("REST request to count BusinessUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(businessUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /business-users/:id} : get the "id" businessUser.
     *
     * @param id the id of the businessUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BusinessUserDTO> getBusinessUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BusinessUser : {}", id);
        Optional<BusinessUserDTO> businessUserDTO = businessUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessUserDTO);
    }

    /**
     * {@code DELETE  /business-users/:id} : delete the "id" businessUser.
     *
     * @param id the id of the businessUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusinessUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BusinessUser : {}", id);
        businessUserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
