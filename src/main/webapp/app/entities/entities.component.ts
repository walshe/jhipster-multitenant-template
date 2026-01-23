import { defineComponent, provide } from 'vue';

import BusinessService from './business/business.service';
import BusinessUserService from './business-user/business-user.service';
import BusinessInvitationService from './business-invitation/business-invitation.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('businessService', () => new BusinessService());
    provide('businessUserService', () => new BusinessUserService());
    provide('businessInvitationService', () => new BusinessInvitationService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
