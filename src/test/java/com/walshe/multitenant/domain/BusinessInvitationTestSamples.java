package com.walshe.multitenant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BusinessInvitationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BusinessInvitation getBusinessInvitationSample1() {
        return new BusinessInvitation().id(1L).token("token1").invitedEmail("invitedEmail1");
    }

    public static BusinessInvitation getBusinessInvitationSample2() {
        return new BusinessInvitation().id(2L).token("token2").invitedEmail("invitedEmail2");
    }

    public static BusinessInvitation getBusinessInvitationRandomSampleGenerator() {
        return new BusinessInvitation()
            .id(longCount.incrementAndGet())
            .token(UUID.randomUUID().toString())
            .invitedEmail(UUID.randomUUID().toString());
    }
}
