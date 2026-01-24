import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BusinessService from './business.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { Business, type IBusiness } from '@/shared/model/business.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BusinessUpdate',
  setup() {
    const businessService = inject('businessService', () => new BusinessService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const business: Ref<IBusiness> = ref(new Business());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveBusiness = async businessId => {
      try {
        const res = await businessService().find(businessId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        business.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.businessId) {
      retrieveBusiness(route.params.businessId);
    }

    // Load business members only when editing an existing business
    const initRelationships = async () => {
      if (route.params?.businessId) {
        // Wait for the business to be loaded before trying to get members
        setTimeout(async () => {
          try {
            const res = await businessService().retrieveBusinessMembers(Number(route.params.businessId));
            users.value = res.data;
          } catch (error) {
            // If the user doesn't have permission to view business members, fall back to loading all users
            // This might happen if the user is not the owner of the business
            try {
              const res = await userService().retrieve();
              users.value = res.data;
            } catch (fallbackError) {
              // If both fail, just continue without users
              console.error('Could not load users:', fallbackError);
            }
          }
        }, 0);
      } else {
        // For new business creation, we don't need to load users
        // The owner field is hidden during creation anyway
      }
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      slug: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdAt: {},
      updatedAt: {},
    };
    const v$ = useVuelidate(validationRules, business as any);
    v$.value.$validate();

    return {
      businessService,
      alertService,
      business,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: business }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.business.id) {
        // For updates, we can modify the owner field
        this.businessService()
          .update(this.business)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('multitenantApp.business.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        // For creation, we should not send the owner field as it will be auto-assigned
        // Create a copy of the business without the owner field for creation
        const businessForCreation = { ...this.business };
        delete businessForCreation.owner;

        this.businessService()
          .create(businessForCreation)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('multitenantApp.business.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
