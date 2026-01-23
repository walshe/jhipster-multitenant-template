import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import BusinessInvitationUpdate from './business-invitation-update.vue';
import BusinessInvitationService from './business-invitation.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import BusinessService from '@/entities/business/business.service';

import UserService from '@/entities/user/user.service';

type BusinessInvitationUpdateComponentType = InstanceType<typeof BusinessInvitationUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const businessInvitationSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<BusinessInvitationUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('BusinessInvitation Management Update Component', () => {
    let comp: BusinessInvitationUpdateComponentType;
    let businessInvitationServiceStub: SinonStubbedInstance<BusinessInvitationService>;

    beforeEach(() => {
      route = {};
      businessInvitationServiceStub = sinon.createStubInstance<BusinessInvitationService>(BusinessInvitationService);
      businessInvitationServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          businessInvitationService: () => businessInvitationServiceStub,
          businessService: () =>
            sinon.createStubInstance<BusinessService>(BusinessService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(BusinessInvitationUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(BusinessInvitationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.businessInvitation = businessInvitationSample;
        businessInvitationServiceStub.update.resolves(businessInvitationSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(businessInvitationServiceStub.update.calledWith(businessInvitationSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        businessInvitationServiceStub.create.resolves(entity);
        const wrapper = shallowMount(BusinessInvitationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.businessInvitation = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(businessInvitationServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        businessInvitationServiceStub.find.resolves(businessInvitationSample);
        businessInvitationServiceStub.retrieve.resolves([businessInvitationSample]);

        // WHEN
        route = {
          params: {
            businessInvitationId: `${businessInvitationSample.id}`,
          },
        };
        const wrapper = shallowMount(BusinessInvitationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.businessInvitation).toMatchObject(businessInvitationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        businessInvitationServiceStub.find.resolves(businessInvitationSample);
        const wrapper = shallowMount(BusinessInvitationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
