package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.CardRepository;
import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import by.timaz.userservice.exception.ResourceNotFoundException;
import by.timaz.userservice.mapping.CardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCardTest() {
        UUID userId = UUID.randomUUID();
        CardDto inputDto = CardDto.builder()
                .number("1234-5678-9012-3456")
                .build();

        Card mappedCard = Card.builder()
                .id(null)
                .number("1234-5678-9012-3456")
                .build();

        User userEntity = User.builder()
                .id(userId)
                .name("Bob")
                .email("bob@example.com")
                .build();

        Card savedCard = Card.builder()
                .id(UUID.randomUUID())
                .number("1234-5678-9012-3456")
                .user(userEntity)
                .build();

        CardDto expectedDto = CardDto.builder()
                .number("1234-5678-9012-3456")
                .build();

        when(cardMapper.toCard(inputDto)).thenReturn(mappedCard);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(cardRepository.save(mappedCard)).thenReturn(savedCard);
        when(cardMapper.toCardDto(savedCard)).thenReturn(expectedDto);

        CardDto actualDto = cardService.createCard(inputDto, userId);

        assertEquals(expectedDto, actualDto, "Method must return correct CardDto");

        verify(cardMapper).toCard(inputDto);
        verify(userRepository).findById(userId);
        verify(cardRepository).save(mappedCard);
        verify(cardMapper).toCardDto(savedCard);
    }

    @Test
    void createCardUserNotFoundTest() {
        UUID userId = UUID.randomUUID();
        CardDto inputDto = CardDto.builder().number("0000-0000-0000-0000").build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        try {
            cardService.createCard(inputDto, userId);
            fail("Exception must have been thrown (user not found)");
        } catch (ResourceNotFoundException e) {
            assertEquals("User with id = " + userId + " not found", e.getMessage());
            verify(userRepository).findById(userId);
            verify(cardMapper).toCard(inputDto);
            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(cardRepository);
        }
    }

    @Test
    void getCardByIdTest() {
        UUID cardId = UUID.randomUUID();
        Card cardEntity = Card.builder()
                .id(cardId)
                .number("1111-2222-3333-4444")
                .build();
        CardDto expectedDto = CardDto.builder()
                .number("1111-2222-3333-4444")
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toCardDto(cardEntity)).thenReturn(expectedDto);

        CardDto actualDto = cardService.getCardById(cardId);

        assertEquals(expectedDto, actualDto, "Method must return correct CardDto (by id)");
        verify(cardRepository).findById(cardId);
        verify(cardMapper).toCardDto(cardEntity);
    }

    @Test
    void getCardByIdNotFoundTest() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        try {
            cardService.getCardById(cardId);
            fail("Exception must have been thrown (card id not found)");
        } catch (ResourceNotFoundException e) {
            assertEquals("Card with id = " + cardId + " not found", e.getMessage());
            verify(cardRepository).findById(cardId);
        }
    }

    @Test
    void getCardByNumberTest() {
        String number = "5555-6666-7777-8888";
        Card cardEntity = Card.builder()
                .id(UUID.randomUUID())
                .number(number)
                .build();
        CardDto expectedDto = CardDto.builder()
                .number(number)
                .build();

        when(cardRepository.findByNumber(number)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toCardDto(cardEntity)).thenReturn(expectedDto);

        CardDto actualDto = cardService.getCardByNumber(number);

        assertEquals(expectedDto, actualDto, "Method must return correct CardDto (by number)");
        verify(cardRepository).findByNumber(number);
        verify(cardMapper).toCardDto(cardEntity);
    }

    @Test
    void getCardByNumberNotFoundTest() {
        String number = "9999-0000-1111-2222";
        when(cardRepository.findByNumber(number)).thenReturn(Optional.empty());

        try {
            cardService.getCardByNumber(number);
            fail("Exception must have been thrown (card number not found)");
        } catch (ResourceNotFoundException e) {
            assertEquals("Card with number = " + number + " not found", e.getMessage());
            verify(cardRepository).findByNumber(number);
        }
    }
    @Test
    void updateCardTest() {
        UUID uuid = UUID.randomUUID();
        String number = "5555-6666-7777-8888";
        Card cardEntity = Card.builder()
                .id(uuid)
                .number(number)
                .build();
        CardDto updateDto = CardDto.builder()
                .number(number)
                .build();

        when(cardRepository.findById(uuid)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toCard(updateDto, cardEntity)).thenReturn(cardEntity);
        when(cardMapper.toCardDto(cardEntity)).thenReturn(updateDto);

        CardDto actualDto = cardService.updateCard(uuid, updateDto);
        assertEquals(updateDto, actualDto, "Method must return correct UserDto (update)");
        verify(cardRepository).findById(uuid);
    }
    @Test
    void deleteCardTest() {
        UUID cardId = UUID.randomUUID();

        cardService.deleteCard(cardId);

        verify(cardRepository, times(1)).deleteById(cardId);
    }
}