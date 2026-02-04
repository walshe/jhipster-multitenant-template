<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="businessUser">
        <h2 class="jh-entity-heading" data-cy="businessUserDetailsHeading">
          <span v-text="t$('multitenantApp.businessUser.detail.title')"></span> {{ businessUser.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('multitenantApp.businessUser.role')"></span>
          </dt>
          <dd>
            <span v-text="t$('multitenantApp.BusinessRole.' + businessUser.role)"></span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessUser.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="businessUser.createdAt">{{ formatDateLong(businessUser.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessUser.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="businessUser.updatedAt">{{ formatDateLong(businessUser.updatedAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessUser.business')"></span>
          </dt>
          <dd>
            <div v-if="businessUser.business">
              <router-link :to="{ name: 'BusinessView', params: { businessId: businessUser.business.id } }">{{
                businessUser.business.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessUser.user')"></span>
          </dt>
          <dd>
            {{ businessUser.user ? businessUser.user.login : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./business-user-details.component.ts"></script>
