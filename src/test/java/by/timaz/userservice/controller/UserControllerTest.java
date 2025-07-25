package by.timaz.userservice.controller;

import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import by.timaz.userservice.service.CardService;
import by.timaz.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CardService cardService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserById() throws Exception {
        UUID uuid = UUID.randomUUID();
        UserDto userDto = UserDto.builder()
                .id(uuid)
                .name("John")
                .email("test@gmail.com")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        when(userService.getUserById(uuid)).thenReturn(Optional.of(userDto).get());
        mockMvc.perform(get("/user/{id}", uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
        verify(userService, times(1)).getUserById(uuid);
    }

    @Test
    void createUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John")
                .surname("Smith")
                .email("test@gmail.com")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        var userJson = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(post("/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void getUserByEmail() throws Exception {
        UUID uuid = UUID.randomUUID();
        String email = "test@gmail.com";
        UserDto userDto = UserDto.builder()
                .id(uuid)
                .name("John")
                .email(email)
                .birthday(LocalDate.of(2000,1,1))
                .build();
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(userDto).get());
        mockMvc.perform(get("/user/")
                        .param("email", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
        verify(userService, times(1)).getUserByEmail(email);
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/user/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser() throws Exception {
        UUID uuid = UUID.randomUUID();
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Alice")
                .build();
        UserDto userDto = UserDto.builder()
                .id(uuid)
                .email("test@gmail.com")
                .name("Alice")
                .build();
        when(userService.updateUser(uuid,userUpdateDto)).thenReturn(userDto);
        mockMvc.perform(patch("/user/{id}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
        verify(userService, times(1)).updateUser(uuid,userUpdateDto);
    }

    @Test
    void addCard() throws Exception {
        UUID uuid = UUID.randomUUID();
        CardDto cardDto = CardDto.builder()
                .number("1234567812345678")
                .holder("TOM MOT")
                .expiryDate("09/27")
                .build();
        UserDto userDto = UserDto.builder()
                .id(uuid)
                .email("test@gmail.com")
                .name("Alice")
                .build();
        userDto.addCard(cardDto);

        when(cardService.createCard(cardDto,uuid)).thenReturn(cardDto);
        when(userService.getUserById(uuid)).thenReturn(Optional.of(userDto).get());

        mockMvc.perform(patch("/user/{id}/new-card", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }


}
