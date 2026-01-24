# Spec Deltas: BV1 - Validate Business Ownership in Update Operations

## ADDED Requirements

### Business Owner Validation
- Requirement: When updating a business via PUT /api/businesses/{id} or PATCH /api/businesses/{id}, if the owner field is being changed, the system shall validate that the user being assigned as owner is a member of the business (has a BusinessUser record for this business)
- Scenario: A user attempts to update a business and change the owner to a user who is not a member of the business, and the system returns an appropriate error response

### Invalid Business Ownership Error Response
- Requirement: When a business update request attempts to assign an owner who is not a member of the business, the system shall return HTTP 400 Bad Request with a clear error message
- Scenario: A PUT or PATCH request to update a business includes an owner field with a user ID that does not have a corresponding BusinessUser record for the business, and the system responds with an error

## MODIFIED Requirements

### Business Update Authorization
- Requirement: The PUT /api/businesses/{id} and PATCH /api/businesses/{id} endpoints shall validate business ownership relationships when the owner field is being modified
- Previous: The endpoints only checked if the business existed
- Updated: The endpoints now also validate that the user being assigned as owner is a member of the business

## REMOVED Requirements

### Unrestricted Owner Assignment in Updates
- Requirement: Business update operations allowed assigning any user as owner without validation
- Rationale: This requirement is removed to enhance security by validating business membership before allowing owner assignment