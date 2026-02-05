import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useAccountStore } from '@/shared/config/store/account-store';

import BusinessService from './business.service';
import { useDateFormat } from '@/shared/composables';
import { type IBusiness } from '@/shared/model/business.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BusinessDetails',
  setup() {
    const dateFormat = useDateFormat();
    const businessService = inject('businessService', () => new BusinessService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const accountStore = useAccountStore();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const business: Ref<IBusiness> = ref({});

    const retrieveBusiness = async businessId => {
      try {
        const res = await businessService().find(businessId);
        business.value = res;
      } catch (error) {
        if (error.response?.status === 403 || error.response?.status === 404) {
          // Handle authorization errors - business not accessible to user
          alertService.showError(t$('multitenantApp.error.forbidden').toString(), { variant: 'warning' });
          // Optionally redirect to business list page
          router.push({ name: 'Business' });
        } else {
          alertService.showHttpError(error.response);
        }
      }
    };

    // Function to check if the current user is the owner of the business
    const isBusinessOwner = (): boolean => {
      if (!accountStore.account || !business.value.owner) {
        return false;
      }
      return accountStore.account.id === business.value.owner.id;
    };

    if (route.params?.businessId) {
      retrieveBusiness(route.params.businessId);
    }

    return {
      ...dateFormat,
      alertService,
      business,
      isBusinessOwner,

      previousState,
      t$: useI18n().t,
    };
  },
});
