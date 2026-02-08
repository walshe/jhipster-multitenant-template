## Implementation Instructions for Business Invitation Security Enhancement

### Step 1: Update BusinessInvitationResource
- Add `@PreAuthorize("@businessSecurity.isBusinessOwner(#businessId)")` to the POST `/businesses/{id}/invitations` endpoint
- Add `@PreAuthorize("@businessSecurity.isBusinessOwnerOrMember(#businessId)")` to the GET `/businesses/{id}/invitations` endpoint
- Add `@PreAuthorize("@businessInvitationSecurity.canModifyInvitation(#id)")` to the PUT `/business-invitations/{id}` endpoint
- Add `@PreAuthorize("@businessInvitationSecurity.canModifyInvitation(#id)")` to the PATCH `/business-invitations/{id}` endpoint
- Add `@PreAuthorize("@businessInvitationSecurity.canModifyInvitation(#id)")` to the DELETE `/business-invitations/{id}` endpoint

### Step 2: Update BusinessInvitationService
- Ensure service methods handle UUID IDs instead of Long IDs
- Update all methods to work with UUID type for entity IDs
- Maintain proper validation of business ownership before operations

### Step 3: Update BusinessInvitationCriteria
- Change ID filter from LongFilter to StringFilter to accommodate UUIDs in HTTP requests
- This allows proper handling of UUIDs in query parameters

### Step 4: Update Test Files
- Update BusinessInvitationTestSamples to use UUID instead of Long
- Update BusinessInvitationDTOTest to use UUID instead of Long
- Update BusinessInvitationResourceIT to use UUID and correct method names
- Add proper UUID import to test files

### Step 5: Verification
- Run all BusinessInvitation tests to ensure functionality is preserved
- Run all Business-related tests to ensure no regressions
- Verify that only business owners can perform invitation operations
- Verify that unauthorized access attempts are properly rejected