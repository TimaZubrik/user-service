package by.timaz.userservice.service;

import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import by.timaz.userservice.exception.ResourceNotFoundException;
import by.timaz.userservice.mapping.UserMap;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMap userMap;


    public UserDto createUser(UserDto user) {
            userRepository.save(userMap.toUser(user));
            return userMap.toUserDto(userRepository.findUserByEmail(user.getEmail()).get());

    }

    public UserDto getUserById(Long id) {
        return userMap.toUserDto(userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User","id", id.toString())));
    }

     public UserDto getUserByEmail(String email) {
        return userMap.toUserDto(userRepository.findUserByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User","email", email)));
    }


    @Transactional
    public UserDto updateUser(Long id,@Valid UserUpdateDto userDto) {

        return userMap.toUserDto(
                userMap.toUser(
                        userDto,
                        userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User","id", id.toString()))
        ));
    }
    @Transactional
    public void deleteUser(Long id) {
            userRepository.deleteById(id);
    }

}
