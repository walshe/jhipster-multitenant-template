package com.walshe.multitenant.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.walshe.multitenant.IntegrationTest;
import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.repository.UserRepository;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.mapper.BusinessMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the BusinessResource REST controller with security.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BusinessResourceSecurityIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS);

    private static final String ENTITY_NAME = "business";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBusinessMockMvc;

    @Autowired
    private ObjectMapper om;

    private Business business;
    private User testUser;
    private User testOwner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public Business createEntity(EntityManager em) {
        Business business = new Business()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return business;
    }

    /**
     * Create another entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public Business createUpdatedEntity(EntityManager em) {
        Business business = new Business()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return business;
    }

    @BeforeEach
    public void initTest() {
        business = createEntity(em);
        
        // Create test users
        testUser = userRepository.findOneByLogin("test").orElse(null);
        if (testUser == null) {
            testUser = new User();
            testUser.setLogin("test");
            testUser.setPassword("$2a$10$vgV4BjJV3r.lZ7vFzoqEI.9m.qfzvJ9BzF9.yuqNzOzgYzZ0ZzZ0Z");
            testUser.setActivated(true);
            testUser.setEmail("test@example.com");
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setLangKey("en");
            testUser = userRepository.saveAndFlush(testUser);
        }
        
        testOwner = userRepository.findOneByLogin("owner").orElse(null);
        if (testOwner == null) {
            testOwner = new User();
            testOwner.setLogin("owner");
            testOwner.setPassword("$2a$10$vgV4BjJV3r.lZ7vFzoqEI.9m.qfzvJ9BzF9.yuqNzOzgYzZ0ZzZ0Z");
            testOwner.setActivated(true);
            testOwner.setEmail("owner@example.com");
            testOwner.setFirstName("Owner");
            testOwner.setLastName("User");
            testOwner.setLangKey("en");
            testOwner = userRepository.saveAndFlush(testOwner);
        }
    }

    @Test
    @Transactional
    void getBusiness_asMember_shouldSucceed() throws Exception {
        // Create business and assign owner
        business.setOwner(testOwner);
        business = businessRepository.saveAndFlush(business);

        // Create BusinessUser relationship to make testUser a member
        BusinessUser businessUser = new BusinessUser()
            .business(business)
            .user(testUser)
            .role(BusinessRole.MEMBER)
            .createdAt(Instant.now())
            .updatedAt(Instant.now());
        businessUserRepository.saveAndFlush(businessUser);

        // Authenticate as testUser who is a member of the business
        restBusinessMockMvc
            .perform(
                get("/api/businesses/{id}", business.getId())
                    .with(user(testUser.getLogin()).roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(business.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getBusiness_asOwner_shouldSucceed() throws Exception {
        // Create business and assign testOwner as owner
        business.setOwner(testOwner);
        business = businessRepository.saveAndFlush(business);

        // Authenticate as the owner of the business
        restBusinessMockMvc
            .perform(
                get("/api/businesses/{id}", business.getId())
                    .with(user(testOwner.getLogin()).roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(business.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getBusiness_asNonMember_shouldReturnForbidden() throws Exception {
        // Create business and assign testOwner as owner
        business.setOwner(testOwner);
        business = businessRepository.saveAndFlush(business);

        // Authenticate as testUser who is NOT a member of the business
        restBusinessMockMvc
            .perform(
                get("/api/businesses/{id}", business.getId())
                    .with(user(testUser.getLogin()).roles("USER"))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void updateBusiness_asOwner_shouldSucceed() throws Exception {
        // Create business and assign testUser as owner
        business.setOwner(testUser);
        business = businessRepository.saveAndFlush(business);

        // Update business data
        BusinessDTO businessDTO = businessMapper.toDto(createUpdatedEntity(em));
        businessDTO.setId(business.getId());

        // Authenticate as the owner of the business
        restBusinessMockMvc
            .perform(
                put("/api/businesses/{id}", business.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessDTO))
                    .with(user(testUser.getLogin()).roles("USER"))
                    .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(business.getId().intValue()))
            .andExpect(jsonPath("$.name").value(UPDATED_NAME));
    }

    @Test
    @Transactional
    void updateBusiness_asNonOwner_shouldReturnForbidden() throws Exception {
        // Create business and assign testOwner as owner
        business.setOwner(testOwner);
        business = businessRepository.saveAndFlush(business);

        // Update business data
        BusinessDTO businessDTO = businessMapper.toDto(createUpdatedEntity(em));
        businessDTO.setId(business.getId());

        // Authenticate as testUser who is NOT the owner of the business
        restBusinessMockMvc
            .perform(
                put("/api/businesses/{id}", business.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessDTO))
                    .with(user(testUser.getLogin()).roles("USER"))
                    .with(csrf())
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void deleteBusiness_asOwner_shouldSucceed() throws Exception {
        // Create business and assign testUser as owner
        business.setOwner(testUser);
        business = businessRepository.saveAndFlush(business);

        // Authenticate as the owner of the business
        restBusinessMockMvc
            .perform(
                delete("/api/businesses/{id}", business.getId())
                    .with(user(testUser.getLogin()).roles("USER"))
                    .with(csrf())
            )
            .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void deleteBusiness_asNonOwner_shouldReturnForbidden() throws Exception {
        // Create business and assign testOwner as owner
        business.setOwner(testOwner);
        business = businessRepository.saveAndFlush(business);

        // Authenticate as testUser who is NOT the owner of the business
        restBusinessMockMvc
            .perform(
                delete("/api/businesses/{id}", business.getId())
                    .with(user(testUser.getLogin()).roles("USER"))
                    .with(csrf())
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void getAllBusinesses_asMember_shouldReturnOnlyMemberBusinesses() throws Exception {
        // Create first business with testOwner as owner
        business.setOwner(testOwner);
        business = businessRepository.saveAndFlush(business);

        // Create second business with testUser as owner
        Business business2 = createUpdatedEntity(em);
        business2.setOwner(testUser);
        business2 = businessRepository.saveAndFlush(business2);

        // Create BusinessUser relationship to make testUser a member of the first business
        BusinessUser businessUser = new BusinessUser()
            .business(business)
            .user(testUser)
            .role(BusinessRole.MEMBER)
            .createdAt(Instant.now())
            .updatedAt(Instant.now());
        businessUserRepository.saveAndFlush(businessUser);

        // Authenticate as testUser - should only see businesses they're a member of
        restBusinessMockMvc
            .perform(
                get("/api/businesses")
                    .with(user(testUser.getLogin()).roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(2))) // Should see both businesses: one as owner, one as member
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business2.getId().intValue())));
    }

    @Test
    @Transactional
    void getAllBusinesses_asNonMember_shouldReturnEmpty() throws Exception {
        // Create business and assign testOwner as owner
        business.setOwner(testOwner);
        business = businessRepository.saveAndFlush(business);

        // Authenticate as testUser who is NOT a member of any business
        restBusinessMockMvc
            .perform(
                get("/api/businesses")
                    .with(user(testUser.getLogin()).roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(0))); // Should return empty list
    }
}