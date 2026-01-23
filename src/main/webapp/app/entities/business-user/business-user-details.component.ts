import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import BusinessUserService from './business-user.service';
import { useDateFormat } from '@/shared/composables';
import { type IBusinessUser } from '@/shared/model/business-user.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BusinessUserDetails',
  setup() {
    const dateFormat = useDateFormat();
    const businessUserService = inject('businessUserService', () => new BusinessUserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const businessUser: Ref<IBusinessUser> = ref({});

    const retrieveBusinessUser = async businessUserId => {
      try {
        const res = await businessUserService().find(businessUserId);
        businessUser.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.businessUserId) {
      retrieveBusinessUser(route.params.businessUserId);
    }

    return {
      ...dateFormat,
      alertService,
      businessUser,

      previousState,
      t$: useI18n().t,
    };
  },
});
