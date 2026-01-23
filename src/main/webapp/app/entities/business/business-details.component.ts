import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

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

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const business: Ref<IBusiness> = ref({});

    const retrieveBusiness = async businessId => {
      try {
        const res = await businessService().find(businessId);
        business.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.businessId) {
      retrieveBusiness(route.params.businessId);
    }

    return {
      ...dateFormat,
      alertService,
      business,

      previousState,
      t$: useI18n().t,
    };
  },
});
