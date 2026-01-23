package com.walshe.multitenant.repository;

import com.walshe.multitenant.domain.BusinessInvitation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BusinessInvitation entity.
 */
@Repository
public interface BusinessInvitationRepository
    extends JpaRepository<BusinessInvitation, Long>, JpaSpecificationExecutor<BusinessInvitation> {
    @Query(
        "select businessInvitation from BusinessInvitation businessInvitation where businessInvitation.invitedBy.login = ?#{authentication.name}"
    )
    List<BusinessInvitation> findByInvitedByIsCurrentUser();

    default Optional<BusinessInvitation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BusinessInvitation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BusinessInvitation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select businessInvitation from BusinessInvitation businessInvitation left join fetch businessInvitation.business left join fetch businessInvitation.invitedBy",
        countQuery = "select count(businessInvitation) from BusinessInvitation businessInvitation"
    )
    Page<BusinessInvitation> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select businessInvitation from BusinessInvitation businessInvitation left join fetch businessInvitation.business left join fetch businessInvitation.invitedBy"
    )
    List<BusinessInvitation> findAllWithToOneRelationships();

    @Query(
        "select businessInvitation from BusinessInvitation businessInvitation left join fetch businessInvitation.business left join fetch businessInvitation.invitedBy where businessInvitation.id =:id"
    )
    Optional<BusinessInvitation> findOneWithToOneRelationships(@Param("id") Long id);
}
