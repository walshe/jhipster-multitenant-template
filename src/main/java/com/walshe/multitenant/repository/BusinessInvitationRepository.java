package com.walshe.multitenant.repository;

import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.domain.enumeration.InvitationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BusinessInvitation entity.
 */
@Repository
public interface BusinessInvitationRepository extends JpaRepository<BusinessInvitation, Long>, JpaSpecificationExecutor<BusinessInvitation> {
    List<BusinessInvitation> findByBusinessId(Long businessId);

    Optional<BusinessInvitation> findByToken(String token);

    List<BusinessInvitation> findByInvitedEmailAndBusinessId(String invitedEmail, Long businessId);

    @Query("SELECT bi FROM BusinessInvitation bi WHERE bi.businessId = :businessId AND bi.status = :status")
    Page<BusinessInvitation> findByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("status") InvitationStatus status, Pageable pageable);
    
    @Query("SELECT bi FROM BusinessInvitation bi LEFT JOIN FETCH bi.business LEFT JOIN FETCH bi.invitedBy WHERE bi.id = :id")
    Optional<BusinessInvitation> findByIdWithEagerRelationships(@Param("id") Long id);
    
    @Query("SELECT bi FROM BusinessInvitation bi LEFT JOIN FETCH bi.business LEFT JOIN FETCH bi.invitedBy")
    Page<BusinessInvitation> findAllWithEagerRelationships(Pageable pageable);
    
    @Query("SELECT bi FROM BusinessInvitation bi LEFT JOIN FETCH bi.business LEFT JOIN FETCH bi.invitedBy WHERE bi.businessId = :businessId")
    Page<BusinessInvitation> findByBusinessIdWithEagerRelationships(@Param("businessId") Long businessId, Pageable pageable);
}