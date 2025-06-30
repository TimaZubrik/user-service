package by.timaz.userservice.mapping;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,uses = CardMap.class)

public interface UserMap {
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDto userDto);

    List<User> toUserList(List<UserDto> userDtoList);
    List<UserDto> toUserDtoList(List<User> userList);
}
