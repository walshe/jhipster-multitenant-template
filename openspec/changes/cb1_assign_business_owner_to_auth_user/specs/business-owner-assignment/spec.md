# Spec Deltas: CB1 - Assign Business Owner to Authenticated User

## ADDED Requirements

### Business Creation with Automatic Owner Assignment
- Requirement: When an authenticated user creates a business via POST /api/businesses, the system shall automatically assign the authenticated user as the business owner
- Scenario: A user with valid authentication credentials sends a POST request to /api/businesses with business details but without specifying an owner, and the system creates the business with the authenticated user as the owner

### Ignore Owner Field in Create Request
- Requirement: When processing a business creation request, the system shall ignore any owner information provided in the request body
- Scenario: A user sends a POST request to /api/businesses with business details including an owner field, and the system creates the business with the authenticated user as the owner regardless of the provided owner field

## MODIFIED Requirements

### Business Creation Endpoint Authorization
- Requirement: The POST /api/businesses endpoint shall be accessible to any authenticated user
- Previous: The endpoint accepted owner information from the request body
- Updated: The endpoint assigns the authenticated user as the owner regardless of request body content
- Scenario: An authenticated user accesses POST /api/businesses with valid business data and the business gets created with the authenticated user as the owner

## REMOVED Requirements

### Explicit Owner Specification in Business Creation
- Requirement: Clients must specify the owner when creating a business via POST /api/businesses
- Rationale: This requirement is removed as the system now automatically assigns the authenticated user as the owner