package by.timaz.userservice.controller;

import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import by.timaz.userservice.service.CardService;
import by.timaz.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user/")
public class UserController {
    private final UserService userService;
    private final CardService cardService;

    @GetMapping(path = "{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    @PostMapping(path = "registration")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User deleted",HttpStatus.OK);
    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDto user) {
        return new ResponseEntity<>( userService.updateUser(id, user), HttpStatus.OK);
    }

    @PatchMapping(path = "{id}/new-card")
    public ResponseEntity<UserDto> addCard(@PathVariable UUID id, @RequestBody @Valid CardDto card) {
        cardService.createCard(card, id);
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

}
