<template>
  <div>
    <h2 id="page-heading" data-cy="BusinessInvitationHeading">
      <span v-text="t$('multitenantApp.businessInvitation.home.title')" id="business-invitation-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('multitenantApp.businessInvitation.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'BusinessInvitationCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-business-invitation"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('multitenantApp.businessInvitation.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && businessInvitations && businessInvitations.length === 0">
      <span v-text="t$('multitenantApp.businessInvitation.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="businessInvitations && businessInvitations.length > 0">
      <table class="table table-striped" aria-describedby="businessInvitations">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('role')">
              <span v-text="t$('multitenantApp.businessInvitation.role')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'role'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('token')">
              <span v-text="t$('multitenantApp.businessInvitation.token')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'token'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('invitedEmail')">
              <span v-text="t$('multitenantApp.businessInvitation.invitedEmail')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'invitedEmail'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span v-text="t$('multitenantApp.businessInvitation.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('updatedAt')">
              <span v-text="t$('multitenantApp.businessInvitation.updatedAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('business.name')">
              <span v-text="t$('multitenantApp.businessInvitation.business')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'business.name'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('invitedBy.login')">
              <span v-text="t$('multitenantApp.businessInvitation.invitedBy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'invitedBy.login'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="businessInvitation in businessInvitations" :key="businessInvitation.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BusinessInvitationView', params: { businessInvitationId: businessInvitation.id } }">{{
                businessInvitation.id
              }}</router-link>
            </td>
            <td v-text="t$('multitenantApp.BusinessRole.' + businessInvitation.role)"></td>
            <td>{{ businessInvitation.token }}</td>
            <td>{{ businessInvitation.invitedEmail }}</td>
            <td>{{ formatDateShort(businessInvitation.createdAt) || '' }}</td>
            <td>{{ formatDateShort(businessInvitation.updatedAt) || '' }}</td>
            <td>
              <div v-if="businessInvitation.business">
                <router-link :to="{ name: 'BusinessView', params: { businessId: businessInvitation.business.id } }">{{
                  businessInvitation.business.name
                }}</router-link>
              </div>
            </td>
            <td>
              {{ businessInvitation.invitedBy ? businessInvitation.invitedBy.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'BusinessInvitationView', params: { businessInvitationId: businessInvitation.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'BusinessInvitationEdit', params: { businessInvitationId: businessInvitation.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(businessInvitation)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="multitenantApp.businessInvitation.delete.question"
          data-cy="businessInvitationDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p
          id="jhi-delete-businessInvitation-heading"
          v-text="t$('multitenantApp.businessInvitation.delete.question', { id: removeId })"
        ></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-businessInvitation"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeBusinessInvitation()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="businessInvitations && businessInvitations.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./business-invitation.component.ts"></script>
