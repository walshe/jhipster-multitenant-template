package com.walshe.multitenant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BusinessTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Business getBusinessSample1() {
        return new Business().id(1L).name("name1").slug("slug1");
    }

    public static Business getBusinessSample2() {
        return new Business().id(2L).name("name2").slug("slug2");
    }

    public static Business getBusinessRandomSampleGenerator() {
        return new Business().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).slug(UUID.randomUUID().toString());
    }
}
