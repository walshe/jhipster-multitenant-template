package com.walshe.multitenant.web.rest;

import static com.walshe.multitenant.domain.BusinessAsserts.*;
import static com.walshe.multitenant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walshe.multitenant.IntegrationTest;
import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.service.BusinessService;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.mapper.BusinessMapper;
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
 * Integration tests for the {@link BusinessResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BusinessResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/businesses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private BusinessRepository businessRepositoryMock;

    @Autowired
    private BusinessMapper businessMapper;

    @Mock
    private BusinessService businessServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBusinessMockMvc;

    private Business business;

    private Business insertedBusiness;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Business createEntity() {
        return new Business().name(DEFAULT_NAME).slug(DEFAULT_SLUG).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Business createUpdatedEntity() {
        return new Business().name(UPDATED_NAME).slug(UPDATED_SLUG).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        business = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBusiness != null) {
            businessRepository.delete(insertedBusiness);
            insertedBusiness = null;
        }
    }

    @Test
    @Transactional
    void createBusiness() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);
        var returnedBusinessDTO = om.readValue(
            restBusinessMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BusinessDTO.class
        );

        // Validate the Business in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBusiness = businessMapper.toEntity(returnedBusinessDTO);
        assertBusinessUpdatableFieldsEquals(returnedBusiness, getPersistedBusiness(returnedBusiness));

        insertedBusiness = returnedBusiness;
    }

    @Test
    @Transactional
    void createBusinessWithExistingId() throws Exception {
        // Create the Business with an existing ID
        business.setId(1L);
        BusinessDTO businessDTO = businessMapper.toDto(business);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        business.setName(null);

        // Create the Business, which fails.
        BusinessDTO businessDTO = businessMapper.toDto(business);

        restBusinessMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        business.setSlug(null);

        // Create the Business, which fails.
        BusinessDTO businessDTO = businessMapper.toDto(business);

        restBusinessMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBusinesses() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList
        restBusinessMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBusinessesWithEagerRelationshipsIsEnabled() throws Exception {
        when(businessServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBusinessMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(businessServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBusinessesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(businessServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBusinessMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(businessRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBusiness() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get the business
        restBusinessMockMvc
            .perform(get(ENTITY_API_URL_ID, business.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(business.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getBusinessesByIdFiltering() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        Long id = business.getId();

        defaultBusinessFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBusinessFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBusinessFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBusinessesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where name equals to
        defaultBusinessFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBusinessesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where name in
        defaultBusinessFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBusinessesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where name is not null
        defaultBusinessFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where name contains
        defaultBusinessFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBusinessesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where name does not contain
        defaultBusinessFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllBusinessesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where slug equals to
        defaultBusinessFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBusinessesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where slug in
        defaultBusinessFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBusinessesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where slug is not null
        defaultBusinessFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where slug contains
        defaultBusinessFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBusinessesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where slug does not contain
        defaultBusinessFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllBusinessesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where createdAt equals to
        defaultBusinessFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where createdAt in
        defaultBusinessFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where createdAt is not null
        defaultBusinessFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where updatedAt equals to
        defaultBusinessFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where updatedAt in
        defaultBusinessFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        // Get all the businessList where updatedAt is not null
        defaultBusinessFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessesByOwnerIsEqualToSomething() throws Exception {
        User owner;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            businessRepository.saveAndFlush(business);
            owner = UserResourceIT.createEntity();
        } else {
            owner = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(owner);
        em.flush();
        business.setOwner(owner);
        businessRepository.saveAndFlush(business);
        Long ownerId = owner.getId();
        // Get all the businessList where owner equals to ownerId
        defaultBusinessShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the businessList where owner equals to (ownerId + 1)
        defaultBusinessShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }

    private void defaultBusinessFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBusinessShouldBeFound(shouldBeFound);
        defaultBusinessShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBusinessShouldBeFound(String filter) throws Exception {
        restBusinessMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restBusinessMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBusinessShouldNotBeFound(String filter) throws Exception {
        restBusinessMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBusinessMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBusiness() throws Exception {
        // Get the business
        restBusinessMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBusiness() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the business
        Business updatedBusiness = businessRepository.findById(business.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBusiness are not directly saved in db
        em.detach(updatedBusiness);
        updatedBusiness.name(UPDATED_NAME).slug(UPDATED_SLUG).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        BusinessDTO businessDTO = businessMapper.toDto(updatedBusiness);

        restBusinessMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessDTO))
            )
            .andExpect(status().isOk());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBusinessToMatchAllProperties(updatedBusiness);
    }

    @Test
    @Transactional
    void putNonExistingBusiness() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        business.setId(longCount.incrementAndGet());

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBusiness() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        business.setId(longCount.incrementAndGet());

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBusiness() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        business.setId(longCount.incrementAndGet());

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBusinessWithPatch() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the business using partial update
        Business partialUpdatedBusiness = new Business();
        partialUpdatedBusiness.setId(business.getId());

        partialUpdatedBusiness.name(UPDATED_NAME);

        restBusinessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusiness.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusiness))
            )
            .andExpect(status().isOk());

        // Validate the Business in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBusiness, business), getPersistedBusiness(business));
    }

    @Test
    @Transactional
    void fullUpdateBusinessWithPatch() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the business using partial update
        Business partialUpdatedBusiness = new Business();
        partialUpdatedBusiness.setId(business.getId());

        partialUpdatedBusiness.name(UPDATED_NAME).slug(UPDATED_SLUG).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restBusinessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusiness.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusiness))
            )
            .andExpect(status().isOk());

        // Validate the Business in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUpdatableFieldsEquals(partialUpdatedBusiness, getPersistedBusiness(partialUpdatedBusiness));
    }

    @Test
    @Transactional
    void patchNonExistingBusiness() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        business.setId(longCount.incrementAndGet());

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, businessDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBusiness() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        business.setId(longCount.incrementAndGet());

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBusiness() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        business.setId(longCount.incrementAndGet());

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(businessDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Business in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBusiness() throws Exception {
        // Initialize the database
        insertedBusiness = businessRepository.saveAndFlush(business);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the business
        restBusinessMockMvc
            .perform(delete(ENTITY_API_URL_ID, business.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return businessRepository.count();
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

    protected Business getPersistedBusiness(Business business) {
        return businessRepository.findById(business.getId()).orElseThrow();
    }

    protected void assertPersistedBusinessToMatchAllProperties(Business expectedBusiness) {
        assertBusinessAllPropertiesEquals(expectedBusiness, getPersistedBusiness(expectedBusiness));
    }

    protected void assertPersistedBusinessToMatchUpdatableProperties(Business expectedBusiness) {
        assertBusinessAllUpdatablePropertiesEquals(expectedBusiness, getPersistedBusiness(expectedBusiness));
    }
}
