# Change Proposal: BU1 - Restrict Owner Selection to Business Members

## Summary
Modify the business edit UI flow to only show users that are related to the specific business in the owner field dropdown. This requires creating a new secure API endpoint that returns only users associated with the business, and ensuring this endpoint is only accessible by authenticated users who are owners of the business.

## Problem Statement
Currently, the business edit form shows all users in the owner selection dropdown, which allows:
1. Unauthorized assignment of business ownership to users not in the business
2. Potential security vulnerabilities where non-members could be assigned as owners
3. Confusion for users who see irrelevant users in the dropdown

## Solution Overview
1. Create a new secure API endpoint `/api/businesses/{id}/members` that returns only users associated with the specified business
2. The endpoint should only be accessible to authenticated users who are owners of the business
3. Update the business edit UI to use this new endpoint to populate the owner dropdown
4. Add proper authorization checks to ensure only business owners can access the member list

## Expected Benefits
- Enhanced security by preventing assignment of ownership to non-members
- Cleaner UI with only relevant users in the dropdown
- Proper authorization enforcement for business member access
- Improved user experience with filtered, relevant options

## Risks & Mitigations
- Risk: Performance impact from additional API calls
  - Mitigation: Optimize queries and consider caching for frequently accessed data
- Risk: Breaking existing functionality if authorization is too restrictive
  - Mitigation: Ensure proper role-based access controls that allow legitimate business owners