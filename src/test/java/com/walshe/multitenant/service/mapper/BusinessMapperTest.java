package com.walshe.multitenant.service.mapper;

import static com.walshe.multitenant.domain.BusinessAsserts.*;
import static com.walshe.multitenant.domain.BusinessTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessMapperTest {

    private BusinessMapper businessMapper;

    @BeforeEach
    void setUp() {
        businessMapper = new BusinessMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBusinessSample1();
        var actual = businessMapper.toEntity(businessMapper.toDto(expected));
        assertBusinessAllPropertiesEquals(expected, actual);
    }
}
