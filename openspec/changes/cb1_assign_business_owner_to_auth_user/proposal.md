# Change Proposal: CB1 - Assign Business Owner to Authenticated User

## Summary
Modify the `createBusiness` POST endpoint to automatically assign the authenticated user as the owner of the newly created business, removing the need to explicitly pass the owner in the request body.

## Problem Statement
Currently, the `createBusiness` endpoint requires the client to pass the owner information in the request body. This creates potential security issues as:
1. Any authenticated user could potentially create a business with any user as the owner
2. The API doesn't enforce that the business owner must be the authenticated user creating the business
3. This violates the principle of least privilege and proper authorization

## Solution Overview
- Modify the `createBusiness` endpoint to automatically assign the currently authenticated user as the business owner
- Remove the need for clients to pass owner information in the request body
- Ensure proper authorization by enforcing that only authenticated users can create businesses
- Update the service layer to populate the owner field using the security context

## Expected Benefits
- Enhanced security by preventing unauthorized assignment of business ownership
- Simplified API contract - clients no longer need to pass owner information
- Consistent business logic where the creator of a business becomes its owner
- Reduced risk of data integrity issues related to business ownership

## Risks & Mitigations
- Risk: Existing clients passing owner information might break
  - Mitigation: Update API documentation and provide migration guidance
- Risk: Potential performance impact from additional security context lookup
  - Mitigation: Security context lookup is lightweight and cached by Spring Security