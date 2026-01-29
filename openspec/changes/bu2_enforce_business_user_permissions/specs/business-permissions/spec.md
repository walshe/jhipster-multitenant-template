# Spec Deltas: BU2 - Enforce Business User Permissions

## ADDED Requirements

### Business Owner Authorization for Modifying Operations
- Requirement: The system shall restrict modifying operations (PUT, PATCH, DELETE) on business resources to business owners only
- Scenario: A user who is a member of a business but not the owner attempts to update the business via PUT /api/businesses/{id} and receives a 403 Forbidden response

### Business Member Read Access
- Requirement: The system shall allow business members to perform read operations (GET) on their business resources
- Scenario: A user who is a member of a business but not the owner can access GET /api/businesses/{id} to view the business details

### Business User Management Authorization
- Requirement: The system shall restrict modifying operations (PUT, PATCH, DELETE) on business-user relationships to business owners only
- Scenario: A business member attempts to update a business-user relationship via PUT /api/business-users/{id} and receives a 403 Forbidden response

### Business Invitation Management Authorization
- Requirement: The system shall restrict modifying operations (PUT, PATCH, DELETE) on business invitations to business owners only
- Scenario: A business member attempts to update a business invitation via PUT /api/business-invitations/{id} and receives a 403 Forbidden response

### Unauthorized Access Error Response
- Requirement: The system shall return HTTP 403 Forbidden with appropriate error message when a user attempts to perform an operation they are not authorized to perform
- Scenario: A business member attempts to delete a business via DELETE /api/businesses/{id} and receives a 403 Forbidden response with an error message indicating insufficient permissions

## MODIFIED Requirements

### Business Resource Access Control
- Requirement: Access to business resources shall be controlled based on the user's relationship to the business
- Previous: Most business endpoints were accessible to any authenticated user
- Updated: Business modifying operations are restricted to business owners, while read operations are available to business members

### Business User Resource Access Control
- Requirement: Access to business-user resources shall be controlled based on business ownership
- Previous: Business-user endpoints had minimal authorization checks
- Updated: Only business owners can modify business-user relationships

### Business Invitation Resource Access Control
- Requirement: Access to business invitation resources shall be controlled based on business ownership
- Previous: Business invitation endpoints had minimal authorization checks
- Updated: Only business owners can modify business invitations

## REMOVED Requirements

### Universal Access to Business Modifying Operations
- Requirement: Any authenticated user could perform modifying operations on any business if they knew the ID
- Rationale: This requirement was removed to implement proper business-level authorization and improve security