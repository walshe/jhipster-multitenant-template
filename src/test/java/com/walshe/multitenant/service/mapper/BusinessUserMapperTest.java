package com.walshe.multitenant.service.mapper;

import static com.walshe.multitenant.domain.BusinessUserAsserts.*;
import static com.walshe.multitenant.domain.BusinessUserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessUserMapperTest {

    private BusinessUserMapper businessUserMapper;

    @BeforeEach
    void setUp() {
        businessUserMapper = new BusinessUserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBusinessUserSample1();
        var actual = businessUserMapper.toEntity(businessUserMapper.toDto(expected));
        assertBusinessUserAllPropertiesEquals(expected, actual);
    }
}
