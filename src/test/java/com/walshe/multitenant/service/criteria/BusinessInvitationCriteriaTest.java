package com.walshe.multitenant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BusinessInvitationCriteriaTest {

    @Test
    void newBusinessInvitationCriteriaHasAllFiltersNullTest() {
        var businessInvitationCriteria = new BusinessInvitationCriteria();
        assertThat(businessInvitationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void businessInvitationCriteriaFluentMethodsCreatesFiltersTest() {
        var businessInvitationCriteria = new BusinessInvitationCriteria();

        setAllFilters(businessInvitationCriteria);

        assertThat(businessInvitationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void businessInvitationCriteriaCopyCreatesNullFilterTest() {
        var businessInvitationCriteria = new BusinessInvitationCriteria();
        var copy = businessInvitationCriteria.copy();

        assertThat(businessInvitationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(businessInvitationCriteria)
        );
    }

    @Test
    void businessInvitationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var businessInvitationCriteria = new BusinessInvitationCriteria();
        setAllFilters(businessInvitationCriteria);

        var copy = businessInvitationCriteria.copy();

        assertThat(businessInvitationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(businessInvitationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var businessInvitationCriteria = new BusinessInvitationCriteria();

        assertThat(businessInvitationCriteria).hasToString("BusinessInvitationCriteria{}");
    }

    private static void setAllFilters(BusinessInvitationCriteria businessInvitationCriteria) {
        businessInvitationCriteria.id();
        businessInvitationCriteria.role();
        businessInvitationCriteria.token();
        businessInvitationCriteria.invitedEmail();
        businessInvitationCriteria.createdAt();
        businessInvitationCriteria.updatedAt();
        businessInvitationCriteria.businessId();
        businessInvitationCriteria.invitedById();
        businessInvitationCriteria.distinct();
    }

    private static Condition<BusinessInvitationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getToken()) &&
                condition.apply(criteria.getInvitedEmail()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getBusinessId()) &&
                condition.apply(criteria.getInvitedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BusinessInvitationCriteria> copyFiltersAre(
        BusinessInvitationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getToken(), copy.getToken()) &&
                condition.apply(criteria.getInvitedEmail(), copy.getInvitedEmail()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getBusinessId(), copy.getBusinessId()) &&
                condition.apply(criteria.getInvitedById(), copy.getInvitedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
