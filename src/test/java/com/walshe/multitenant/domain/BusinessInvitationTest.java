package com.walshe.multitenant.domain;

import static com.walshe.multitenant.domain.BusinessInvitationTestSamples.*;
import static com.walshe.multitenant.domain.BusinessTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.walshe.multitenant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessInvitationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessInvitation.class);
        BusinessInvitation businessInvitation1 = getBusinessInvitationSample1();
        BusinessInvitation businessInvitation2 = new BusinessInvitation();
        assertThat(businessInvitation1).isNotEqualTo(businessInvitation2);

        businessInvitation2.setId(businessInvitation1.getId());
        assertThat(businessInvitation1).isEqualTo(businessInvitation2);

        businessInvitation2 = getBusinessInvitationSample2();
        assertThat(businessInvitation1).isNotEqualTo(businessInvitation2);
    }

    @Test
    void businessTest() {
        BusinessInvitation businessInvitation = getBusinessInvitationRandomSampleGenerator();
        Business businessBack = getBusinessRandomSampleGenerator();

        businessInvitation.setBusiness(businessBack);
        assertThat(businessInvitation.getBusiness()).isEqualTo(businessBack);

        businessInvitation.business(null);
        assertThat(businessInvitation.getBusiness()).isNull();
    }
}
