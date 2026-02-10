import { type Ref, computed, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useDateFormat } from '@/shared/composables';
import BusinessInvitationService from './business-invitation.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useAccountStore } from '@/shared/config/store/account-store';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BusinessInvitationPreview',
  setup() {
    const { t: t$ } = useI18n();
    const route = useRoute();
    const router = useRouter();
    const businessInvitationService = inject('businessInvitationService', () => new BusinessInvitationService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const accountStore = useAccountStore();

    const businessInvitation: Ref<any> = ref(null);
    const loading: Ref<boolean> = ref(false);
    const error: Ref<boolean> = ref(false);
    const isAccepting: Ref<boolean> = ref(false);

    const token = computed(() => route.params.token as string);

    const isAuthenticated = computed(() => accountStore.authenticated);

    const loadInvitation = async () => {
      if (!token.value) {
        error.value = true;
        return;
      }

      loading.value = true;
      error.value = false;

      try {
        businessInvitation.value = await businessInvitationService().getByToken(token.value);
      } catch (err) {
        error.value = true;
        console.error('Error loading invitation:', err);
        alertService.showError(t$('multitenantApp.businessInvitation.preview.loadError'));
      } finally {
        loading.value = false;
      }
    };

    const acceptInvitation = async () => {
      if (!token.value || !businessInvitation.value) {
        return;
      }

      isAccepting.value = true;

      try {
        const result = await businessInvitationService().acceptInvitation(token.value);
        businessInvitation.value = result;
        alertService.showSuccess(t$('multitenantApp.businessInvitation.preview.acceptSuccess'));
        
        // Redirect to the business page after acceptance
        router.push(`/businesses/${businessInvitation.value.business.id}`);
      } catch (err) {
        console.error('Error accepting invitation:', err);
        alertService.showError(t$('multitenantApp.businessInvitation.preview.acceptError'));
      } finally {
        isAccepting.value = false;
      }
    };

    onMounted(() => {
      loadInvitation();
    });

    return {
      businessInvitation,
      loading,
      error,
      isAccepting,
      isAuthenticated,
      acceptInvitation,
      t$,
      ...useDateFormat({ entityRef: businessInvitation }),
    };
  },
});