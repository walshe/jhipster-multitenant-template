<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="multitenantApp.businessInvitation.home.createOrEditLabel"
          data-cy="BusinessInvitationCreateUpdateHeading"
          v-text="t$('multitenantApp.businessInvitation.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="businessInvitation.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="businessInvitation.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('multitenantApp.businessInvitation.role')" for="business-invitation-role"></label>
            <select
              class="form-control"
              name="role"
              :class="{ valid: !v$.role.$invalid, invalid: v$.role.$invalid }"
              v-model="v$.role.$model"
              id="business-invitation-role"
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
            <label
              class="form-control-label"
              v-text="t$('multitenantApp.businessInvitation.token')"
              for="business-invitation-token"
            ></label>
            <input
              type="text"
              class="form-control"
              name="token"
              id="business-invitation-token"
              data-cy="token"
              :class="{ valid: !v$.token.$invalid, invalid: v$.token.$invalid }"
              v-model="v$.token.$model"
              required
            />
            <div v-if="v$.token.$anyDirty && v$.token.$invalid">
              <small class="form-text text-danger" v-for="error of v$.token.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('multitenantApp.businessInvitation.invitedEmail')"
              for="business-invitation-invitedEmail"
            ></label>
            <input
              type="text"
              class="form-control"
              name="invitedEmail"
              id="business-invitation-invitedEmail"
              data-cy="invitedEmail"
              :class="{ valid: !v$.invitedEmail.$invalid, invalid: v$.invitedEmail.$invalid }"
              v-model="v$.invitedEmail.$model"
              required
            />
            <div v-if="v$.invitedEmail.$anyDirty && v$.invitedEmail.$invalid">
              <small class="form-text text-danger" v-for="error of v$.invitedEmail.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('multitenantApp.businessInvitation.createdAt')"
              for="business-invitation-createdAt"
            ></label>
            <div class="d-flex">
              <input
                id="business-invitation-createdAt"
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
            <label
              class="form-control-label"
              v-text="t$('multitenantApp.businessInvitation.updatedAt')"
              for="business-invitation-updatedAt"
            ></label>
            <div class="d-flex">
              <input
                id="business-invitation-updatedAt"
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
            <label
              class="form-control-label"
              v-text="t$('multitenantApp.businessInvitation.business')"
              for="business-invitation-business"
            ></label>
            <select
              class="form-control"
              id="business-invitation-business"
              data-cy="business"
              name="business"
              v-model="businessInvitation.business"
            >
              <option :value="null"></option>
              <option
                :value="
                  businessInvitation.business && businessOption.id === businessInvitation.business.id
                    ? businessInvitation.business
                    : businessOption
                "
                v-for="businessOption in businesses"
                :key="businessOption.id"
              >
                {{ businessOption.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('multitenantApp.businessInvitation.invitedBy')"
              for="business-invitation-invitedBy"
            ></label>
            <select
              class="form-control"
              id="business-invitation-invitedBy"
              data-cy="invitedBy"
              name="invitedBy"
              v-model="businessInvitation.invitedBy"
            >
              <option :value="null"></option>
              <option
                :value="
                  businessInvitation.invitedBy && userOption.id === businessInvitation.invitedBy.id
                    ? businessInvitation.invitedBy
                    : userOption
                "
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
<script lang="ts" src="./business-invitation-update.component.ts"></script>
