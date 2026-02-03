package com.walshe.multitenant.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.walshe.multitenant.IntegrationTest;
import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.BusinessRole;
import com.walshe.multitenant.repository.BusinessRepository;
import com.walshe.multitenant.repository.BusinessUserRepository;
import com.walshe.multitenant.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
class BusinessVisibilityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private EntityManager em;

    private User owner;
    private User member;
    private User other;
    private Business business;

    @BeforeEach
    void setup() {
        owner = createUser("owner");
        member = createUser("member");
        other = createUser("other");

        userRepository.saveAndFlush(owner);
        userRepository.saveAndFlush(member);
        userRepository.saveAndFlush(other);

        business = new Business();
        business.setName("Shop A");
        business.setSlug("shop-a" + System.nanoTime());
        business.setCreatedAt(Instant.now());
        business.setUpdatedAt(Instant.now());
        business.setOwner(owner);
        business = businessRepository.saveAndFlush(business);

        BusinessUser bu = new BusinessUser();
        bu.setBusiness(business);
        bu.setUser(member);
        bu.setRole(BusinessRole.MEMBER);
        bu.setCreatedAt(Instant.now());
        bu.setUpdatedAt(Instant.now());
        businessUserRepository.saveAndFlush(bu);
        em.clear();
    }

    private static User createUser(String login) {
        User u = new User();
        u.setLogin(login);
        u.setPassword("password");
        u.setActivated(true);
        u.setEmail(login + "@example.com");
        u.setFirstName(login);
        u.setLastName("tester");
        u.setImageUrl("");
        u.setLangKey("en");
        return u;
    }

    @Test
    @Transactional
    @WithMockUser(username = "owner")
    void listBusinesses_asOwner_seesOwnedBusiness() throws Exception {
        mockMvc
            .perform(get("/api/businesses").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())));

        mockMvc
            .perform(get("/api/businesses/count").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "member")
    void listBusinesses_asMember_seesMemberBusiness() throws Exception {
        mockMvc
            .perform(get("/api/businesses").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())));

        mockMvc
            .perform(get("/api/businesses/count").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "other")
    void listBusinesses_asNonMember_seesEmpty() throws Exception {
        mockMvc
            .perform(get("/api/businesses").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        mockMvc
            .perform(get("/api/businesses/count").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "other")
    void getBusiness_asNonMember_gets404() throws Exception {
        mockMvc.perform(get("/api/businesses/{id}", business.getId())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username = "member")
    void listBusinesses_withFilter_combinesWithMembership() throws Exception {
        // Add another business with different name; member shouldn't see it without membership
        Business otherBiz = new Business();
        otherBiz.setName("Not Visible Shop");
        otherBiz.setSlug("not-visible-" + System.nanoTime());
        otherBiz.setCreatedAt(Instant.now());
        otherBiz.setUpdatedAt(Instant.now());
        businessRepository.saveAndFlush(otherBiz);

        mockMvc
            .perform(get("/api/businesses?name.contains=Shop").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())));
    }
}
