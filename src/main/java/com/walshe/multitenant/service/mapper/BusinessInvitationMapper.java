package com.walshe.multitenant.service.mapper;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessInvitation;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.dto.BusinessInvitationDTO;
import com.walshe.multitenant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BusinessInvitation} and its DTO {@link BusinessInvitationDTO}.
 */
@Mapper(componentModel = "spring")
public interface BusinessInvitationMapper extends EntityMapper<BusinessInvitationDTO, BusinessInvitation> {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "invitedEmail", source = "invitedEmail")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "businessId", source = "businessId")
    @Mapping(target = "createdById", source = "createdByUserId")
    @Mapping(target = "status", source = "status")
    BusinessInvitationDTO toDto(BusinessInvitation s);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "invitedEmail", source = "invitedEmail")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "businessId", source = "businessId")
    @Mapping(target = "createdByUserId", source = "createdById")
    @Mapping(target = "status", source = "status")
    BusinessInvitation toEntity(BusinessInvitationDTO businessInvitationDTO);

    @Named("businessId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BusinessDTO toDtoBusinessId(Business business);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
