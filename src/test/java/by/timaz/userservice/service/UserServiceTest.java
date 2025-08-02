package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import by.timaz.userservice.exception.ResourceNotFoundException;
import by.timaz.userservice.mapping.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserTest() {
        UserDto inputDto = UserDto.builder()
                .id(null)
                .name("Alice")
                .email("alice@gmail.com")
                .build();
        User mappedEntity = User.builder()
                .id(null)
                .name("Alice")
                .email("alice@gmail.com")
                .build();

        UUID uuid = UUID.randomUUID();
        User savedEntity = User.builder()
                .id(uuid)
                .name("Alice")
                .email("alice@gmail.com")
                .build();
        UserDto expectedDto = UserDto.builder()
                .id(uuid)
                .name("Alice")
                .email("alice@gmail.com")
                .build();

        when(userMapper.toUser(inputDto)).thenReturn(mappedEntity);
        when(userRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(userMapper.toUserDto(savedEntity)).thenReturn(expectedDto);

        UserDto actualDto = userService.createUser(inputDto);

        assertEquals(expectedDto, actualDto, "Method must return correct UserDto");

        verify(userMapper).toUser(inputDto);
        verify(userRepository).save(mappedEntity);
        verify(userMapper).toUserDto(savedEntity);
    }

    @Test
    void getUserByIdTest() {
        UUID uuid = UUID.randomUUID();
        User expectedUser = User.builder()
                .id(uuid)
                .name("Alice")
                .email("alice@gmail.com")
                .build();
        UserDto expectedDto = UserDto.builder()
                .id(uuid)
                .name("Alice")
                .email("alice@gmail.com")
                .build();

        when(userRepository.findById(uuid)).thenReturn(Optional.of(expectedUser));
        when(userMapper.toUserDto(expectedUser)).thenReturn(expectedDto);

        UserDto actualDto = userService.getUserById(uuid);
        assertEquals(expectedDto, actualDto, "Method must return correct UserDto (by id)");
        verify(userRepository).findById(uuid);
    }

    @Test
    void getUserByIdNotFoundTest() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());
        try {
            userService.getUserById(uuid);
            fail("Exception must have been thrown (id not found)");
        }catch (ResourceNotFoundException e){
            assertEquals("User with id = "+uuid+" not found", e.getMessage());
            verify(userRepository).findById(uuid);
        }
    }

    @Test
    void getUserByEmailTest() {
        UUID uuid = UUID.randomUUID();
        String userEmail = "alice@gmail.com";
        User expectedUser = User.builder()
                .id(uuid)
                .name("Alice")
                .email(userEmail)
                .build();
        UserDto expectedDto = UserDto.builder()
                .id(uuid)
                .name("Alice")
                .email(userEmail)
                .build();

        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(expectedUser));
        when(userMapper.toUserDto(expectedUser)).thenReturn(expectedDto);

        UserDto actualDto = userService.getUserByEmail(userEmail);
        assertEquals(expectedDto, actualDto, "Method must return correct UserDto (by email)");
        verify(userRepository).findUserByEmail(userEmail);
        verify(userMapper).toUserDto(expectedUser);
    }

    @Test
    void getUserByEmailNotFoundTest() {
        String userEmail = "notexist@email.com";
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.empty());
        try {
            userService.getUserByEmail(userEmail);
            fail("Exception must have been thrown (email not found)");
        }catch (ResourceNotFoundException e){
            assertEquals("User with email = "+userEmail+" not found", e.getMessage());
            verify(userRepository).findUserByEmail(userEmail);
        }
    }

    @Test
    void updateUserTest() {
        UUID uuid = UUID.randomUUID();
        String userEmail = "alice@gmail.com";
        User userToUpdate = User.builder()
                .id(uuid)
                .name("John")
                .email(userEmail)
                .build();
        User userAfterUpdate = User.builder()
                .id(uuid)
                .name("Alice")
                .email(userEmail)
                .build();
        UserDto dtoAfterUpdate = UserDto.builder()
                .id(uuid)
                .name("Alice")
                .email(userEmail)
                .build();
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Alice")
                .email(userEmail)
                .build();

        when(userRepository.findById(uuid)).thenReturn(Optional.of(userToUpdate));
        when(userMapper.toUser(updateDto, userToUpdate)).thenReturn(userAfterUpdate);
        when(userMapper.toUserDto(userAfterUpdate)).thenReturn(dtoAfterUpdate);

        UserDto actualDto = userService.updateUser(uuid, updateDto);
        assertEquals(dtoAfterUpdate, actualDto, "Method must return correct UserDto (update)");
        verify(userRepository).findById(uuid);
    }

    @Test
    void updateUserNotFoundTest() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());
        try {
            userService.updateUser(uuid, new UserUpdateDto());
            fail("Exception must have been thrown (id not found in update method)");
        }catch (ResourceNotFoundException e){
            assertEquals("User with id = "+uuid+" not found", e.getMessage());
            verify(userRepository).findById(uuid);
        }
    }

    @Test
    void deleteUserTest() {
        UUID userId = UUID.randomUUID();
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}