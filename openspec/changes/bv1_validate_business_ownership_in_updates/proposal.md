# Change Proposal: BV1 - Validate Business Ownership in Update Operations

## Summary
Modify the business PUT and PATCH endpoints to return an appropriate error if the assigned owner is not belonging to the Business (i.e., there is not a BusinessUser found relating to the user and the business in question).

## Problem Statement
Currently, the business PUT and PATCH endpoints allow updating business information without validating that the user specified as the owner actually belongs to the business. This creates a security vulnerability where users could potentially assign ownership of a business to someone who is not a member of that business.

## Solution Overview
- Add validation to the PUT and PATCH endpoints to check if the user being assigned as owner is a member of the business
- Query the BusinessUser repository to verify that a relationship exists between the user and the business
- Return an appropriate error response if the validation fails
- Maintain backward compatibility for legitimate owner assignments

## Expected Benefits
- Enhanced security by preventing unauthorized assignment of business ownership
- Data integrity protection by ensuring business owners are valid members
- Clear error messaging for invalid owner assignments
- Consistent authorization enforcement across business operations

## Risks & Mitigations
- Risk: Performance impact from additional database queries
  - Mitigation: Optimize queries and consider caching for frequently accessed relationships
- Risk: Breaking existing functionality if validation is too restrictive
  - Mitigation: Ensure validation only applies to owner changes and preserves existing behavior for other fields