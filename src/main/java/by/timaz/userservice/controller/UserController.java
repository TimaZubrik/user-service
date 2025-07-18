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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/user/")
public class UserController {
    private final UserService userService;
    private final CardService cardService;

    @GetMapping(path = "{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    @PostMapping(path = "registration")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDto user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User deleted",HttpStatus.OK);
    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDto user) {
        return new ResponseEntity<>( userService.updateUser(id, user), HttpStatus.OK);
    }

    @PatchMapping(path = "{id}/new-card")
    public ResponseEntity<?> addCard(@PathVariable UUID id, @RequestBody @Valid CardDto card) {
        cardService.createCard(card, id);
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

}
