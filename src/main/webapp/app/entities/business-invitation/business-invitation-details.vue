<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="businessInvitation">
        <h2 class="jh-entity-heading" data-cy="businessInvitationDetailsHeading">
          <span v-text="t$('multitenantApp.businessInvitation.detail.title')"></span> {{ businessInvitation.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.role')"></span>
          </dt>
          <dd>
            <span v-text="t$('multitenantApp.BusinessRole.' + businessInvitation.role)"></span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.token')"></span>
          </dt>
          <dd>
            <span>{{ businessInvitation.token }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.invitedEmail')"></span>
          </dt>
          <dd>
            <span>{{ businessInvitation.invitedEmail }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="businessInvitation.createdAt">{{ formatDateLong(businessInvitation.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="businessInvitation.updatedAt">{{ formatDateLong(businessInvitation.updatedAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.business')"></span>
          </dt>
          <dd>
            <div v-if="businessInvitation.business">
              <router-link :to="{ name: 'BusinessView', params: { businessId: businessInvitation.business.id } }">{{
                businessInvitation.business.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.invitedBy')"></span>
          </dt>
          <dd>
            {{ businessInvitation.invitedBy ? businessInvitation.invitedBy.login : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="businessInvitation.id"
          :to="{ name: 'BusinessInvitationEdit', params: { businessInvitationId: businessInvitation.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./business-invitation-details.component.ts"></script>
