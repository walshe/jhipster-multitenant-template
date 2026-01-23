package com.walshe.multitenant.web.rest;

import static com.walshe.multitenant.domain.BusinessInvitationAsserts.*;
import static com.walshe.multitenant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walshe.multitenant.IntegrationTest;
import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.repository.BusinessInvitationRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.service.BusinessInvitationService;
import com.walshe.multitenant.service.dto.BusinessInvitationDTO;
import com.walshe.multitenant.service.mapper.BusinessInvitationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BusinessInvitationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BusinessInvitationResourceIT {

    private static final BusinessRole DEFAULT_ROLE = BusinessRole.OWNER;
    private static final BusinessRole UPDATED_ROLE = BusinessRole.ADMIN;

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final String DEFAULT_INVITED_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_INVITED_EMAIL = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/business-invitations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BusinessInvitationRepository businessInvitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private BusinessInvitationRepository businessInvitationRepositoryMock;

    @Autowired
    private BusinessInvitationMapper businessInvitationMapper;

    @Mock
    private BusinessInvitationService businessInvitationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBusinessInvitationMockMvc;

    private BusinessInvitation businessInvitation;

    private BusinessInvitation insertedBusinessInvitation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessInvitation createEntity() {
        return new BusinessInvitation()
            .role(DEFAULT_ROLE)
            .token(DEFAULT_TOKEN)
            .invitedEmail(DEFAULT_INVITED_EMAIL)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessInvitation createUpdatedEntity() {
        return new BusinessInvitation()
            .role(UPDATED_ROLE)
            .token(UPDATED_TOKEN)
            .invitedEmail(UPDATED_INVITED_EMAIL)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        businessInvitation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBusinessInvitation != null) {
            businessInvitationRepository.delete(insertedBusinessInvitation);
            insertedBusinessInvitation = null;
        }
    }

    @Test
    @Transactional
    void createBusinessInvitation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BusinessInvitation
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);
        var returnedBusinessInvitationDTO = om.readValue(
            restBusinessInvitationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessInvitationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BusinessInvitationDTO.class
        );

        // Validate the BusinessInvitation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBusinessInvitation = businessInvitationMapper.toEntity(returnedBusinessInvitationDTO);
        assertBusinessInvitationUpdatableFieldsEquals(
            returnedBusinessInvitation,
            getPersistedBusinessInvitation(returnedBusinessInvitation)
        );

        insertedBusinessInvitation = returnedBusinessInvitation;
    }

    @Test
    @Transactional
    void createBusinessInvitationWithExistingId() throws Exception {
        // Create the BusinessInvitation with an existing ID
        businessInvitation.setId(1L);
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessInvitationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessInvitationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessInvitation.setRole(null);

        // Create the BusinessInvitation, which fails.
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        restBusinessInvitationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessInvitationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessInvitation.setToken(null);

        // Create the BusinessInvitation, which fails.
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        restBusinessInvitationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessInvitationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInvitedEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessInvitation.setInvitedEmail(null);

        // Create the BusinessInvitation, which fails.
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        restBusinessInvitationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessInvitationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBusinessInvitations() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList
        restBusinessInvitationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessInvitation.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].invitedEmail").value(hasItem(DEFAULT_INVITED_EMAIL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBusinessInvitationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(businessInvitationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBusinessInvitationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(businessInvitationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBusinessInvitationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(businessInvitationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBusinessInvitationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(businessInvitationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBusinessInvitation() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get the businessInvitation
        restBusinessInvitationMockMvc
            .perform(get(ENTITY_API_URL_ID, businessInvitation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businessInvitation.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.invitedEmail").value(DEFAULT_INVITED_EMAIL))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getBusinessInvitationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        Long id = businessInvitation.getId();

        defaultBusinessInvitationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBusinessInvitationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBusinessInvitationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where role equals to
        defaultBusinessInvitationFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where role in
        defaultBusinessInvitationFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where role is not null
        defaultBusinessInvitationFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where token equals to
        defaultBusinessInvitationFiltering("token.equals=" + DEFAULT_TOKEN, "token.equals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByTokenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where token in
        defaultBusinessInvitationFiltering("token.in=" + DEFAULT_TOKEN + "," + UPDATED_TOKEN, "token.in=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where token is not null
        defaultBusinessInvitationFiltering("token.specified=true", "token.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByTokenContainsSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where token contains
        defaultBusinessInvitationFiltering("token.contains=" + DEFAULT_TOKEN, "token.contains=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByTokenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where token does not contain
        defaultBusinessInvitationFiltering("token.doesNotContain=" + UPDATED_TOKEN, "token.doesNotContain=" + DEFAULT_TOKEN);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByInvitedEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where invitedEmail equals to
        defaultBusinessInvitationFiltering("invitedEmail.equals=" + DEFAULT_INVITED_EMAIL, "invitedEmail.equals=" + UPDATED_INVITED_EMAIL);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByInvitedEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where invitedEmail in
        defaultBusinessInvitationFiltering(
            "invitedEmail.in=" + DEFAULT_INVITED_EMAIL + "," + UPDATED_INVITED_EMAIL,
            "invitedEmail.in=" + UPDATED_INVITED_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByInvitedEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where invitedEmail is not null
        defaultBusinessInvitationFiltering("invitedEmail.specified=true", "invitedEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByInvitedEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where invitedEmail contains
        defaultBusinessInvitationFiltering(
            "invitedEmail.contains=" + DEFAULT_INVITED_EMAIL,
            "invitedEmail.contains=" + UPDATED_INVITED_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByInvitedEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where invitedEmail does not contain
        defaultBusinessInvitationFiltering(
            "invitedEmail.doesNotContain=" + UPDATED_INVITED_EMAIL,
            "invitedEmail.doesNotContain=" + DEFAULT_INVITED_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where createdAt equals to
        defaultBusinessInvitationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where createdAt in
        defaultBusinessInvitationFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where createdAt is not null
        defaultBusinessInvitationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where updatedAt equals to
        defaultBusinessInvitationFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where updatedAt in
        defaultBusinessInvitationFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        // Get all the businessInvitationList where updatedAt is not null
        defaultBusinessInvitationFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByBusinessIsEqualToSomething() throws Exception {
        Business business;
        if (TestUtil.findAll(em, Business.class).isEmpty()) {
            businessInvitationRepository.saveAndFlush(businessInvitation);
            business = BusinessResourceIT.createEntity();
        } else {
            business = TestUtil.findAll(em, Business.class).get(0);
        }
        em.persist(business);
        em.flush();
        businessInvitation.setBusiness(business);
        businessInvitationRepository.saveAndFlush(businessInvitation);
        Long businessId = business.getId();
        // Get all the businessInvitationList where business equals to businessId
        defaultBusinessInvitationShouldBeFound("businessId.equals=" + businessId);

        // Get all the businessInvitationList where business equals to (businessId + 1)
        defaultBusinessInvitationShouldNotBeFound("businessId.equals=" + (businessId + 1));
    }

    @Test
    @Transactional
    void getAllBusinessInvitationsByInvitedByIsEqualToSomething() throws Exception {
        User invitedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            businessInvitationRepository.saveAndFlush(businessInvitation);
            invitedBy = UserResourceIT.createEntity();
        } else {
            invitedBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(invitedBy);
        em.flush();
        businessInvitation.setInvitedBy(invitedBy);
        businessInvitationRepository.saveAndFlush(businessInvitation);
        Long invitedById = invitedBy.getId();
        // Get all the businessInvitationList where invitedBy equals to invitedById
        defaultBusinessInvitationShouldBeFound("invitedById.equals=" + invitedById);

        // Get all the businessInvitationList where invitedBy equals to (invitedById + 1)
        defaultBusinessInvitationShouldNotBeFound("invitedById.equals=" + (invitedById + 1));
    }

    private void defaultBusinessInvitationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBusinessInvitationShouldBeFound(shouldBeFound);
        defaultBusinessInvitationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBusinessInvitationShouldBeFound(String filter) throws Exception {
        restBusinessInvitationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessInvitation.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].invitedEmail").value(hasItem(DEFAULT_INVITED_EMAIL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restBusinessInvitationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBusinessInvitationShouldNotBeFound(String filter) throws Exception {
        restBusinessInvitationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBusinessInvitationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBusinessInvitation() throws Exception {
        // Get the businessInvitation
        restBusinessInvitationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBusinessInvitation() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessInvitation
        BusinessInvitation updatedBusinessInvitation = businessInvitationRepository.findById(businessInvitation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBusinessInvitation are not directly saved in db
        em.detach(updatedBusinessInvitation);
        updatedBusinessInvitation
            .role(UPDATED_ROLE)
            .token(UPDATED_TOKEN)
            .invitedEmail(UPDATED_INVITED_EMAIL)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(updatedBusinessInvitation);

        restBusinessInvitationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessInvitationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessInvitationDTO))
            )
            .andExpect(status().isOk());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBusinessInvitationToMatchAllProperties(updatedBusinessInvitation);
    }

    @Test
    @Transactional
    void putNonExistingBusinessInvitation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessInvitation.setId(longCount.incrementAndGet());

        // Create the BusinessInvitation
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessInvitationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessInvitationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessInvitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBusinessInvitation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessInvitation.setId(longCount.incrementAndGet());

        // Create the BusinessInvitation
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessInvitationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessInvitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBusinessInvitation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessInvitation.setId(longCount.incrementAndGet());

        // Create the BusinessInvitation
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessInvitationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessInvitationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBusinessInvitationWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessInvitation using partial update
        BusinessInvitation partialUpdatedBusinessInvitation = new BusinessInvitation();
        partialUpdatedBusinessInvitation.setId(businessInvitation.getId());

        partialUpdatedBusinessInvitation.invitedEmail(UPDATED_INVITED_EMAIL).createdAt(UPDATED_CREATED_AT);

        restBusinessInvitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessInvitation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusinessInvitation))
            )
            .andExpect(status().isOk());

        // Validate the BusinessInvitation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessInvitationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBusinessInvitation, businessInvitation),
            getPersistedBusinessInvitation(businessInvitation)
        );
    }

    @Test
    @Transactional
    void fullUpdateBusinessInvitationWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessInvitation using partial update
        BusinessInvitation partialUpdatedBusinessInvitation = new BusinessInvitation();
        partialUpdatedBusinessInvitation.setId(businessInvitation.getId());

        partialUpdatedBusinessInvitation
            .role(UPDATED_ROLE)
            .token(UPDATED_TOKEN)
            .invitedEmail(UPDATED_INVITED_EMAIL)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restBusinessInvitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessInvitation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusinessInvitation))
            )
            .andExpect(status().isOk());

        // Validate the BusinessInvitation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessInvitationUpdatableFieldsEquals(
            partialUpdatedBusinessInvitation,
            getPersistedBusinessInvitation(partialUpdatedBusinessInvitation)
        );
    }

    @Test
    @Transactional
    void patchNonExistingBusinessInvitation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessInvitation.setId(longCount.incrementAndGet());

        // Create the BusinessInvitation
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessInvitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, businessInvitationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessInvitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBusinessInvitation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessInvitation.setId(longCount.incrementAndGet());

        // Create the BusinessInvitation
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessInvitationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessInvitationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBusinessInvitation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessInvitation.setId(longCount.incrementAndGet());

        // Create the BusinessInvitation
        BusinessInvitationDTO businessInvitationDTO = businessInvitationMapper.toDto(businessInvitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessInvitationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(businessInvitationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessInvitation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBusinessInvitation() throws Exception {
        // Initialize the database
        insertedBusinessInvitation = businessInvitationRepository.saveAndFlush(businessInvitation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the businessInvitation
        restBusinessInvitationMockMvc
            .perform(delete(ENTITY_API_URL_ID, businessInvitation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return businessInvitationRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BusinessInvitation getPersistedBusinessInvitation(BusinessInvitation businessInvitation) {
        return businessInvitationRepository.findById(businessInvitation.getId()).orElseThrow();
    }

    protected void assertPersistedBusinessInvitationToMatchAllProperties(BusinessInvitation expectedBusinessInvitation) {
        assertBusinessInvitationAllPropertiesEquals(expectedBusinessInvitation, getPersistedBusinessInvitation(expectedBusinessInvitation));
    }

    protected void assertPersistedBusinessInvitationToMatchUpdatableProperties(BusinessInvitation expectedBusinessInvitation) {
        assertBusinessInvitationAllUpdatablePropertiesEquals(
            expectedBusinessInvitation,
            getPersistedBusinessInvitation(expectedBusinessInvitation)
        );
    }
}
