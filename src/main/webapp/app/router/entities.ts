import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Business = () => import('@/entities/business/business.vue');
const BusinessUpdate = () => import('@/entities/business/business-update.vue');
const BusinessDetails = () => import('@/entities/business/business-details.vue');

const BusinessUser = () => import('@/entities/business-user/business-user.vue');
const BusinessUserDetails = () => import('@/entities/business-user/business-user-details.vue');

const BusinessInvitation = () => import('@/entities/business-invitation/business-invitation.vue');
const BusinessInvitationUpdate = () => import('@/entities/business-invitation/business-invitation-update.vue');
const BusinessInvitationDetails = () => import('@/entities/business-invitation/business-invitation-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'business',
      name: 'Business',
      component: Business,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business/new',
      name: 'BusinessCreate',
      component: BusinessUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business/:businessId/edit',
      name: 'BusinessEdit',
      component: BusinessUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business/:businessId/view',
      name: 'BusinessView',
      component: BusinessDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business-user',
      name: 'BusinessUser',
      component: BusinessUser,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business-user/:businessUserId/view',
      name: 'BusinessUserView',
      component: BusinessUserDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business-invitation',
      name: 'BusinessInvitation',
      component: BusinessInvitation,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business-invitation/new',
      name: 'BusinessInvitationCreate',
      component: BusinessInvitationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business-invitation/:businessInvitationId/edit',
      name: 'BusinessInvitationEdit',
      component: BusinessInvitationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'business-invitation/:businessInvitationId/view',
      name: 'BusinessInvitationView',
      component: BusinessInvitationDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
