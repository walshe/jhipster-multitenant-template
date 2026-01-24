# Design: CB1 - Assign Business Owner to Authenticated User

## Architecture Overview
The change involves modifying the business creation flow to automatically assign the authenticated user as the business owner. This affects three main layers:
1. REST Controller Layer: BusinessResource
2. Service Layer: BusinessService
3. Mapping Layer: BusinessMapper

## Current Flow
```
POST /api/businesses
    ↓
BusinessResource.createBusiness(BusinessDTO)
    ↓
BusinessService.save(BusinessDTO)
    ↓
BusinessMapper.toEntity(BusinessDTO)
    ↓
BusinessRepository.save(Business)
```

## Proposed Flow
```
POST /api/businesses
    ↓
BusinessResource.createBusiness(BusinessDTO) ← Extract authenticated user
    ↓
BusinessService.save(BusinessDTO, currentUser) ← Set owner to authenticated user
    ↓
BusinessMapper.toEntity(BusinessDTO) ← Owner already set in service
    ↓
BusinessRepository.save(Business)
```

## Key Design Decisions

### 1. Security Context Access
- Use `SecurityUtils.getCurrentUserId()` to obtain the ID of the authenticated user
- This avoids potential issues with lazy loading if we tried to fetch the full User object
- The User entity will be loaded by JPA when needed

### 2. Service Layer Modification
- Modify the `BusinessService.save()` method to accept the authenticated user
- Alternatively, call `SecurityUtils` directly from the service layer
- The second approach is cleaner as it keeps security concerns in the service layer

### 3. DTO vs Entity Handling
- The `BusinessDTO` will still have an `owner` field for API compatibility
- However, when creating a business, the service layer will override this field
- For updates, the owner field will remain as is (not modifiable through this endpoint)

### 4. Error Handling
- If no authenticated user is found, throw an appropriate exception
- This should theoretically never happen due to security configuration requiring authentication

## Implementation Approach

### Option 1: Modify Service Method Signature
```java
public BusinessDTO save(BusinessDTO businessDTO, Long ownerId) {
    // Implementation
}
```

### Option 2: Access Security Context in Service
```java
public BusinessDTO save(BusinessDTO businessDTO) {
    Long ownerId = SecurityUtils.getCurrentUserId()
        .orElseThrow(() -> new IllegalStateException("No authenticated user"));
    // Set owner and continue with save
}
```

Option 2 is preferred as it:
- Maintains backward compatibility with existing service method signatures
- Keeps security context access within the service layer
- Reduces coupling between controller and security implementation

## Data Flow Modifications

### Before
```java
@PostMapping("")
public ResponseEntity<BusinessDTO> createBusiness(@Valid @RequestBody BusinessDTO businessDTO) {
    // businessDTO contains owner from request body
    businessDTO = businessService.save(businessDTO);
    // ...
}
```

### After
```java
@PostMapping("")
public ResponseEntity<BusinessDTO> createBusiness(@Valid @RequestBody BusinessDTO businessDTO) {
    // businessDTO owner will be overridden by service
    businessDTO = businessService.save(businessDTO);
    // ...
}
```

## Security Considerations
- The `/api/businesses` endpoint is already secured and requires authentication
- Only authenticated users can create businesses
- The owner field in the request body will be ignored during creation
- During business updates, the owner field should remain protected and not be modifiable by non-admin users