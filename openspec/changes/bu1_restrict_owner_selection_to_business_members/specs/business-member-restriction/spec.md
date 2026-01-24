# Spec Deltas: BU1 - Restrict Owner Selection to Business Members

## ADDED Requirements

### Secure Business Members API Endpoint
- Requirement: The system shall provide a secure API endpoint `GET /api/businesses/{id}/members` that returns only users associated with the specified business
- Scenario: An authenticated user who is an owner of business with ID 123 makes a request to `/api/businesses/123/members` and receives a list of users who are members of that business

### Business Owner Authorization for Member Access
- Requirement: The system shall only allow authenticated users who are owners of a business to access the list of members for that business
- Scenario: User A is an owner of business 123, so they can access `/api/businesses/123/members`, but cannot access `/api/businesses/456/members` if they are not an owner of business 456

### Filtered Owner Dropdown in Business Edit UI
- Requirement: The business edit form shall only display users who are members of the business in the owner selection dropdown
- Scenario: When editing business 123, the owner dropdown only shows users who are members of business 123 as returned by the secure API endpoint

### Permission Denied Response
- Requirement: The system shall return HTTP 403 Forbidden when a user attempts to access business members without proper authorization
- Scenario: User A attempts to access `/api/businesses/456/members` but is not an owner of business 456, resulting in a 403 Forbidden response

## MODIFIED Requirements

### Business Edit Form Behavior
- Requirement: The business edit form shall load business members via the new secure API endpoint instead of loading all users
- Previous: The form loaded all users in the system for the owner dropdown
- Updated: The form now loads only members of the specific business for the owner dropdown

### Business User Association
- Requirement: When updating a business owner, the system shall validate that the selected user is a member of the business
- Previous: The system only validated this during the update operation
- Updated: The UI now prevents selection of non-members by only showing valid members in the dropdown

## REMOVED Requirements

### All Users in Owner Dropdown
- Requirement: The business edit form displayed all users in the system in the owner selection dropdown
- Rationale: This requirement is removed to enhance security by limiting owner assignment to business members only