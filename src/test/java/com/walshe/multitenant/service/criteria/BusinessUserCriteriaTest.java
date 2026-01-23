package com.walshe.multitenant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BusinessUserCriteriaTest {

    @Test
    void newBusinessUserCriteriaHasAllFiltersNullTest() {
        var businessUserCriteria = new BusinessUserCriteria();
        assertThat(businessUserCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void businessUserCriteriaFluentMethodsCreatesFiltersTest() {
        var businessUserCriteria = new BusinessUserCriteria();

        setAllFilters(businessUserCriteria);

        assertThat(businessUserCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void businessUserCriteriaCopyCreatesNullFilterTest() {
        var businessUserCriteria = new BusinessUserCriteria();
        var copy = businessUserCriteria.copy();

        assertThat(businessUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(businessUserCriteria)
        );
    }

    @Test
    void businessUserCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var businessUserCriteria = new BusinessUserCriteria();
        setAllFilters(businessUserCriteria);

        var copy = businessUserCriteria.copy();

        assertThat(businessUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(businessUserCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var businessUserCriteria = new BusinessUserCriteria();

        assertThat(businessUserCriteria).hasToString("BusinessUserCriteria{}");
    }

    private static void setAllFilters(BusinessUserCriteria businessUserCriteria) {
        businessUserCriteria.id();
        businessUserCriteria.role();
        businessUserCriteria.createdAt();
        businessUserCriteria.updatedAt();
        businessUserCriteria.businessId();
        businessUserCriteria.userId();
        businessUserCriteria.distinct();
    }

    private static Condition<BusinessUserCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getBusinessId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BusinessUserCriteria> copyFiltersAre(
        BusinessUserCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getBusinessId(), copy.getBusinessId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
