<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="multitenantApp.business.home.createOrEditLabel"
          data-cy="BusinessCreateUpdateHeading"
          v-text="t$('multitenantApp.business.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="business.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="business.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.business.name')" for="business-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="business-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.business.slug')" for="business-slug"></label>
            <input
              type="text"
              class="form-control"
              name="slug"
              id="business-slug"
              data-cy="slug"
              :class="{ valid: !v$.slug.$invalid, invalid: v$.slug.$invalid }"
              v-model="v$.slug.$model"
              required
            />
            <div v-if="v$.slug.$anyDirty && v$.slug.$invalid">
              <small class="form-text text-danger" v-for="error of v$.slug.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.business.createdAt')" for="business-createdAt"></label>
            <div class="d-flex">
              <input
                id="business-createdAt"
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
            <label class="form-control-label" v-text="t$('multitenantApp.business.updatedAt')" for="business-updatedAt"></label>
            <div class="d-flex">
              <input
                id="business-updatedAt"
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
            <label class="form-control-label" v-text="t$('multitenantApp.business.owner')" for="business-owner"></label>
            <select class="form-control" id="business-owner" data-cy="owner" name="owner" v-model="business.owner">
              <option :value="null"></option>
              <option
                :value="business.owner && userOption.id === business.owner.id ? business.owner : userOption"
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
<script lang="ts" src="./business-update.component.ts"></script>
