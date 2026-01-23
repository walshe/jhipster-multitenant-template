package com.walshe.multitenant.service.mapper;

import static com.walshe.multitenant.domain.BusinessInvitationAsserts.*;
import static com.walshe.multitenant.domain.BusinessInvitationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessInvitationMapperTest {

    private BusinessInvitationMapper businessInvitationMapper;

    @BeforeEach
    void setUp() {
        businessInvitationMapper = new BusinessInvitationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBusinessInvitationSample1();
        var actual = businessInvitationMapper.toEntity(businessInvitationMapper.toDto(expected));
        assertBusinessInvitationAllPropertiesEquals(expected, actual);
    }
}
