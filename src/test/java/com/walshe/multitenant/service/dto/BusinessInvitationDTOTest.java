package com.walshe.multitenant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.walshe.multitenant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessInvitationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessInvitationDTO.class);
        BusinessInvitationDTO businessInvitationDTO1 = new BusinessInvitationDTO();
        businessInvitationDTO1.setId(1L);
        BusinessInvitationDTO businessInvitationDTO2 = new BusinessInvitationDTO();
        assertThat(businessInvitationDTO1).isNotEqualTo(businessInvitationDTO2);
        businessInvitationDTO2.setId(businessInvitationDTO1.getId());
        assertThat(businessInvitationDTO1).isEqualTo(businessInvitationDTO2);
        businessInvitationDTO2.setId(2L);
        assertThat(businessInvitationDTO1).isNotEqualTo(businessInvitationDTO2);
        businessInvitationDTO1.setId(null);
        assertThat(businessInvitationDTO1).isNotEqualTo(businessInvitationDTO2);
    }
}
