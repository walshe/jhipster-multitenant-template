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
      // Fetch businesses owned by the current user
      businessService()
        .retrieveBusinessesOwnedByCurrentUser()
        .then(res => {
          businesses.value = res.data;
        })
        .catch(error => {
          console.error('Error fetching businesses owned by current user:', error);
          // Fallback to regular businesses if the owned endpoint fails
          businessService()
            .retrieve()
            .then(res => {
              businesses.value = res.data;
            })
            .catch(err => {
              console.error('Error fetching businesses:', err);
            });
        });
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        })
        .catch(error => {
          console.error('Error fetching users:', error);
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      role: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      // Token is auto-generated server-side and not required from UI
      invitedEmail: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdAt: {},
      updatedAt: {},
      business: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
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
      // Don't send token from UI - it's auto-generated server-side
      const businessInvitationToSend = { ...this.businessInvitation };
      delete businessInvitationToSend.token;

      if (this.businessInvitation.id) {
        this.businessInvitationService()
          .update(businessInvitationToSend)
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
        // For new invitations, use the business-specific endpoint
        this.businessInvitationService()
          .createForBusiness(
            this.businessInvitation.business.id,
            this.businessInvitation.invitedEmail,
            this.businessInvitation.role
          )
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
