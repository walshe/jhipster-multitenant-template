import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import BusinessUserDetails from './business-user-details.vue';
import BusinessUserService from './business-user.service';
import AlertService from '@/shared/alert/alert.service';

type BusinessUserDetailsComponentType = InstanceType<typeof BusinessUserDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const businessUserSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('BusinessUser Management Detail Component', () => {
    let businessUserServiceStub: SinonStubbedInstance<BusinessUserService>;
    let mountOptions: MountingOptions<BusinessUserDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      businessUserServiceStub = sinon.createStubInstance<BusinessUserService>(BusinessUserService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          businessUserService: () => businessUserServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        businessUserServiceStub.find.resolves(businessUserSample);
        route = {
          params: {
            businessUserId: `${123}`,
          },
        };
        const wrapper = shallowMount(BusinessUserDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.businessUser).toMatchObject(businessUserSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        businessUserServiceStub.find.resolves(businessUserSample);
        const wrapper = shallowMount(BusinessUserDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
