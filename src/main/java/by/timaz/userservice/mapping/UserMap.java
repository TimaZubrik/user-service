package by.timaz.userservice.mapping;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = CardMap.class)
public interface UserMap {

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUser(UserDto userDto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUser(UserUpdateDto userUpdateDto, @MappingTarget User user);
}
