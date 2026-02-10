import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import BusinessInvitationService from './business-invitation.service';
import { useDateFormat } from '@/shared/composables';
import { type IBusinessInvitation } from '@/shared/model/business-invitation.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BusinessInvitationDetails',
  setup() {
    const dateFormat = useDateFormat();
    const businessInvitationService = inject('businessInvitationService', () => new BusinessInvitationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const businessInvitation: Ref<IBusinessInvitation> = ref({});

    const retrieveBusinessInvitation = async businessInvitationId => {
      try {
        const res = await businessInvitationService().find(businessInvitationId);
        businessInvitation.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.businessInvitationId) {
      retrieveBusinessInvitation(route.params.businessInvitationId);
    }

    const copyInvitationLink = () => {
      // Construct the invitation link using the token - now points to preview route
      const invitationLink = `${window.location.origin}/invite/${businessInvitation.value.token}`;
      
      // Use the Clipboard API to copy the link
      navigator.clipboard.writeText(invitationLink).then(() => {
        alertService.showSuccess(t$('multitenantApp.businessInvitation.linkCopied'));
      }).catch(err => {
        console.error('Failed to copy: ', err);
        alertService.showError(t$('multitenantApp.businessInvitation.linkCopyFailed'));
      });
    };

    return {
      ...dateFormat,
      alertService,
      businessInvitation,
      copyInvitationLink,
      previousState,
      t$: useI18n().t,
    };
  },
});
