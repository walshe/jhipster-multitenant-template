package com.walshe.multitenant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BusinessCriteriaTest {

    @Test
    void newBusinessCriteriaHasAllFiltersNullTest() {
        var businessCriteria = new BusinessCriteria();
        assertThat(businessCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void businessCriteriaFluentMethodsCreatesFiltersTest() {
        var businessCriteria = new BusinessCriteria();

        setAllFilters(businessCriteria);

        assertThat(businessCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void businessCriteriaCopyCreatesNullFilterTest() {
        var businessCriteria = new BusinessCriteria();
        var copy = businessCriteria.copy();

        assertThat(businessCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(businessCriteria)
        );
    }

    @Test
    void businessCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var businessCriteria = new BusinessCriteria();
        setAllFilters(businessCriteria);

        var copy = businessCriteria.copy();

        assertThat(businessCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(businessCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var businessCriteria = new BusinessCriteria();

        assertThat(businessCriteria).hasToString("BusinessCriteria{}");
    }

    private static void setAllFilters(BusinessCriteria businessCriteria) {
        businessCriteria.id();
        businessCriteria.name();
        businessCriteria.slug();
        businessCriteria.createdAt();
        businessCriteria.updatedAt();
        businessCriteria.ownerId();
        businessCriteria.distinct();
    }

    private static Condition<BusinessCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getOwnerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BusinessCriteria> copyFiltersAre(BusinessCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getOwnerId(), copy.getOwnerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
