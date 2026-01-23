package com.walshe.multitenant.repository;

import com.walshe.multitenant.domain.BusinessUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BusinessUser entity.
 */
@Repository
public interface BusinessUserRepository extends JpaRepository<BusinessUser, Long>, JpaSpecificationExecutor<BusinessUser> {
    @Query("select businessUser from BusinessUser businessUser where businessUser.user.login = ?#{authentication.name}")
    List<BusinessUser> findByUserIsCurrentUser();

    default Optional<BusinessUser> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BusinessUser> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BusinessUser> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select businessUser from BusinessUser businessUser left join fetch businessUser.business left join fetch businessUser.user",
        countQuery = "select count(businessUser) from BusinessUser businessUser"
    )
    Page<BusinessUser> findAllWithToOneRelationships(Pageable pageable);

    @Query("select businessUser from BusinessUser businessUser left join fetch businessUser.business left join fetch businessUser.user")
    List<BusinessUser> findAllWithToOneRelationships();

    @Query(
        "select businessUser from BusinessUser businessUser left join fetch businessUser.business left join fetch businessUser.user where businessUser.id =:id"
    )
    Optional<BusinessUser> findOneWithToOneRelationships(@Param("id") Long id);
}
