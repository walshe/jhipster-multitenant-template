# Design: BV1 - Validate Business Ownership in Update Operations

## Architecture Overview
The change involves adding validation to business update operations to ensure that when a business owner is assigned, the user being assigned is actually a member of the business. This affects two main layers:
1. REST Controller Layer: BusinessResource
2. Service Layer: BusinessService

## Current Flow
```
PUT/PATCH /api/businesses/{id}
    ↓
BusinessResource.updateBusiness/partialUpdateBusiness(BusinessDTO)
    ↓
BusinessService.update/partialUpdate(BusinessDTO)
    ↓
BusinessMapper.toEntity(BusinessDTO)
    ↓
BusinessRepository.save(Business)
```

## Proposed Flow
```
PUT/PATCH /api/businesses/{id}
    ↓
BusinessResource.updateBusiness/partialUpdateBusiness(BusinessDTO)
    ↓
BusinessService.update/partialUpdate(BusinessDTO) ← Validate owner assignment
    ↓
BusinessMapper.toEntity(BusinessDTO)
    ↓
BusinessRepository.save(Business)
```

## Key Design Decisions

### 1. Validation Location
- Perform validation in the BusinessService layer to maintain separation of concerns
- This allows the validation logic to be reused across both PUT and PATCH operations
- The service layer has access to both BusinessRepository and BusinessUserRepository

### 2. BusinessUser Relationship Check
- Query the BusinessUserRepository to check if a relationship exists between the business and the user being assigned as owner
- Only validate when the owner field is being changed in the update
- Allow other business fields to be updated without this validation

### 3. Error Handling
- Create a custom exception for invalid business ownership assignments
- Return HTTP 400 Bad Request when validation fails
- Provide clear error messaging to indicate why the request failed

## Implementation Approach

### Step 1: Create Custom Exception
```java
public class InvalidBusinessOwnershipException extends BadRequestAlertException {
    public InvalidBusinessOwnershipException(String businessId, String userId) {
        super("Cannot assign owner who is not a member of the business", "business", "invalidowner");
    }
}
```

### Step 2: Inject BusinessUserRepository in BusinessService
- Add BusinessUserRepository dependency to BusinessService
- Use it to check if the user exists as a member of the business

### Step 3: Add Validation Logic
- In update methods, check if the owner field is being changed
- If so, verify that the user exists in the BusinessUser table for this business
- Throw exception if validation fails

## Data Flow Modifications

### Before
```java
public BusinessDTO update(BusinessDTO businessDTO) {
    Business business = businessMapper.toEntity(businessDTO);
    // For updates, preserve the original owner
    Business existingBusiness = businessRepository.findById(business.getId()).orElse(null);
    if (existingBusiness != null) {
        business.setOwner(existingBusiness.getOwner());
    }
    business = businessRepository.save(business);
    return businessMapper.toDto(business);
}
```

### After
```java
public BusinessDTO update(BusinessDTO businessDTO) {
    // Check if owner is being changed and validate ownership
    Business existingBusiness = businessRepository.findById(businessDTO.getId()).orElse(null);
    if (existingBusiness != null && businessDTO.getOwner() != null && 
        !Objects.equals(existingBusiness.getOwner().getId(), businessDTO.getOwner().getId())) {
        validateBusinessOwnership(businessDTO.getId(), businessDTO.getOwner().getId());
    }
    
    Business business = businessMapper.toEntity(businessDTO);
    // For updates, preserve the original owner unless validated
    if (existingBusiness != null) {
        business.setOwner(existingBusiness.getOwner());
    }
    business = businessRepository.save(business);
    return businessMapper.toDto(business);
}
```

## Security Considerations
- The validation ensures that only users who are members of a business can be assigned as its owner
- This prevents unauthorized elevation of privileges
- The validation only applies when the owner field is being modified
- Other business fields can still be updated normally

## Performance Considerations
- Additional database query to check BusinessUser relationship
- Should be minimal impact as it's only performed when owner field changes
- Could be optimized with caching if needed in the future