<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="multitenantApp.businessUser.home.createOrEditLabel"
          data-cy="BusinessUserCreateUpdateHeading"
          v-text="t$('multitenantApp.businessUser.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="businessUser.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="businessUser.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.businessUser.role')" for="business-user-role"></label>
            <select
              class="form-control"
              name="role"
              :class="{ valid: !v$.role.$invalid, invalid: v$.role.$invalid }"
              v-model="v$.role.$model"
              id="business-user-role"
              data-cy="role"
              required
            >
              <option
                v-for="businessRole in businessRoleValues"
                :key="businessRole"
                :value="businessRole"
                :label="t$('multitenantApp.BusinessRole.' + businessRole)"
              >
                {{ businessRole }}
              </option>
            </select>
            <div v-if="v$.role.$anyDirty && v$.role.$invalid">
              <small class="form-text text-danger" v-for="error of v$.role.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.businessUser.createdAt')" for="business-user-createdAt"></label>
            <div class="d-flex">
              <input
                id="business-user-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !v$.createdAt.$invalid, invalid: v$.createdAt.$invalid }"
                :value="convertDateTimeFromServer(v$.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.businessUser.updatedAt')" for="business-user-updatedAt"></label>
            <div class="d-flex">
              <input
                id="business-user-updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                class="form-control"
                name="updatedAt"
                :class="{ valid: !v$.updatedAt.$invalid, invalid: v$.updatedAt.$invalid }"
                :value="convertDateTimeFromServer(v$.updatedAt.$model)"
                @change="updateInstantField('updatedAt', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.businessUser.business')" for="business-user-business"></label>
            <select class="form-control" id="business-user-business" data-cy="business" name="business" v-model="businessUser.business">
              <option :value="null"></option>
              <option
                :value="businessUser.business && businessOption.id === businessUser.business.id ? businessUser.business : businessOption"
                v-for="businessOption in businesses"
                :key="businessOption.id"
              >
                {{ businessOption.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.businessUser.user')" for="business-user-user"></label>
            <select class="form-control" id="business-user-user" data-cy="user" name="user" v-model="businessUser.user">
              <option :value="null"></option>
              <option
                :value="businessUser.user && userOption.id === businessUser.user.id ? businessUser.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./business-user-update.component.ts"></script>
