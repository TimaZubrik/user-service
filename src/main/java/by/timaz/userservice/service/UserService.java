package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.mapping.UserMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMap userMap;


    public void createUser(UserDto user) {
        userRepository.save(userMap.toUser(user));
    }

    public UserDto getUserById(Long id) {
        return userMap.toUserDto(userRepository.findById(id).get());
    }

    public List<UserDto> getUsersById(List<Long> ids) {
        return userMap.toUserDtoList(userRepository.findAllById(ids));
    }

    public UserDto getUserByEmail(String email) {
        return userMap.toUserDto(userRepository.getUserByEmail(email));
    }
    @Transactional
    public void updateUser(UserDto user) {
        userRepository.save(userMap.toUser(user));
    }
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
