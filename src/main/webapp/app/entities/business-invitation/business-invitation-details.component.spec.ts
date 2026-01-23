import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import BusinessInvitationDetails from './business-invitation-details.vue';
import BusinessInvitationService from './business-invitation.service';
import AlertService from '@/shared/alert/alert.service';

type BusinessInvitationDetailsComponentType = InstanceType<typeof BusinessInvitationDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const businessInvitationSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('BusinessInvitation Management Detail Component', () => {
    let businessInvitationServiceStub: SinonStubbedInstance<BusinessInvitationService>;
    let mountOptions: MountingOptions<BusinessInvitationDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      businessInvitationServiceStub = sinon.createStubInstance<BusinessInvitationService>(BusinessInvitationService);

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
          businessInvitationService: () => businessInvitationServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        businessInvitationServiceStub.find.resolves(businessInvitationSample);
        route = {
          params: {
            businessInvitationId: `${123}`,
          },
        };
        const wrapper = shallowMount(BusinessInvitationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.businessInvitation).toMatchObject(businessInvitationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        businessInvitationServiceStub.find.resolves(businessInvitationSample);
        const wrapper = shallowMount(BusinessInvitationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
