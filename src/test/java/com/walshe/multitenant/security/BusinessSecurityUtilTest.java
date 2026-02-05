package com.walshe.multitenant.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.service.BusinessAuthorizationService;
import com.walshe.multitenant.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessSecurityUtilTest {

    @Mock
    private BusinessAuthorizationService businessAuthorizationService;

    @Mock
    private UserService userService;

    private BusinessSecurityUtil businessSecurityUtil;

    @BeforeEach
    void setUp() {
        businessSecurityUtil = new BusinessSecurityUtil(businessAuthorizationService, userService);
    }

    @Test
    void testIsBusinessMember_withValidBusinessIdAndMember_returnsTrue() {
        // Given
        Long businessId = 1L;
        when(businessAuthorizationService.isBusinessMember(businessId)).thenReturn(true);

        // When
        boolean result = businessSecurityUtil.isBusinessMember(businessId);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsBusinessMember_withValidBusinessIdAndNotMember_returnsFalse() {
        // Given
        Long businessId = 1L;
        when(businessAuthorizationService.isBusinessMember(businessId)).thenReturn(false);

        // When
        boolean result = businessSecurityUtil.isBusinessMember(businessId);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsBusinessMember_withNullBusinessId_returnsFalse() {
        // Given
        Long businessId = null;

        // When
        boolean result = businessSecurityUtil.isBusinessMember(businessId);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsBusinessOwner_withValidBusinessIdAndOwner_returnsTrue() {
        // Given
        Long businessId = 1L;
        when(businessAuthorizationService.isBusinessOwner(businessId)).thenReturn(true);

        // When
        boolean result = businessSecurityUtil.isBusinessOwner(businessId);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsBusinessOwner_withValidBusinessIdAndNotOwner_returnsFalse() {
        // Given
        Long businessId = 1L;
        when(businessAuthorizationService.isBusinessOwner(businessId)).thenReturn(false);

        // When
        boolean result = businessSecurityUtil.isBusinessOwner(businessId);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsBusinessOwner_withNullBusinessId_returnsFalse() {
        // Given
        Long businessId = null;

        // When
        boolean result = businessSecurityUtil.isBusinessOwner(businessId);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsAuthenticated_whenUserPresent_returnsTrue() {
        // Given
        User mockUser = mock(User.class);
        when(userService.getUserWithAuthorities()).thenReturn(Optional.of(mockUser));

        // When
        boolean result = businessSecurityUtil.isAuthenticated();

        // Then
        assertTrue(result);
    }

    @Test
    void testIsAuthenticated_whenUserNotPresent_returnsFalse() {
        // Given
        when(userService.getUserWithAuthorities()).thenReturn(Optional.empty());

        // When
        boolean result = businessSecurityUtil.isAuthenticated();

        // Then
        assertFalse(result);
    }
}