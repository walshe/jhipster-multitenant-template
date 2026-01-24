# Tasks: BV1 - Validate Business Ownership in Update Operations

## Phase 1: Preparation
1. Create the changes directory structure
2. Review current implementation of BusinessResource PUT and PATCH methods
3. Understand how BusinessUser relationships are stored and queried
4. Identify the appropriate validation points in the service layer

## Phase 2: Implementation
1. Update BusinessService to add validation for owner assignment
2. Modify BusinessResource PUT method to validate owner assignment
3. Modify BusinessResource PATCH method to validate owner assignment
4. Add necessary imports for BusinessUserRepository
5. Create custom exception for invalid business ownership assignment

## Phase 3: Validation
1. Create/update unit tests to verify the new validation behavior
2. Test that legitimate owner assignments still work
3. Test that invalid owner assignments return appropriate errors
4. Verify that other business fields can still be updated normally

## Phase 4: Documentation
1. Update API documentation to reflect the new validation requirements
2. Add comments to code explaining the business ownership validation
3. Update any relevant README or developer documentation