package com.walshe.multitenant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.walshe.multitenant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessUserDTO.class);
        BusinessUserDTO businessUserDTO1 = new BusinessUserDTO();
        businessUserDTO1.setId(1L);
        BusinessUserDTO businessUserDTO2 = new BusinessUserDTO();
        assertThat(businessUserDTO1).isNotEqualTo(businessUserDTO2);
        businessUserDTO2.setId(businessUserDTO1.getId());
        assertThat(businessUserDTO1).isEqualTo(businessUserDTO2);
        businessUserDTO2.setId(2L);
        assertThat(businessUserDTO1).isNotEqualTo(businessUserDTO2);
        businessUserDTO1.setId(null);
        assertThat(businessUserDTO1).isNotEqualTo(businessUserDTO2);
    }
}
