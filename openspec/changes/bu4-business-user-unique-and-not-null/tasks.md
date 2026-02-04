## 1. Preparation

- [ ] 1.1 Review current BusinessUser entity mapping for business and user relationships
- [ ] 1.2 Confirm DB column names for FKs (business_id, user_id) in business_user table
- [ ] 1.3 Check existing data for nulls or duplicates in business_user

## 2. Domain annotations

- [ ] 2.1 Annotate BusinessUser.business with @NotNull and @ManyToOne(optional = false); add @JoinColumn(nullable = false)
- [ ] 2.2 Annotate BusinessUser.user with @NotNull and @ManyToOne(optional = false); add @JoinColumn(nullable = false)

## 3. Liquibase migration

- [ ] 3.1 Add changeset to enforce NOT NULL on business_user.business_id and business_user.user_id
- [ ] 3.2 Add UNIQUE constraint on (business_id, user_id) with a stable name (e.g., uq_business_user_business_user)
- [ ] 3.3 Include preconditions to detect existing nulls/duplicates and fail or markRan accordingly
- [ ] 3.4 Include the new changeset in master.xml after prior BusinessUser changes

## 4. Tests

- [ ] 4.1 Add persistence test to verify NOT NULL constraints violate on save when missing associations
- [ ] 4.2 Add persistence test to verify duplicate (business, user) insert is rejected
- [ ] 4.3 Update any existing tests that create BusinessUser to always set both relationships

## 5. Manual verification

- [ ] 5.1 Run database migration on H2/MySQL and verify constraints applied (describe table / information_schema)
- [ ] 5.2 Attempt to insert duplicates via SQL to confirm UNIQUE blocks them
- [ ] 5.3 Attempt to insert null business_id/user_id to confirm NOT NULL blocks them

## 6. Rollback plan (if needed)

- [ ] 6.1 Document how to drop the unique constraint and relax not-null in a follow-up changeset if rollback is required
