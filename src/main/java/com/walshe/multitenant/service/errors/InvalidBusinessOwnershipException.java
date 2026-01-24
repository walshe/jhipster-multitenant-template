package com.walshe.multitenant.service.errors;

public class InvalidBusinessOwnershipException extends RuntimeException {

    public InvalidBusinessOwnershipException(String businessId, String userId) {
        super("Cannot assign owner who is not a member of the business. Business ID: " + businessId + ", User ID: " + userId);
    }
}