<template>
  <div>
    <b-dropdown-item to="/business">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.business')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/business-user">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.businessUser')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/business-invitation">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.businessInvitation')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
