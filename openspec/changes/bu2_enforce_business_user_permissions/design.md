# Design: BU2 - Enforce Business User Permissions

## Architecture Overview
The solution involves implementing fine-grained authorization at the business level to ensure that only business owners can perform modifying operations on business resources, while business members can only perform read operations.

## Current State Analysis

### BusinessResource Endpoints
- POST /api/businesses - Create business (currently assigns owner to authenticated user)
- GET /api/businesses - Get all businesses (accessible to all authenticated users)
- GET /api/businesses/{id} - Get specific business (accessible to all authenticated users)
- PUT /api/businesses/{id} - Update business (currently has some validation)
- PATCH /api/businesses/{id} - Partial update business (currently has some validation)
- DELETE /api/businesses/{id} - Delete business (accessible to all authenticated users)
- GET /api/businesses/{id}/members - Get business members (restricted to business owner/admin)

### BusinessUserResource Endpoints
- POST /api/business-users - Create business-user relationship
- GET /api/business-users - Get all business-user relationships
- GET /api/business-users/{id} - Get specific business-user relationship
- PUT /api/business-users/{id} - Update business-user relationship
- PATCH /api/business-users/{id} - Partial update business-user relationship
- DELETE /api/business-users/{id} - Delete business-user relationship

### BusinessInvitationResource Endpoints
- POST /api/business-invitations - Create business invitation
- GET /api/business-invitations - Get all business invitations
- GET /api/business-invitations/{id} - Get specific business invitation
- PUT /api/business-invitations/{id} - Update business invitation
- PATCH /api/business-invitations/{id} - Partial update business invitation
- DELETE /api/business-invitations/{id} - Delete business invitation

## Proposed Authorization Model

### Business Resource Authorization
- GET /api/businesses - All authenticated users
- GET /api/businesses/{id} - Business members and above
- POST /api/businesses - All authenticated users (creates business owned by authenticated user)
- PUT /api/businesses/{id} - Business owner only
- PATCH /api/businesses/{id} - Business owner only
- DELETE /api/businesses/{id} - Business owner only
- GET /api/businesses/{id}/members - Business owner and admin only

### BusinessUser Resource Authorization
- GET /api/business-users - Business members and above
- GET /api/business-users/{id} - Business members and above
- POST /api/business-users - Business owner only
- PUT /api/business-users/{id} - Business owner only
- PATCH /api/business-users/{id} - Business owner only
- DELETE /api/business-users/{id} - Business owner only

### BusinessInvitation Resource Authorization
- GET /api/business-invitations - Business members and above
- GET /api/business-invitations/{id} - Business members and above
- POST /api/business-invitations - Business owner only
- PUT /api/business-invitations/{id} - Business owner only
- PATCH /api/business-invitations/{id} - Business owner only
- DELETE /api/business-invitations/{id} - Business owner only

## Implementation Approach

### 1. Authorization Utilities
Create utility methods to check business ownership and membership:
- `isBusinessOwner(userId, businessId)` - Check if user owns the business
- `isBusinessMember(userId, businessId)` - Check if user is a member of the business

### 2. Service Layer Changes
Add authorization checks in service methods:
- Create methods to verify user permissions before performing operations
- Implement proper exception handling for unauthorized access

### 3. REST Layer Changes
- Add authorization checks in REST controllers before calling service methods
- Return appropriate HTTP status codes (403 Forbidden) for unauthorized access

## Security Considerations
- Implement proper authentication checks in all endpoints
- Ensure authorization is checked before any data access or modification
- Use existing Spring Security infrastructure
- Follow the principle of least privilege

## Error Handling
- Return 403 Forbidden for unauthorized access attempts
- Provide clear error messages without exposing sensitive information
- Log security-related events for monitoring

## Performance Considerations
- Optimize database queries for authorization checks
- Consider caching business membership information
- Minimize additional database calls where possible