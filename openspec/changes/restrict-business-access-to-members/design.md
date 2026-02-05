## Context

Currently, users can potentially access all businesses regardless of their membership status. We need to implement proper access controls so that users can only view businesses they are members of. Additionally, only business owners should be able to edit businesses or call modifying APIs.

## Goals / Non-Goals

Goals:
- Restrict business viewing to only businesses the user is a member of
- Allow only business owners to see/edit business details or call modifying APIs
- Implement proper security checks at both UI and API levels

Non-Goals:
- Changing the underlying business/user relationship model
- Modifying authentication mechanisms
- Implementing complex role-based access beyond owner/member distinction

## Decisions

1) Use Spring Security with custom authorization checks
- Implement method-level security with @PreAuthorize and @PostAuthorize
- Create custom security expressions to check business membership
- Use @Query annotations to filter data at the repository level

2) Frontend filtering
- Update frontend components to only display businesses the user has access to
- Hide edit buttons for non-owners
- Implement proper error handling when access is denied

3) API endpoint protection
- Secure business-related endpoints to check ownership for modification operations
- Return appropriate HTTP status codes (403, 404) when access is denied

## Risks / Trade-offs

- Performance impact from additional security checks
- Complexity in maintaining security rules across layers
- Potential confusion for users who expect to see all businesses

## Implementation Plan

- Update Business entity and repository with security filters
- Implement custom security methods/checks
- Update REST controllers with proper authorization
- Update frontend to respect access controls
- Add integration tests to verify security

## Open Questions

- Should unauthorized access attempts return 403 Forbidden or 404 Not Found?
- How should we handle bulk operations on businesses?