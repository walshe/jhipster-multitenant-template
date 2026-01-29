# Tasks: BU2 - Enforce Business User Permissions

## Phase 1: Analysis and Discovery
1. Review all Business-related API endpoints and identify which ones need authorization checks
2. Document current behavior of each endpoint
3. Identify which operations should be restricted to business owners only
4. Create a matrix of endpoints vs required permissions

## Phase 2: Implementation
1. Add authorization utilities to check if a user is a business owner
2. Update BusinessResource to restrict modifying operations to business owners
3. Update BusinessUserResource to restrict operations based on business ownership
4. Update BusinessInvitationResource to restrict operations based on business ownership
5. Update service layer methods to include authorization checks
6. Add proper exception handling for unauthorized access

## Phase 3: Testing
1. Create unit tests for authorization logic
2. Test that business owners can perform all operations
3. Test that business members can only perform read operations
4. Test that unauthorized users cannot access any operations
5. Verify existing functionality still works for legitimate use cases

## Phase 4: Documentation
1. Update API documentation to reflect new authorization requirements
2. Add comments to code explaining authorization logic
3. Update any relevant README or developer documentation