package com.walshe.multitenant.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class BusinessUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BusinessUser getBusinessUserSample1() {
        return new BusinessUser().id(1L);
    }

    public static BusinessUser getBusinessUserSample2() {
        return new BusinessUser().id(2L);
    }

    public static BusinessUser getBusinessUserRandomSampleGenerator() {
        return new BusinessUser().id(longCount.incrementAndGet());
    }
}
