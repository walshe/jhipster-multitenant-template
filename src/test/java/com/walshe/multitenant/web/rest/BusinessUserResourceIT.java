package com.walshe.multitenant.web.rest;

import static com.walshe.multitenant.domain.BusinessUserAsserts.*;
import static com.walshe.multitenant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walshe.multitenant.IntegrationTest;
import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.service.BusinessUserService;
import com.walshe.multitenant.service.dto.BusinessUserDTO;
import com.walshe.multitenant.service.mapper.BusinessUserMapper;
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
 * Integration tests for the {@link BusinessUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BusinessUserResourceIT {

    private static final BusinessRole DEFAULT_ROLE = BusinessRole.MEMBER;
    private static final BusinessRole UPDATED_ROLE = BusinessRole.MEMBER;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/business-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private BusinessUserRepository businessUserRepositoryMock;

    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Mock
    private BusinessUserService businessUserServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBusinessUserMockMvc;

    private BusinessUser businessUser;

    private BusinessUser insertedBusinessUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessUser createEntity() {
        return new BusinessUser().role(DEFAULT_ROLE).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessUser createUpdatedEntity() {
        return new BusinessUser().role(UPDATED_ROLE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        businessUser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBusinessUser != null) {
            businessUserRepository.delete(insertedBusinessUser);
            insertedBusinessUser = null;
        }
    }

    @Test
    @Transactional
    void createBusinessUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BusinessUser
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);
        var returnedBusinessUserDTO = om.readValue(
            restBusinessUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BusinessUserDTO.class
        );

        // Validate the BusinessUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBusinessUser = businessUserMapper.toEntity(returnedBusinessUserDTO);
        assertBusinessUserUpdatableFieldsEquals(returnedBusinessUser, getPersistedBusinessUser(returnedBusinessUser));

        insertedBusinessUser = returnedBusinessUser;
    }

    @Test
    @Transactional
    void createBusinessUserWithExistingId() throws Exception {
        // Create the BusinessUser with an existing ID
        businessUser.setId(1L);
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessUser.setRole(null);

        // Create the BusinessUser, which fails.
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        restBusinessUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBusinessUsers() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList
        restBusinessUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBusinessUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(businessUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBusinessUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(businessUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBusinessUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(businessUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBusinessUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(businessUserRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBusinessUser() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get the businessUser
        restBusinessUserMockMvc
            .perform(get(ENTITY_API_URL_ID, businessUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businessUser.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getBusinessUsersByIdFiltering() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        Long id = businessUser.getId();

        defaultBusinessUserFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBusinessUserFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBusinessUserFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBusinessUsersByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where role equals to
        defaultBusinessUserFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllBusinessUsersByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where role in
        defaultBusinessUserFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllBusinessUsersByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where role is not null
        defaultBusinessUserFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessUsersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where createdAt equals to
        defaultBusinessUserFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessUsersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where createdAt in
        defaultBusinessUserFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessUsersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where createdAt is not null
        defaultBusinessUserFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessUsersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where updatedAt equals to
        defaultBusinessUserFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessUsersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where updatedAt in
        defaultBusinessUserFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBusinessUsersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        // Get all the businessUserList where updatedAt is not null
        defaultBusinessUserFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBusinessUsersByBusinessIsEqualToSomething() throws Exception {
        Business business;
        if (TestUtil.findAll(em, Business.class).isEmpty()) {
            businessUserRepository.saveAndFlush(businessUser);
            business = BusinessResourceIT.createEntity();
        } else {
            business = TestUtil.findAll(em, Business.class).get(0);
        }
        em.persist(business);
        em.flush();
        businessUser.setBusiness(business);
        businessUserRepository.saveAndFlush(businessUser);
        Long businessId = business.getId();
        // Get all the businessUserList where business equals to businessId
        defaultBusinessUserShouldBeFound("businessId.equals=" + businessId);

        // Get all the businessUserList where business equals to (businessId + 1)
        defaultBusinessUserShouldNotBeFound("businessId.equals=" + (businessId + 1));
    }

    @Test
    @Transactional
    void getAllBusinessUsersByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            businessUserRepository.saveAndFlush(businessUser);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        businessUser.setUser(user);
        businessUserRepository.saveAndFlush(businessUser);
        Long userId = user.getId();
        // Get all the businessUserList where user equals to userId
        defaultBusinessUserShouldBeFound("userId.equals=" + userId);

        // Get all the businessUserList where user equals to (userId + 1)
        defaultBusinessUserShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultBusinessUserFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBusinessUserShouldBeFound(shouldBeFound);
        defaultBusinessUserShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBusinessUserShouldBeFound(String filter) throws Exception {
        restBusinessUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restBusinessUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBusinessUserShouldNotBeFound(String filter) throws Exception {
        restBusinessUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBusinessUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBusinessUser() throws Exception {
        // Get the businessUser
        restBusinessUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBusinessUser() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUser
        BusinessUser updatedBusinessUser = businessUserRepository.findById(businessUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBusinessUser are not directly saved in db
        em.detach(updatedBusinessUser);
        updatedBusinessUser.role(UPDATED_ROLE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(updatedBusinessUser);

        restBusinessUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBusinessUserToMatchAllProperties(updatedBusinessUser);
    }

    @Test
    @Transactional
    void putNonExistingBusinessUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUser.setId(longCount.incrementAndGet());

        // Create the BusinessUser
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBusinessUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUser.setId(longCount.incrementAndGet());

        // Create the BusinessUser
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBusinessUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUser.setId(longCount.incrementAndGet());

        // Create the BusinessUser
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBusinessUserWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUser using partial update
        BusinessUser partialUpdatedBusinessUser = new BusinessUser();
        partialUpdatedBusinessUser.setId(businessUser.getId());

        partialUpdatedBusinessUser.updatedAt(UPDATED_UPDATED_AT);

        restBusinessUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusinessUser))
            )
            .andExpect(status().isOk());

        // Validate the BusinessUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBusinessUser, businessUser),
            getPersistedBusinessUser(businessUser)
        );
    }

    @Test
    @Transactional
    void fullUpdateBusinessUserWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUser using partial update
        BusinessUser partialUpdatedBusinessUser = new BusinessUser();
        partialUpdatedBusinessUser.setId(businessUser.getId());

        partialUpdatedBusinessUser.role(UPDATED_ROLE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restBusinessUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusinessUser))
            )
            .andExpect(status().isOk());

        // Validate the BusinessUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUserUpdatableFieldsEquals(partialUpdatedBusinessUser, getPersistedBusinessUser(partialUpdatedBusinessUser));
    }

    @Test
    @Transactional
    void patchNonExistingBusinessUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUser.setId(longCount.incrementAndGet());

        // Create the BusinessUser
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, businessUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBusinessUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUser.setId(longCount.incrementAndGet());

        // Create the BusinessUser
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBusinessUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUser.setId(longCount.incrementAndGet());

        // Create the BusinessUser
        BusinessUserDTO businessUserDTO = businessUserMapper.toDto(businessUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(businessUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBusinessUser() throws Exception {
        // Initialize the database
        insertedBusinessUser = businessUserRepository.saveAndFlush(businessUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the businessUser
        restBusinessUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, businessUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return businessUserRepository.count();
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

    protected BusinessUser getPersistedBusinessUser(BusinessUser businessUser) {
        return businessUserRepository.findById(businessUser.getId()).orElseThrow();
    }

    protected void assertPersistedBusinessUserToMatchAllProperties(BusinessUser expectedBusinessUser) {
        assertBusinessUserAllPropertiesEquals(expectedBusinessUser, getPersistedBusinessUser(expectedBusinessUser));
    }

    protected void assertPersistedBusinessUserToMatchUpdatableProperties(BusinessUser expectedBusinessUser) {
        assertBusinessUserAllUpdatablePropertiesEquals(expectedBusinessUser, getPersistedBusinessUser(expectedBusinessUser));
    }
}
