package com.walshe.multitenant.service.mapper;

import com.walshe.multitenant.domain.Business;
import com.walshe.multitenant.domain.User;
import com.walshe.multitenant.service.dto.BusinessDTO;
import com.walshe.multitenant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Business} and its DTO {@link BusinessDTO}.
 */
@Mapper(componentModel = "spring")
public interface BusinessMapper extends EntityMapper<BusinessDTO, Business> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userLogin")
    BusinessDTO toDto(Business s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
