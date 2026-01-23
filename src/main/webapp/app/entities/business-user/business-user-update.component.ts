import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BusinessUserService from './business-user.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import BusinessService from '@/entities/business/business.service';
import { type IBusiness } from '@/shared/model/business.model';
import UserService from '@/entities/user/user.service';
import { BusinessUser, type IBusinessUser } from '@/shared/model/business-user.model';
import { BusinessRole } from '@/shared/model/enumerations/business-role.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BusinessUserUpdate',
  setup() {
    const businessUserService = inject('businessUserService', () => new BusinessUserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const businessUser: Ref<IBusinessUser> = ref(new BusinessUser());

    const businessService = inject('businessService', () => new BusinessService());

    const businesses: Ref<IBusiness[]> = ref([]);
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const businessRoleValues: Ref<string[]> = ref(Object.keys(BusinessRole));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveBusinessUser = async businessUserId => {
      try {
        const res = await businessUserService().find(businessUserId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        businessUser.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.businessUserId) {
      retrieveBusinessUser(route.params.businessUserId);
    }

    const initRelationships = () => {
      businessService()
        .retrieve()
        .then(res => {
          businesses.value = res.data;
        });
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      role: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdAt: {},
      updatedAt: {},
      business: {},
      user: {},
    };
    const v$ = useVuelidate(validationRules, businessUser as any);
    v$.value.$validate();

    return {
      businessUserService,
      alertService,
      businessUser,
      previousState,
      businessRoleValues,
      isSaving,
      currentLanguage,
      businesses,
      users,
      v$,
      ...useDateFormat({ entityRef: businessUser }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.businessUser.id) {
        this.businessUserService()
          .update(this.businessUser)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('multitenantApp.businessUser.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.businessUserService()
          .create(this.businessUser)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('multitenantApp.businessUser.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
