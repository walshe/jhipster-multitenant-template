<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="business">
        <h2 class="jh-entity-heading" data-cy="businessDetailsHeading">
          <span v-text="t$('multitenantApp.business.detail.title')"></span> {{ business.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('multitenantApp.business.name')"></span>
          </dt>
          <dd>
            <span>{{ business.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.business.slug')"></span>
          </dt>
          <dd>
            <span>{{ business.slug }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.business.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="business.createdAt">{{ formatDateLong(business.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.business.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="business.updatedAt">{{ formatDateLong(business.updatedAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.business.owner')"></span>
          </dt>
          <dd>
            {{ business.owner ? business.owner.login : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <div v-if="isBusinessOwner()">
          <router-link v-if="business.id" :to="{ name: 'BusinessEdit', params: { businessId: business.id } }" custom v-slot="{ navigate }">
            <button @click="navigate" class="btn btn-primary">
              <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
            </button>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./business-details.component.ts"></script>
