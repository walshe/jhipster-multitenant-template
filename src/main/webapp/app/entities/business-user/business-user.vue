<template>
  <div>
    <h2 id="page-heading" data-cy="BusinessUserHeading">
      <span v-text="t$('multitenantApp.businessUser.home.title')" id="business-user-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('multitenantApp.businessUser.home.refreshListLabel')"></span>
        </button>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && businessUsers && businessUsers.length === 0">
      <span v-text="t$('multitenantApp.businessUser.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="businessUsers && businessUsers.length > 0">
      <table class="table table-striped" aria-describedby="businessUsers">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('role')">
              <span v-text="t$('multitenantApp.businessUser.role')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'role'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span v-text="t$('multitenantApp.businessUser.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('updatedAt')">
              <span v-text="t$('multitenantApp.businessUser.updatedAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('business.name')">
              <span v-text="t$('multitenantApp.businessUser.business')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'business.name'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.login')">
              <span v-text="t$('multitenantApp.businessUser.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.login'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="businessUser in businessUsers" :key="businessUser.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BusinessUserView', params: { businessUserId: businessUser.id } }">{{
                businessUser.id
              }}</router-link>
            </td>
            <td v-text="t$('multitenantApp.BusinessRole.' + businessUser.role)"></td>
            <td>{{ formatDateShort(businessUser.createdAt) || '' }}</td>
            <td>{{ formatDateShort(businessUser.updatedAt) || '' }}</td>
            <td>
              <div v-if="businessUser.business">
                <router-link :to="{ name: 'BusinessView', params: { businessId: businessUser.business.id } }">{{
                  businessUser.business.name
                }}</router-link>
              </div>
            </td>
            <td>
              {{ businessUser.user ? businessUser.user.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'BusinessUserView', params: { businessUserId: businessUser.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-show="businessUsers && businessUsers.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./business-user.component.ts"></script>
