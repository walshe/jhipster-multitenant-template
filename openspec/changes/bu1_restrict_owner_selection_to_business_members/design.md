# Design: BU1 - Restrict Owner Selection to Business Members

## Architecture Overview
The solution involves creating a new secure API endpoint that returns only users associated with a specific business, and updating the UI to use this endpoint for populating the owner dropdown during business editing.

## Current Flow
```
Business Edit Form
    ↓
Loads all users via existing user API
    ↓
Shows all users in owner dropdown
```

## Proposed Flow
```
Business Edit Form
    ↓
Calls new secure API: GET /api/businesses/{id}/members
    ↓
Authorization checks: Is user an owner of this business?
    ↓
Returns only users associated with this business via BusinessUser relationship
    ↓
Populates owner dropdown with only business members
```

## Key Design Decisions

### 1. New API Endpoint
- Create `GET /api/businesses/{id}/members` endpoint
- Only accessible to authenticated users who are owners of the specified business
- Returns list of users associated with the business through BusinessUser relationship

### 2. Authorization Logic
- Check if the authenticated user is an owner of the requested business
- Return 403 Forbidden if user is not authorized
- Use existing Spring Security mechanisms for authentication

### 3. Data Retrieval
- Query BusinessUser table to find all users associated with the business
- Return only the user information needed for the dropdown
- Optimize query to avoid N+1 problems

## Implementation Approach

### Backend Changes
1. **New Service Method**: Create method in BusinessService to retrieve business members
2. **New REST Endpoint**: Add endpoint in BusinessResource with proper security annotations
3. **Authorization Check**: Implement logic to verify user is business owner
4. **DTO**: Create appropriate DTO for returning user information

### Frontend Changes
1. **API Call**: Update business-edit component to call new endpoint
2. **Conditional Loading**: Only load members when editing existing business
3. **Dropdown Update**: Populate owner dropdown with business members only
4. **Error Handling**: Handle cases where user lacks permissions

## Security Considerations
- The endpoint should only be accessible to authenticated users
- Additional check to ensure user is an owner of the requested business
- Prevent unauthorized access to business member information
- Follow existing security patterns in the application

## Performance Considerations
- Optimize database queries to fetch business members efficiently
- Consider caching for frequently accessed business member lists
- Minimize data transferred in API responses

## Error Handling
- Return appropriate HTTP codes (403 for unauthorized access)
- Provide clear error messages for UI display
- Graceful degradation if API call fails