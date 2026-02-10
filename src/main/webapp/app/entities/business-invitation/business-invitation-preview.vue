<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="businessInvitation">
        <h2 class="jh-entity-heading" data-cy="businessInvitationPreviewHeading">
          <span v-text="t$('multitenantApp.businessInvitation.preview.title')"></span>
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.role')"></span>
          </dt>
          <dd>
            <span v-text="t$('multitenantApp.BusinessRole.' + businessInvitation.role)"></span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.invitedEmail')"></span>
          </dt>
          <dd>
            {{ businessInvitation.invitedEmail }}
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('multitenantApp.InvitationStatus.' + businessInvitation.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.business')"></span>
          </dt>
          <dd>
            {{ businessInvitation.business?.name }}
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.invitedBy')"></span>
          </dt>
          <dd>
            {{ businessInvitation.invitedBy?.login }}
          </dd>
          <dt>
            <span v-text="t$('multitenantApp.businessInvitation.createdAt')"></span>
          </dt>
          <dd>
            {{ formatDateLong(businessInvitation.createdAt) }}
          </dd>
        </dl>
        <div v-if="businessInvitation.status === 'PENDING'">
          <div v-if="!isAuthenticated">
            <p v-text="t$('multitenantApp.businessInvitation.preview.notLoggedIn')"></p>
            <router-link :to="{ name: 'Login' }" class="btn btn-primary" data-cy="loginButton">
              <span v-text="t$('multitenantApp.businessInvitation.preview.loginToAccept')"></span>
            </router-link>
            <router-link :to="{ name: 'Register' }" class="btn btn-secondary ml-2" data-cy="registerButton">
              <span v-text="t$('multitenantApp.businessInvitation.preview.registerToAccept')"></span>
            </router-link>
          </div>
          <div v-else>
            <button @click="acceptInvitation()" :disabled="isAccepting" class="btn btn-success" data-cy="acceptInvitationButton">
              <span v-if="!isAccepting" v-text="t$('multitenantApp.businessInvitation.preview.acceptInvitation')"></span>
              <span v-else>
                <font-awesome-icon icon="sync" spin></font-awesome-icon>
                <span v-text="t$('multitenantApp.businessInvitation.preview.accepting')"></span>
              </span>
            </button>
          </div>
        </div>
        <div v-else-if="businessInvitation.status === 'ACCEPTED'">
          <p class="alert alert-info" v-text="t$('multitenantApp.businessInvitation.preview.alreadyAccepted')"></p>
        </div>
        <div v-else-if="businessInvitation.status === 'EXPIRED'">
          <p class="alert alert-warning" v-text="t$('multitenantApp.businessInvitation.preview.expired')"></p>
        </div>
      </div>
      <div v-else>
        <div v-if="loading" class="alert alert-info">
          <span v-text="t$('multitenantApp.businessInvitation.preview.loading')"></span>
        </div>
        <div v-else-if="error" class="alert alert-danger">
          <span v-text="t$('multitenantApp.businessInvitation.preview.error')"></span>
        </div>
        <div v-else class="alert alert-warning">
          <span v-text="t$('multitenantApp.businessInvitation.preview.notFound')"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./business-invitation-preview.component.ts"></script>