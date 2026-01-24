# Tasks: BU1 - Restrict Owner Selection to Business Members

## Phase 1: Analysis and Planning
1. Review current business edit UI implementation
2. Analyze existing BusinessUser relationships and how they connect users to businesses
3. Examine current authentication and authorization mechanisms
4. Identify the best approach for the new secure API endpoint

## Phase 2: Backend Implementation
1. Create new service method to retrieve users associated with a specific business
2. Implement new REST endpoint `/api/businesses/{id}/members` with proper security
3. Add authorization logic to ensure only business owners can access the endpoint
4. Write unit tests for the new service and endpoint

## Phase 3: Frontend Implementation
1. Update business edit component to call the new API endpoint
2. Modify the UI to populate the owner dropdown with only business members
3. Handle error cases when users don't have proper permissions
4. Update component tests to reflect the new behavior

## Phase 4: Validation and Testing
1. Test that only business owners can access the member list endpoint
2. Verify that the dropdown shows only relevant users in the edit form
3. Ensure create flow still works without showing the owner field
4. Test error handling when users lack proper permissions
5. Run full test suite to ensure no regressions