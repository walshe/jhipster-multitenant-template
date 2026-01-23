package com.walshe.multitenant.domain;

import static com.walshe.multitenant.domain.BusinessTestSamples.*;
import static com.walshe.multitenant.domain.BusinessUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.walshe.multitenant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessUser.class);
        BusinessUser businessUser1 = getBusinessUserSample1();
        BusinessUser businessUser2 = new BusinessUser();
        assertThat(businessUser1).isNotEqualTo(businessUser2);

        businessUser2.setId(businessUser1.getId());
        assertThat(businessUser1).isEqualTo(businessUser2);

        businessUser2 = getBusinessUserSample2();
        assertThat(businessUser1).isNotEqualTo(businessUser2);
    }

    @Test
    void businessTest() {
        BusinessUser businessUser = getBusinessUserRandomSampleGenerator();
        Business businessBack = getBusinessRandomSampleGenerator();

        businessUser.setBusiness(businessBack);
        assertThat(businessUser.getBusiness()).isEqualTo(businessBack);

        businessUser.business(null);
        assertThat(businessUser.getBusiness()).isNull();
    }
}
