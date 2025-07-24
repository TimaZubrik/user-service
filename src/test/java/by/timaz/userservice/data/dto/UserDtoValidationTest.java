package by.timaz.userservice.data.dto;

import by.timaz.userservice.dto.UserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDtoValidationTest {

    private static Validator validator;
    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    @Test
    void testCorrectUserDto() {
        UserDto userDto = UserDto.builder()
                .email("test@example.com")
                .name("Osas")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();
      assertTrue(validator.validate(userDto).isEmpty());
    }

    @Test
    void testUserDto_invalidEmail() {
        UserDto userDto1 = UserDto.builder()
                .email("testexamplecom")
                .name("Osas")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto2 = UserDto.builder()
                .email(null)
                .name("Osas")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto3 = UserDto.builder()
                .email("")
                .name("Osas")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();
    var check1 = validator.validate(userDto1).stream()
            .map(ConstraintViolation::getMessage)
            .toList()
            .getFirst();
    var check2 = validator.validate(userDto2).stream()
            .map(ConstraintViolation::getMessage)
            .toList()
            .getFirst();
    var check3 = validator.validate(userDto3).stream()
            .map(ConstraintViolation::getMessage)
            .toList()
            .getFirst();
    assertEquals("Incorrect email",check1);
    assertEquals("Email cannot be empty",check2);
    assertEquals("Email cannot be empty",check3);
    }

    @Test
    void testUserDto_invalidBirthday() {
        UserDto userDto1 = UserDto.builder()
                .email("test@example.com")
                .name("Osas")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2100,1,1))
                .build();
        UserDto userDto2 = UserDto.builder()
                .email("test@example.com")
                .name("Osas")
                .surname("Ouwauwauwuwa")
                .birthday(null)
                .build();

        var check1 = validator.validate(userDto1).stream()
                .map(ConstraintViolation::getMessage)
                .toList()
                .getFirst();
        var check2 = validator.validate(userDto2).stream()
                .map(ConstraintViolation::getMessage)
                .toList()
                .getFirst();
        assertEquals("Birthday should be in the past",check1);
        assertEquals("Birthday cannot be empty",check2);
    }

    @Test
    void testUserDto_invalidName() {
        UserDto userDto1 = UserDto.builder()
                .email("test@example.com")
                .name("")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto2 = UserDto.builder()
                .email("test@example.com")
                .name("123")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto3 = UserDto.builder()
                .email("test@example.com")
                .name(null)
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto4 = UserDto.builder()
                .email("test@example.com")
                .name("Lucas???")
                .surname("Ouwauwauwuwa")
                .birthday(LocalDate.of(2000,1,1))
                .build();

        String empty = "Name cannot be empty. ";
        String pattern =  "Name cannot contain digits and other special characters";
        var messages = List.of(empty, pattern);

        var check1 = validator.validate(userDto1).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        var check2 = validator.validate(userDto2).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        var check3 = validator.validate(userDto3).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        var check4 = validator.validate(userDto4).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
       assertTrue(check1.containsAll(messages));
       assertTrue(check2.contains(pattern));
       assertTrue(check3.contains(empty));
       assertTrue(check4.contains(pattern));
    }

    @Test
    void testUserDto_invalidSurname() {
        UserDto userDto1 = UserDto.builder()
                .email("test@example.com")
                .name("John")
                .surname("")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto2 = UserDto.builder()
                .email("test@example.com")
                .name("John")
                .surname("123")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto3 = UserDto.builder()
                .email("test@example.com")
                .name("John")
                .surname(null)
                .birthday(LocalDate.of(2000,1,1))
                .build();
        UserDto userDto4 = UserDto.builder()
                .email("test@example.com")
                .name("John")
                .surname("Ouwauwauwuwa??")
                .birthday(LocalDate.of(2000,1,1))
                .build();

        String empty = "Surname cannot be empty";
        String pattern = "Surname cannot contain digits and other special characters";
        var messages = List.of(empty, pattern);

        var check1 = validator.validate(userDto1).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        var check2 = validator.validate(userDto2).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        var check3 = validator.validate(userDto3).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        var check4 = validator.validate(userDto4).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        assertTrue(check1.containsAll(messages));
        assertTrue(check2.contains(pattern));
        assertTrue(check3.contains(empty));
        assertTrue(check4.contains(pattern));
    }

}
