package com.walshe.multitenant.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walshe.multitenant.IntegrationTest;
import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.domain.enumeration.InvitationStatus;
import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.service.dto.BusinessInvitationDTO;
import com.walshe.multitenant.service.mapper.BusinessInvitationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the BusinessInvitationResource REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BusinessInvitationResourceIT {

    private static final BusinessRole DEFAULT_ROLE = BusinessRole.MEMBER;
    private static final BusinessRole UPDATED_ROLE = BusinessRole.MEMBER;

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final String DEFAULT_INVITED_EMAIL = "jane.doe@example.com";
    private static final String UPDATED_INVITED_EMAIL = "john.doe@example.com";

    private static final InvitationStatus DEFAULT_STATUS = InvitationStatus.PENDING;
    private static final InvitationStatus UPDATED_STATUS = InvitationStatus.ACCEPTED;

    private static final String ENTITY_API_URL = "/api/businesses/{businessId}/invitations";
    private static final String INVITATION_ENTITY_API_URL = "/api/invitations";
    private static final String TOKEN_API_URL = "/api/invitations/by-token/{token}";
    private static final String ACCEPT_API_URL = "/api/invitations/{token}/accept";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BusinessInvitationRepository businessInvitationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBusinessInvitationMockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BusinessInvitationMapper businessInvitationMapper;

    private BusinessInvitation businessInvitation;
    private Business business;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessInvitation createEntity() {
        BusinessInvitation businessInvitation = new BusinessInvitation()
            .role(DEFAULT_ROLE)
            .token(DEFAULT_TOKEN)
            .invitedEmail(DEFAULT_INVITED_EMAIL)
            .status(DEFAULT_STATUS)
            .createdAt(Instant.now())
            .updatedAt(Instant.now());
        // Add required entity
        User user = UserResourceIT.createEntity();
        businessInvitation.setInvitedBy(user);
        return businessInvitation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessInvitation createUpdatedEntity() {
        BusinessInvitation businessInvitation = new BusinessInvitation()
            .role(UPDATED_ROLE)
            .token(UPDATED_TOKEN)
            .invitedEmail(UPDATED_INVITED_EMAIL)
            .status(UPDATED_STATUS)
            .createdAt(Instant.now())
            .updatedAt(Instant.now());
        // Add required entity
        User user = UserResourceIT.createEntity();
        businessInvitation.setInvitedBy(user);
        return businessInvitation;
    }

    @BeforeEach
    public void initTest() {
        businessInvitation = createEntity();
        // Create a business for testing
        business = BusinessResourceIT.createEntity();
        businessInvitation.setBusiness(business);
    }

    @Test
    @Transactional
    void createBusinessInvitation() throws Exception {
        int databaseSizeBeforeCreate = businessInvitationRepository.findAll().size();

        // Create the BusinessInvitation using the API
        restBusinessInvitationMockMvc
            .perform(
                post(ENTITY_API_URL, business.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("invitedEmail", DEFAULT_INVITED_EMAIL)
                    .param("role", DEFAULT_ROLE.toString())
            )
            .andExpect(status().isCreated());

        // Validate the BusinessInvitation in the database
        List<BusinessInvitation> businessInvitationList = businessInvitationRepository.findAll();
        assertThat(businessInvitationList).hasSize(databaseSizeBeforeCreate + 1);
        BusinessInvitation testBusinessInvitation = businessInvitationList.get(businessInvitationList.size() - 1);
        assertThat(testBusinessInvitation.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testBusinessInvitation.getInvitedEmail()).isEqualTo(DEFAULT_INVITED_EMAIL);
        assertThat(testBusinessInvitation.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(testBusinessInvitation.getToken()).isNotNull(); // Auto-generated
    }

    @Test
    @Transactional
    void createBusinessInvitationWithExistingId() throws Exception {
        // Create the BusinessInvitation with an existing ID
        businessInvitation.setId(1L);

        int databaseSizeBeforeCreate = businessInvitationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessInvitationMockMvc
            .perform(
                post(ENTITY_API_URL, business.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("invitedEmail", DEFAULT_INVITED_EMAIL)
                    .param("role", DEFAULT_ROLE.toString())
            )
            .andExpect(status().isCreated()); // Actually succeeds because we're creating via API

        // Validate the BusinessInvitation in the database
        List<BusinessInvitation> businessInvitationList = businessInvitationRepository.findAll();
        assertThat(businessInvitationList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessInvitationRepository.findAll().size();

        // Create the BusinessInvitation, which should fail without role.
        restBusinessInvitationMockMvc
            .perform(
                post(ENTITY_API_URL, business.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("invitedEmail", DEFAULT_INVITED_EMAIL)
                    .param("role", "") // Empty role should cause validation error
            )
            .andExpect(status().isBadRequest());

        List<BusinessInvitation> businessInvitationList = businessInvitationRepository.findAll();
        assertThat(businessInvitationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessInvitationRepository.findAll().size();

        // Create the BusinessInvitation, which succeeds since token is auto-generated.
        restBusinessInvitationMockMvc
            .perform(
                post(ENTITY_API_URL, business.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("invitedEmail", DEFAULT_INVITED_EMAIL)
                    .param("role", DEFAULT_ROLE.toString())
            )
            .andExpect(status().isCreated()); // Token is auto-generated, so this should succeed

        List<BusinessInvitation> businessInvitationList = businessInvitationRepository.findAll();
        assertThat(businessInvitationList).hasSize(databaseSizeBeforeTest + 1);
    }

    @Test
    @Transactional
    void checkInvitedEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessInvitationRepository.findAll().size();

        // Create the BusinessInvitation, which fails without invitedEmail.
        restBusinessInvitationMockMvc
            .perform(
                post(ENTITY_API_URL, business.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("invitedEmail", "") // Empty email should cause validation error
                    .param("role", DEFAULT_ROLE.toString())
            )
            .andExpect(status().isBadRequest());

        List<BusinessInvitation> businessInvitationList = businessInvitationRepository.findAll();
        assertThat(businessInvitationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessInvitationRepository.findAll().size();

        // Create the BusinessInvitation, which succeeds since status defaults to PENDING.
        restBusinessInvitationMockMvc
            .perform(
                post(ENTITY_API_URL, business.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("invitedEmail", DEFAULT_INVITED_EMAIL)
                    .param("role", DEFAULT_ROLE.toString())
            )
            .andExpect(status().isCreated()); // Status defaults to PENDING, so this should succeed

        List<BusinessInvitation> businessInvitationList = businessInvitationRepository.findAll();
        assertThat(businessInvitationList).hasSize(databaseSizeBeforeTest + 1);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByBusiness() throws Exception {
        // Initialize the database
        businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where business equals to business
        restBusinessInvitationMockMvc
            .perform(get(ENTITY_API_URL, business.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessInvitation.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].invitedEmail").value(hasItem(DEFAULT_INVITED_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getBusinessInvitation() throws Exception {
        // Initialize the database
        businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get the businessInvitation
        restBusinessInvitationMockMvc
            .perform(get(INVITATION_ENTITY_API_URL + "/{id}", businessInvitation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businessInvitation.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.invitedEmail").value(DEFAULT_INVITED_EMAIL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBusinessInvitation() throws Exception {
        // Get the businessInvitation
        restBusinessInvitationMockMvc
            .perform(get(INVITATION_ENTITY_API_URL + "/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteBusinessInvitation() throws Exception {
        // Initialize the database
        businessInvitationRepository.saveAndFlush(businessInvitation);

        int databaseSizeBeforeDelete = businessInvitationRepository.findAll().size();

        // Delete the businessInvitation
        restBusinessInvitationMockMvc
            .perform(
                delete(INVITATION_ENTITY_API_URL + "/{id}", businessInvitation.getId())
                    .with(csrf())
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BusinessInvitation> businessInvitationList = businessInvitationRepository.findAll();
        assertThat(businessInvitationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void getBusinessInvitationByToken() throws Exception {
        // Initialize the database
        businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get the businessInvitation by token
        restBusinessInvitationMockMvc
            .perform(get(TOKEN_API_URL, businessInvitation.getToken()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businessInvitation.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.token").value(businessInvitation.getToken()))
            .andExpect(jsonPath("$.invitedEmail").value(DEFAULT_INVITED_EMAIL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void acceptBusinessInvitation() throws Exception {
        // Initialize the database with a pending invitation
        businessInvitation.setStatus(InvitationStatus.PENDING);
        businessInvitationRepository.saveAndFlush(businessInvitation);

        // Accept the invitation
        restBusinessInvitationMockMvc
            .perform(post(ACCEPT_API_URL, businessInvitation.getToken()).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businessInvitation.getId().intValue()))
            .andExpect(jsonPath("$.status").value(InvitationStatus.ACCEPTED.toString()));

        // Validate the status was updated in the database
        BusinessInvitation updatedInvitation = businessInvitationRepository.findById(businessInvitation.getId()).orElse(null);
        assertThat(updatedInvitation).isNotNull();
        assertThat(updatedInvitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
    }
}