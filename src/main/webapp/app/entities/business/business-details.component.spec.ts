import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import BusinessDetails from './business-details.vue';
import BusinessService from './business.service';
import AlertService from '@/shared/alert/alert.service';

type BusinessDetailsComponentType = InstanceType<typeof BusinessDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const businessSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Business Management Detail Component', () => {
    let businessServiceStub: SinonStubbedInstance<BusinessService>;
    let mountOptions: MountingOptions<BusinessDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      businessServiceStub = sinon.createStubInstance<BusinessService>(BusinessService);

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
          businessService: () => businessServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        businessServiceStub.find.resolves(businessSample);
        route = {
          params: {
            businessId: `${123}`,
          },
        };
        const wrapper = shallowMount(BusinessDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.business).toMatchObject(businessSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        businessServiceStub.find.resolves(businessSample);
        const wrapper = shallowMount(BusinessDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
