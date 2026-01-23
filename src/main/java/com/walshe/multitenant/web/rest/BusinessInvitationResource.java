package com.walshe.multitenant.web.rest;

import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.service.BusinessInvitationQueryService;
import com.walshe.multitenant.service.BusinessInvitationService;
import com.walshe.multitenant.service.criteria.BusinessInvitationCriteria;
import com.walshe.multitenant.service.dto.BusinessInvitationDTO;
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
 * REST controller for managing {@link com.walshe.multitenant.domain.BusinessInvitation}.
 */
@RestController
@RequestMapping("/api/business-invitations")
public class BusinessInvitationResource {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessInvitationResource.class);

    private static final String ENTITY_NAME = "businessInvitation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessInvitationService businessInvitationService;

    private final BusinessInvitationRepository businessInvitationRepository;

    private final BusinessInvitationQueryService businessInvitationQueryService;

    public BusinessInvitationResource(
        BusinessInvitationService businessInvitationService,
        BusinessInvitationRepository businessInvitationRepository,
        BusinessInvitationQueryService businessInvitationQueryService
    ) {
        this.businessInvitationService = businessInvitationService;
        this.businessInvitationRepository = businessInvitationRepository;
        this.businessInvitationQueryService = businessInvitationQueryService;
    }

    /**
     * {@code POST  /business-invitations} : Create a new businessInvitation.
     *
     * @param businessInvitationDTO the businessInvitationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessInvitationDTO, or with status {@code 400 (Bad Request)} if the businessInvitation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BusinessInvitationDTO> createBusinessInvitation(@Valid @RequestBody BusinessInvitationDTO businessInvitationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BusinessInvitation : {}", businessInvitationDTO);
        if (businessInvitationDTO.getId() != null) {
            throw new BadRequestAlertException("A new businessInvitation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        businessInvitationDTO = businessInvitationService.save(businessInvitationDTO);
        return ResponseEntity.created(new URI("/api/business-invitations/" + businessInvitationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, businessInvitationDTO.getId().toString()))
            .body(businessInvitationDTO);
    }

    /**
     * {@code PUT  /business-invitations/:id} : Updates an existing businessInvitation.
     *
     * @param id the id of the businessInvitationDTO to save.
     * @param businessInvitationDTO the businessInvitationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessInvitationDTO,
     * or with status {@code 400 (Bad Request)} if the businessInvitationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessInvitationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BusinessInvitationDTO> updateBusinessInvitation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BusinessInvitationDTO businessInvitationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BusinessInvitation : {}, {}", id, businessInvitationDTO);
        if (businessInvitationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessInvitationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessInvitationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        businessInvitationDTO = businessInvitationService.update(businessInvitationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessInvitationDTO.getId().toString()))
            .body(businessInvitationDTO);
    }

    /**
     * {@code PATCH  /business-invitations/:id} : Partial updates given fields of an existing businessInvitation, field will ignore if it is null
     *
     * @param id the id of the businessInvitationDTO to save.
     * @param businessInvitationDTO the businessInvitationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessInvitationDTO,
     * or with status {@code 400 (Bad Request)} if the businessInvitationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the businessInvitationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the businessInvitationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BusinessInvitationDTO> partialUpdateBusinessInvitation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BusinessInvitationDTO businessInvitationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BusinessInvitation partially : {}, {}", id, businessInvitationDTO);
        if (businessInvitationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessInvitationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessInvitationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BusinessInvitationDTO> result = businessInvitationService.partialUpdate(businessInvitationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessInvitationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /business-invitations} : get all the businessInvitations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessInvitations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BusinessInvitationDTO>> getAllBusinessInvitations(
        BusinessInvitationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BusinessInvitations by criteria: {}", criteria);

        Page<BusinessInvitationDTO> page = businessInvitationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /business-invitations/count} : count all the businessInvitations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBusinessInvitations(BusinessInvitationCriteria criteria) {
        LOG.debug("REST request to count BusinessInvitations by criteria: {}", criteria);
        return ResponseEntity.ok().body(businessInvitationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /business-invitations/:id} : get the "id" businessInvitation.
     *
     * @param id the id of the businessInvitationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessInvitationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BusinessInvitationDTO> getBusinessInvitation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BusinessInvitation : {}", id);
        Optional<BusinessInvitationDTO> businessInvitationDTO = businessInvitationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessInvitationDTO);
    }

    /**
     * {@code DELETE  /business-invitations/:id} : delete the "id" businessInvitation.
     *
     * @param id the id of the businessInvitationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusinessInvitation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BusinessInvitation : {}", id);
        businessInvitationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
