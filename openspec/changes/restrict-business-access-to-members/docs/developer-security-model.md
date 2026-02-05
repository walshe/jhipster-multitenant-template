# Business Access Control Security Model

## Overview
This document describes the security model implemented to restrict business access to members only. Users can only view businesses they are members of, and only business owners can modify businesses.

## Architecture

### Components

#### BusinessSecurityUtil
A utility class that provides security checks for business-related operations:
- `isBusinessMember(Long businessId)`: Checks if the current user is a member of the specified business
- `isBusinessOwner(Long businessId)`: Checks if the current user is the owner of the specified business
- `isAuthenticated()`: Checks if the current user is authenticated

#### Security Annotations
The system uses Spring Security's `@PreAuthorize` annotations to enforce access controls at the method level:

- `@PreAuthorize("@businessSecurity.isBusinessMember(#id)")` - Allows access to business details only if the user is a member
- `@PreAuthorize("@businessSecurity.isBusinessOwner(#id)")` - Allows modification/deletion only if the user is the owner
- `@PreAuthorize("@businessSecurity.isBusinessOwner(#id) or hasAuthority('ROLE_ADMIN')")` - Allows certain operations for owners or admins

#### Business Authorization Service
The `BusinessAuthorizationService` contains the core logic for determining business membership and ownership:
- `isBusinessMember(Long businessId)`: Checks if the current user is a member of the specified business
- `isBusinessOwner(Long businessId)`: Checks if the current user is the owner of the specified business

## Implementation Details

### Business Membership
A user is considered a member of a business if:
1. They are the owner of the business (linked via the `owner` field in the Business entity)
2. They have a `BusinessUser` relationship with the business (many-to-many relationship)

### Business Ownership
A user is considered the owner of a business if they are linked as the owner in the Business entity (`owner` field).

### Filtering Logic
The `BusinessQueryService` implements filtering in its `createSpecification` method to ensure that:
- When retrieving lists of businesses, only businesses the user is a member of are returned
- This filtering happens at the database level using JPA Specifications

## API Endpoints Protection

| Endpoint | Method | Protection |
|----------|--------|------------|
| GET /api/businesses | GET | Returns only businesses user is member of |
| GET /api/businesses/{id} | GET | User must be member of the business |
| POST /api/businesses | POST | Creates business with current user as owner |
| PUT /api/businesses/{id} | PUT | User must be owner of the business |
| PATCH /api/businesses/{id} | PATCH | User must be owner of the business |
| DELETE /api/businesses/{id} | DELETE | User must be owner of the business or admin |
| GET /api/businesses/{id}/members | GET | User must be owner of the business or admin |

## Frontend Integration

### Business Listing
- The frontend automatically receives only businesses the user is a member of
- Edit/delete buttons are conditionally displayed based on ownership status

### Business Details
- Access to business details is restricted to members
- Edit button is conditionally displayed based on ownership status
- Proper error handling for unauthorized access attempts

## Testing

### Unit Tests
- `BusinessSecurityUtilTest.java` - Tests for security utility methods
- Service layer tests verify proper authorization checks

### Integration Tests
- `BusinessResourceSecurityIT.java` - Comprehensive integration tests covering:
  - Member access to business details
  - Owner modification rights
  - Non-member access restrictions
  - Non-owner modification restrictions

## Error Handling

Unauthorized access attempts return appropriate HTTP status codes:
- 403 Forbidden: When a user tries to access resources they don't have permission for
- 404 Not Found: When a non-member tries to access a business (to avoid revealing business existence)

## Migration Considerations

When implementing this security model:
1. Ensure all existing BusinessUser relationships are properly established
2. Verify that business owners are correctly assigned
3. Test that legitimate access patterns continue to work
4. Update any external integrations that rely on business access