## 1. Codebase Scan and Planning

- [ ] 1.1 Search backend for role usages and dead branches: grep for `BusinessRole.OWNER`, `BusinessRole.ADMIN`, `'OWNER'`, `'ADMIN'`
- [ ] 1.2 Search frontend for role usages and selector options: grep for `BusinessRole`, `OWNER`, `ADMIN`
- [ ] 1.3 Identify all files that need updates (enum, DTOs, services, forms, i18n, fake data, JDL)

## 2. Database Migration

- [ ] 2.1 Create Liquibase changeset file: `src/main/resources/config/liquibase/changelog/20260203T000000_business_role_member_only.xml`
- [ ] 2.2 In the changeset, normalize data: `UPDATE business_user SET role = 'MEMBER' WHERE role IS NULL OR role <> 'MEMBER';`
- [ ] 2.3 In the changeset, normalize invitations: `UPDATE business_invitation SET role = 'MEMBER' WHERE role IS NULL OR role <> 'MEMBER';`
- [ ] 2.4 Attempt to add DB-level CHECK constraints restricting role to 'MEMBER' on tables `business_user` and `business_invitation` (skip if unsupported)
- [ ] 2.5 Include the changeset in `src/main/resources/config/liquibase/master.xml` under the incremental changelog needle
- [ ] 2.6 Run migration locally for H2 (dev) and MySQL (prod via docker-compose) and verify updates

## 3. Backend Implementation

- [ ] 3.1 Update enum: `src/main/java/com/walshe/multitenant/domain/enumeration/BusinessRole.java` to only `MEMBER`
- [ ] 3.2 Ensure DTO validation still requires role: `BusinessUserDTO` and `BusinessInvitation` flows accept only MEMBER
- [ ] 3.3 Enforce server-side default to MEMBER on create if null in: `BusinessUserService` and `BusinessInvitationService` (or validation rejection if preferring strictness)
- [ ] 3.4 Remove any branching on ADMIN/OWNER role (authorization should use Business.owner or membership)
- [ ] 3.5 Regenerate/verify MapStruct mappers compile without OWNER/ADMIN
- [ ] 3.6 Verify REST controllers accept and return only MEMBER for role fields
- [ ] 3.7 Update any constants or filters that enumerate role values (criteria classes if applicable)

## 4. Frontend Implementation

- [ ] 4.1 Update TS enum: `src/main/webapp/app/shared/model/enumerations/business-role.model.ts` to only `MEMBER`
- [ ] 4.2 Update role selector logic: in `business-user-update.component.ts/.vue` ensure only MEMBER is displayed and selected by default for new entities
- [ ] 4.3 Update i18n: `src/main/webapp/i18n/en/businessRole.json` to remove OWNER/ADMIN and keep MEMBER
- [ ] 4.4 Verify any other components or services that use BusinessRole compile and behave correctly

## 5. Seed/Fake Data and JDL

- [ ] 5.1 Update fake data CSV: `src/main/resources/config/liquibase/fake-data/business_user.csv` to set all role values to MEMBER
- [ ] 5.2 Update fake data CSV: `src/main/resources/config/liquibase/fake-data/business_invitation.csv` to set all role values to MEMBER
- [ ] 5.3 Update JDL enum in `jhipster-model.jdl` to `enum BusinessRole { MEMBER }`
- [ ] 5.4 If JHipster is re-run for entities, ensure incremental changelogs are used and the enum change is reflected without reintroducing OWNER/ADMIN

## 6. API and Documentation

- [ ] 6.1 Regenerate/review OpenAPI docs (Swagger UI) and verify BusinessUser.role and BusinessInvitation.role enums contain only MEMBER
- [ ] 6.2 Update any external API documentation or client SDK generation scripts if applicable

## 7. Testing

- [ ] 7.1 Update backend unit/integration tests that reference OWNER/ADMIN to use MEMBER
- [ ] 7.2 Add tests to assert role validation rejects non-MEMBER values (400 Bad Request)
- [ ] 7.3 Update frontend unit/e2e tests to reflect single-option role selection and defaulting behavior
- [ ] 7.4 Run full test suite (backend and frontend) and ensure green build

## 8. Verification and QA

- [ ] 8.1 Manual QA: Create, update, delete BusinessUser; confirm role is MEMBER and only option shown
- [ ] 8.2 Manual QA: Create BusinessInvitation; confirm role is MEMBER and only option shown
- [ ] 8.3 DB verification: Inspect `business_user` and `business_invitation` tables to ensure only MEMBER stored
- [ ] 8.4 Authorization sanity checks: Ensure owner-only operations still rely on Business.owner and that membership checks function unchanged

## 9. Deployment

- [ ] 9.1 Apply Liquibase migration to target environment
- [ ] 9.2 Deploy backend application
- [ ] 9.3 Deploy frontend application
- [ ] 9.4 Post-deploy smoke tests (API role validation, UI forms, i18n rendering)

## 10. Rollback and Communication

- [ ] 10.1 Document rollback: revert enum changes, remove CHECK constraints, and redeploy; data remains MEMBER-compatible
- [ ] 10.2 Communicate breaking change to any API consumers that may have depended on OWNER/ADMIN values