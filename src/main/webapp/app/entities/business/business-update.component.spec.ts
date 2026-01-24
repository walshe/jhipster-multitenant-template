import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import BusinessUpdate from './business-update.vue';
import BusinessService from './business.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type BusinessUpdateComponentType = InstanceType<typeof BusinessUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const businessSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<BusinessUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Business Management Update Component', () => {
    let comp: BusinessUpdateComponentType;
    let businessServiceStub: SinonStubbedInstance<BusinessService>;

    beforeEach(() => {
      route = {};
      businessServiceStub = sinon.createStubInstance<BusinessService>(BusinessService);
      businessServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));
      businessServiceStub.retrieveBusinessMembers.resolves({ data: [] }); // Mock the new method

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
          businessService: () => businessServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(BusinessUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(BusinessUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.business = businessSample;
        businessServiceStub.update.resolves(businessSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(businessServiceStub.update.calledWith(businessSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        businessServiceStub.create.resolves(entity);
        const wrapper = shallowMount(BusinessUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.business = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(businessServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        businessServiceStub.find.resolves(businessSample);
        businessServiceStub.retrieve.resolves([businessSample]);

        // WHEN
        route = {
          params: {
            businessId: `${businessSample.id}`,
          },
        };
        const wrapper = shallowMount(BusinessUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.business).toMatchObject(businessSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        businessServiceStub.find.resolves(businessSample);
        const wrapper = shallowMount(BusinessUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
