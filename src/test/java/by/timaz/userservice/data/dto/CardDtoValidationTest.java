package by.timaz.userservice.data.dto;

import by.timaz.userservice.dto.CardDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testCorrectCardDto() {
        CardDto cardDto = CardDto.builder()
                .number("1234567890123456")
                .holder("JOHN DOE")
                .expiryDate("12/25")
                .build();

        assertTrue(validator.validate(cardDto).isEmpty());
    }

    @Test
    void testCardDto_invalidNumber() {
        CardDto card1 = CardDto.builder()
                .number(null)
                .holder("JOHN DOE")
                .expiryDate("12/25")
                .build();
        CardDto card2 = CardDto.builder()
                .number("")
                .holder("JOHN DOE")
                .expiryDate("12/25")
                .build();
        CardDto card3 = CardDto.builder()
                .number("123456789012")
                .holder("JOHN DOE")
                .expiryDate("12/25")
                .build();
        CardDto card4 = CardDto.builder()
                .number("12345678901234567890")
                .holder("JOHN DOE")
                .expiryDate("12/25")
                .build();
        CardDto card5 = CardDto.builder()
                .number("1234abc8901234")
                .holder("JOHN DOE")
                .expiryDate("12/25")
                .build();

        String emptyMsg   = "card number cannot be empty";
        String patternMsg = "card number must contain from 13 to 19 digits";

        List<String> v1 = validator.validate(card1).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v2 = validator.validate(card2).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v3 = validator.validate(card3).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v4 = validator.validate(card4).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v5 = validator.validate(card5).stream()
                .map(ConstraintViolation::getMessage).toList();

        assertTrue(v1.contains(emptyMsg));
        assertTrue(v2.contains(emptyMsg));
        assertTrue(v3.contains(patternMsg));
        assertTrue(v4.contains(patternMsg));
        assertTrue(v5.contains(patternMsg));
    }

    @Test
    void testCardDto_invalidHolder() {
        CardDto card1 = CardDto.builder()
                .number("1234567890123")
                .holder("")
                .expiryDate("12/25")
                .build();
        CardDto card2 = CardDto.builder()
                .number("1234567890123")
                .holder("John Doe")
                .expiryDate("12/25")
                .build();
        CardDto card3 = CardDto.builder()
                .number("1234567890123")
                .holder("JOHN1 DOE")
                .expiryDate("12/25")
                .build();
        CardDto card4 = CardDto.builder()
                .number("1234567890123")
                .holder(null)
                .expiryDate("12/25")
                .build();

        String patternMsg = "holder's name must contain no more than 26 capital letters";

        List<String> v1 = validator.validate(card1).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v2 = validator.validate(card2).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v3 = validator.validate(card3).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v4 = validator.validate(card4).stream()
                .map(ConstraintViolation::getMessage).toList();

        assertTrue(v1.contains(patternMsg));
        assertTrue(v2.contains(patternMsg));
        assertTrue(v3.contains(patternMsg));
        // holder is optional - null should not trigger Pattern
        assertTrue(v4.isEmpty());
    }

    @Test
    void testCardDto_invalidExpiryDate() {
        CardDto card1 = CardDto.builder()
                .number("1234567890123")
                .holder("JOHN DOE")
                .expiryDate(null)
                .build();
        CardDto card2 = CardDto.builder()
                .number("1234567890123")
                .holder("JOHN DOE")
                .expiryDate("")
                .build();
        CardDto card3 = CardDto.builder()
                .number("1234567890123")
                .holder("JOHN DOE")
                .expiryDate("1225")
                .build();

        String emptyMsg   = "expiration date cannot be empty";
        String patternMsg = "expiration date must follow the pattern MM/YY";

        List<String> v1 = validator.validate(card1).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v2 = validator.validate(card2).stream()
                .map(ConstraintViolation::getMessage).toList();
        List<String> v3 = validator.validate(card3).stream()
                .map(ConstraintViolation::getMessage).toList();

        assertTrue(v1.contains(emptyMsg));
        assertTrue(v2.contains(emptyMsg));
        assertTrue(v3.contains(patternMsg));
    }
}
