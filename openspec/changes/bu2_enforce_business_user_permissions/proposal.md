# Change Proposal: BU2 - Enforce Business User Permissions

## Summary
Review all Business-related APIs and implement authorization logic to ensure that any BusinessUsers of a business who are not the Business.OWNER can only perform non-modifying actions. This will enhance security by preventing unauthorized modifications to business resources by non-owners.

## Problem Statement
Currently, the application allows business members (non-owners) to potentially perform modifying operations on business resources. This creates a security vulnerability where users who are only members of a business could modify business properties or related entities they shouldn't have access to change.

## Solution Overview
1. Review all existing Business-related endpoints (Business, BusinessUser, BusinessInvitation)
2. Implement authorization checks to differentiate between business owners and business members
3. Restrict modifying operations (PUT, PATCH, DELETE) to business owners only
4. Allow non-modifying operations (GET) to business members
5. Update service layer with proper authorization checks

## Expected Benefits
- Enhanced security by preventing unauthorized business modifications
- Clear distinction between business owner and member permissions
- Better compliance with multitenant security requirements
- Reduced risk of data integrity issues

## Risks & Mitigations
- Risk: Breaking existing functionality for business members
  - Mitigation: Ensure GET operations remain accessible to business members
- Risk: Performance impact from additional authorization checks
  - Mitigation: Optimize queries and use caching where appropriate