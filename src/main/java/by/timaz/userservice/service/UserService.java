package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import by.timaz.userservice.exception.ResourceNotFoundException;
import by.timaz.userservice.mapping.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Caching(put = {
            @CachePut(value = "UserService::getUserById", key = "#result.id"),
            @CachePut(value = "UserService::getUserByEmail", key = "#result.email")
    })
    public UserDto createUser(UserDto user) {
        User savedUser = userRepository.save(userMapper.toUser(user));
        return userMapper.toUserDto(savedUser);

    }

    @Cacheable(value = "UserService::getUserById", key = "#id")
    public UserDto getUserById(UUID id) {
        return userMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("User","id", id.toString())
                ));
    }
    @Cacheable(value = "UserService::getUserByEmail", key = "#email")
     public UserDto getUserByEmail(String email) {
        return userMapper.toUserDto(userRepository.findUserByEmail(email)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("User","email", email)
                ));
    }


    @Transactional
    @CachePut(value ="UserService::getUserById", key = "#id")
    public UserDto updateUser(UUID id, UserUpdateDto userDto) {

        if (userDto.getEmail() != null && userDto.getEmail().isBlank()) {
            userDto.setEmail(null);
        }
        return userMapper.toUserDto(
                userMapper.toUser(
                        userDto,
                        userRepository.findById(id)
                                .orElseThrow(
                                        ()-> new ResourceNotFoundException("User","id", id.toString())
                                )
        ));
    }
    @Transactional
    @CacheEvict(value ="UserService::getUserById", key = "#id")
    public void deleteUser(UUID id) {
            userRepository.deleteById(id);
    }

}
