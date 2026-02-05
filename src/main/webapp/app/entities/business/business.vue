<template>
  <div>
    <h2 id="page-heading" data-cy="BusinessHeading">
      <span v-text="t$('multitenantApp.business.home.title')" id="business-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('multitenantApp.business.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'BusinessCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-business"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('multitenantApp.business.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && businesses && businesses.length === 0">
      <span v-text="t$('multitenantApp.business.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="businesses && businesses.length > 0">
      <table class="table table-striped" aria-describedby="businesses">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('name')">
              <span v-text="t$('multitenantApp.business.name')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('slug')">
              <span v-text="t$('multitenantApp.business.slug')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'slug'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span v-text="t$('multitenantApp.business.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('updatedAt')">
              <span v-text="t$('multitenantApp.business.updatedAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('owner.login')">
              <span v-text="t$('multitenantApp.business.owner')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'owner.login'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="business in businesses" :key="business.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BusinessView', params: { businessId: business.id } }">{{ business.id }}</router-link>
            </td>
            <td>{{ business.name }}</td>
            <td>{{ business.slug }}</td>
            <td>{{ formatDateShort(business.createdAt) || '' }}</td>
            <td>{{ formatDateShort(business.updatedAt) || '' }}</td>
            <td>
              {{ business.owner ? business.owner.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'BusinessView', params: { businessId: business.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <div v-if="isBusinessOwner(business)">
                  <router-link :to="{ name: 'BusinessEdit', params: { businessId: business.id } }" custom v-slot="{ navigate }">
                    <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                      <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                      <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                    </button>
                  </router-link>
                  <b-button
                    @click="prepareRemove(business)"
                    variant="danger"
                    class="btn btn-sm"
                    data-cy="entityDeleteButton"
                    v-b-modal.removeEntity
                  >
                    <font-awesome-icon icon="times"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                  </b-button>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="multitenantApp.business.delete.question" data-cy="businessDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-business-heading" v-text="t$('multitenantApp.business.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-business"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeBusiness()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="businesses && businesses.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./business.component.ts"></script>
