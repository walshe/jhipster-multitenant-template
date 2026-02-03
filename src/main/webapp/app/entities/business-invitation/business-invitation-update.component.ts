import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BusinessInvitationService from './business-invitation.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import BusinessService from '@/entities/business/business.service';
import { type IBusiness } from '@/shared/model/business.model';
import UserService from '@/entities/user/user.service';
import { BusinessInvitation, type IBusinessInvitation } from '@/shared/model/business-invitation.model';
import { BusinessRole } from '@/shared/model/enumerations/business-role.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BusinessInvitationUpdate',
  setup() {
    const businessInvitationService = inject('businessInvitationService', () => new BusinessInvitationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const businessInvitation: Ref<IBusinessInvitation> = ref(new BusinessInvitation());

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

    const retrieveBusinessInvitation = async businessInvitationId => {
      try {
        const res = await businessInvitationService().find(businessInvitationId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        businessInvitation.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.businessInvitationId) {
      retrieveBusinessInvitation(route.params.businessInvitationId);
    } else {
      // default role to MEMBER for new records
      businessInvitation.value.role = 'MEMBER' as any;
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
      token: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      invitedEmail: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdAt: {},
      updatedAt: {},
      business: {},
      invitedBy: {},
    };
    const v$ = useVuelidate(validationRules, businessInvitation as any);
    v$.value.$validate();

    return {
      businessInvitationService,
      alertService,
      businessInvitation,
      previousState,
      businessRoleValues,
      isSaving,
      currentLanguage,
      businesses,
      users,
      v$,
      ...useDateFormat({ entityRef: businessInvitation }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.businessInvitation.id) {
        this.businessInvitationService()
          .update(this.businessInvitation)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('multitenantApp.businessInvitation.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.businessInvitationService()
          .create(this.businessInvitation)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('multitenantApp.businessInvitation.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
