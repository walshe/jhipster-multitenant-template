package com.walshe.multitenant.service.mapper;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.BusinessUser;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.dto.BusinessUserDTO;
import com.walshe.multitenant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BusinessUser} and its DTO {@link BusinessUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface BusinessUserMapper extends EntityMapper<BusinessUserDTO, BusinessUser> {
    @Mapping(target = "business", source = "business", qualifiedByName = "businessName")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    BusinessUserDTO toDto(BusinessUser s);

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
