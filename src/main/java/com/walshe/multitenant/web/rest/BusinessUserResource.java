package com.walshe.multitenant.web.rest;

import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.service.BusinessAuthorizationService;
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

    private final BusinessAuthorizationService businessAuthorizationService;

    public BusinessUserResource(
        BusinessUserService businessUserService,
        BusinessUserRepository businessUserRepository,
        BusinessUserQueryService businessUserQueryService,
        BusinessAuthorizationService businessAuthorizationService
    ) {
        this.businessUserService = businessUserService;
        this.businessUserRepository = businessUserRepository;
        this.businessUserQueryService = businessUserQueryService;
        this.businessAuthorizationService = businessAuthorizationService;
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

}
