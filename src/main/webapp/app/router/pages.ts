// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here
import BusinessInvitationPreview from '@/entities/business-invitation/business-invitation-preview.vue';

export default [
  {
    path: '/invite/:token',
    name: 'BusinessInvitationPreview',
    component: BusinessInvitationPreview,
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
