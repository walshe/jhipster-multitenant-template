import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import BusinessUserUpdate from './business-user-update.vue';
import BusinessUserService from './business-user.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import BusinessService from '@/entities/business/business.service';

import UserService from '@/entities/user/user.service';

type BusinessUserUpdateComponentType = InstanceType<typeof BusinessUserUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const businessUserSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<BusinessUserUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('BusinessUser Management Update Component', () => {
    let comp: BusinessUserUpdateComponentType;
    let businessUserServiceStub: SinonStubbedInstance<BusinessUserService>;

    beforeEach(() => {
      route = {};
      businessUserServiceStub = sinon.createStubInstance<BusinessUserService>(BusinessUserService);
      businessUserServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          businessUserService: () => businessUserServiceStub,
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
        const wrapper = shallowMount(BusinessUserUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(BusinessUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.businessUser = businessUserSample;
        businessUserServiceStub.update.resolves(businessUserSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(businessUserServiceStub.update.calledWith(businessUserSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        businessUserServiceStub.create.resolves(entity);
        const wrapper = shallowMount(BusinessUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.businessUser = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(businessUserServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        businessUserServiceStub.find.resolves(businessUserSample);
        businessUserServiceStub.retrieve.resolves([businessUserSample]);

        // WHEN
        route = {
          params: {
            businessUserId: `${businessUserSample.id}`,
          },
        };
        const wrapper = shallowMount(BusinessUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.businessUser).toMatchObject(businessUserSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        businessUserServiceStub.find.resolves(businessUserSample);
        const wrapper = shallowMount(BusinessUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
