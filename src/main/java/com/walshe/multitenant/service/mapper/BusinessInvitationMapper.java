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
    @Mapping(target = "business", source = "business", qualifiedByName = "businessName")
    @Mapping(target = "invitedBy", source = "invitedBy", qualifiedByName = "userLogin")
    BusinessInvitationDTO toDto(BusinessInvitation s);

    @Named("businessName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BusinessDTO toDtoBusinessName(Business business);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
