package com.walshe.multitenant.web.rest;

import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.service.BusinessInvitationService;
import com.walshe.multitenant.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import com.walshe.multitenant.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link BusinessInvitation}.
 */
@RestController
@RequestMapping("/api")
public class BusinessInvitationResource {

    private final Logger log = LoggerFactory.getLogger(BusinessInvitationResource.class);

    private static final String ENTITY_NAME = "businessInvitation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessInvitationService businessInvitationService;

    private final BusinessInvitationRepository businessInvitationRepository;

    public BusinessInvitationResource(
        BusinessInvitationService businessInvitationService,
        BusinessInvitationRepository businessInvitationRepository
    ) {
        this.businessInvitationService = businessInvitationService;
        this.businessInvitationRepository = businessInvitationRepository;
    }

    /**
     * {@code POST  /businesses/:businessId/invitations} : Create a new businessInvitation.
     *
     * @param businessId the ID of the business to invite to
     * @param invitedEmail the email of the person being invited
     * @param role the role to assign to the invited person
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and the new businessInvitation in body
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/businesses/{businessId}/invitations")
    @PreAuthorize("@businessSecurity.isBusinessOwner(#businessId)")
    public ResponseEntity<BusinessInvitation> createBusinessInvitation(
        @PathVariable(value = "businessId") Long businessId,
        @RequestParam String invitedEmail,
        @RequestParam BusinessRole role
    ) throws URISyntaxException {
        log.debug("REST request to create BusinessInvitation : {}", invitedEmail);
        
        BusinessInvitation result = businessInvitationService.createInvitation(invitedEmail, role, businessId);
        
        return ResponseEntity
            .created(new URI("/api/invitations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /businesses/:businessId/invitations} : get all the businessInvitations for a specific business.
     *
     * @param businessId the ID of the business
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessInvitations in body
     */
    @GetMapping("/businesses/{businessId}/invitations")
    @PreAuthorize("@businessSecurity.isBusinessOwner(#businessId)")
    public ResponseEntity<List<BusinessInvitation>> getAllBusinessInvitationsByBusiness(
        @PathVariable(value = "businessId") Long businessId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all BusinessInvitations for business : {}", businessId);
        
        Page<BusinessInvitation> page = businessInvitationService.findAllByBusiness(businessId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    /**
     * {@code GET  /business-invitations} : get all the businessInvitations.
     *
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessInvitations in body
     */

    /**
     * {@code GET  /business-invitations/:id} : get the "id" businessInvitation.
     *
     * @param id the id of the businessInvitation to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessInvitation, or with status {@code 404 (Not Found)}
     */
    @GetMapping("/business-invitations/{id}")
    @PreAuthorize("@businessInvitationSecurity.canViewInvitation(#id)")
    public ResponseEntity<BusinessInvitation> getBusinessInvitationFlat(@PathVariable Long id) {
        log.debug("REST request to get BusinessInvitation : {}", id);
        Optional<BusinessInvitation> businessInvitation = businessInvitationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessInvitation);
    }


    /**
     * {@code DELETE  /business-invitations/:id} : delete the "id" businessInvitation.
     *
     * @param id the id of the businessInvitation to delete
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/business-invitations/{id}")
    @PreAuthorize("@businessInvitationSecurity.canModifyInvitation(#id)")
    public ResponseEntity<Void> deleteBusinessInvitationFlat(@PathVariable Long id) {
        log.debug("REST request to delete BusinessInvitation : {}", id);
        businessInvitationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code PUT  /business-invitations/:id} : Updates an existing businessInvitation.
     *
     * @param id the id of the businessInvitation to save
     * @param businessInvitation the businessInvitation to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessInvitation,
     * or with status {@code 400 (Bad Request)} if the businessInvitation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessInvitation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/business-invitations/{id}")
    @PreAuthorize("@businessInvitationSecurity.canModifyInvitation(#id)")
    public ResponseEntity<BusinessInvitation> updateBusinessInvitationFlat(
        @PathVariable(value = "id") Long id,
        @RequestBody BusinessInvitation businessInvitation
    ) throws URISyntaxException {
        log.debug("REST request to update BusinessInvitation : {}", id);
        if (businessInvitation.getId() == null || !Objects.equals(id, businessInvitation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        businessInvitation = businessInvitationService.update(businessInvitation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessInvitation.getId().toString()))
            .body(businessInvitation);
    }

    /**
     * {@code PATCH  /business-invitations/:id} : Partial updates given fields of an existing businessInvitation, field will ignore if it is null
     *
     * @param id the id of the businessInvitation to save
     * @param businessInvitation the businessInvitation to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessInvitation,
     * or with status {@code 400 (Bad Request)} if the businessInvitation is not valid,
     * or with status {@code 404 (Not Found)} if the businessInvitation is not found,
     * or with status {@code 500 (Internal Server Error)} if the businessInvitation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PatchMapping(value = "/business-invitations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessInvitationSecurity.canModifyInvitation(#id)")
    public ResponseEntity<BusinessInvitation> partialUpdateBusinessInvitationFlat(
        @PathVariable(value = "id") Long id,
        @RequestBody BusinessInvitation businessInvitation
    ) throws URISyntaxException {
        log.debug("REST request to partial update BusinessInvitation partially : {}", id);
        if (businessInvitation.getId() == null || !Objects.equals(id, businessInvitation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        Optional<BusinessInvitation> result = businessInvitationService.partialUpdate(businessInvitation);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessInvitation.getId().toString())
        );
    }

    /**
     * {@code GET  /business-invitations/by-token/{token}} : get the businessInvitation by token (public endpoint).
     *
     * @param token the token of the businessInvitation to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessInvitation, or with status {@code 404 (Not Found)}
     */
    /**
     * {@code GET  /business-invitations} : get all the businessInvitations where the current user is the business owner.
     *
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessInvitations in body
     */
    @GetMapping("/business-invitations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BusinessInvitation>> getAllBusinessInvitations(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get all BusinessInvitations for current user as business owner");
        
        Page<BusinessInvitation> page = businessInvitationService.findAllForCurrentUserAsBusinessOwner(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/business-invitations/by-token/{token}")
    public ResponseEntity<BusinessInvitation> getBusinessInvitationByToken(@PathVariable String token) {
        log.debug("REST request to get BusinessInvitation by token: {}", token);
        Optional<BusinessInvitation> businessInvitation = businessInvitationService.findByTokenWithEagerRelationships(token);
        return ResponseUtil.wrapOrNotFound(businessInvitation);
    }

    /**
     * {@code POST  /business-invitations/accept} : accept the invitation with the given token in request body.
     *
     * @param requestBody the request body containing the token
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accepted businessInvitation
     */
    @PostMapping("/business-invitations/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BusinessInvitation> acceptBusinessInvitationFromBody(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        log.debug("REST request to accept BusinessInvitation with token from request body: {}", token);
        BusinessInvitation result = businessInvitationService.acceptInvitation(token);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}