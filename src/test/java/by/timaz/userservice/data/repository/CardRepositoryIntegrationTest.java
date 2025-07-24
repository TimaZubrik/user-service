package by.timaz.userservice.data.repository;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.CardRepository;
import by.timaz.userservice.dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CardRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.5-alpine3.22"));
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private static int i=0;
    private static UUID cardId;

    @BeforeEach
     void setUp() {
        User userMain = User.builder()
                .email("test"+i+"@ex.com")
                .name("Gregory")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        i++;
        userRepository.save(userMain);
        user = userRepository.findUserByEmail(userMain.getEmail()).get();
    }

    @Test
    void testSaveAndFoundCard(){
        Card card = Card.builder()
                .number("1234123412341234")
                .user(user)
                .holder("holder")
                .expiryDate("09/26")
                .build();
        cardRepository.save(card);
        Optional<Card> foundCard = cardRepository.findByNumber(card.getNumber());
        assertTrue(foundCard.isPresent());
        cardId = foundCard.get().getId();
        assertEquals(foundCard.get().getNumber(), card.getNumber());
    }

    @Test
    void testUpdateCard(){
        testSaveAndFoundCard();

        Card card = cardRepository.findByNumber("1234123412341234").get();
        UUID cardId = card.getId();

        card.setHolder("holder new");
        card.setNumber("9999000099990000");
        cardRepository.save(card);

        Card updatedCard = cardRepository.findById(cardId).get();
        assertEquals("holder new", updatedCard.getHolder());
        assertEquals("9999000099990000", updatedCard.getNumber());
    }

    @Test
    void testDeleteCard(){

        Card card = cardRepository.findById(cardId).get();
        cardRepository.delete(card);

        Optional<Card> foundCard = cardRepository.findById(card.getId());
        assertTrue(foundCard.isEmpty());
    }

    @Test
    void testNotNullValidation(){
        Card card1 = Card.builder()
                .user(user)
                .holder("holder")
                .expiryDate("09/26")
                .build();
        Card card2 = Card.builder()
                .number("1234123412341234")
                .user(user)
                .expiryDate("09/26")
                .build();
        Card card3 = Card.builder()
                .number("1234123412341234")
                .user(user)
                .holder("holder")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> cardRepository.save(card1));
        assertThrows(DataIntegrityViolationException.class, () -> cardRepository.save(card2));
        assertThrows(DataIntegrityViolationException.class, () -> cardRepository.save(card3));
    }

    @Test
    void testFK(){
        Card card1 = Card.builder()
                .number("2234123412341234")
                .user(user)
                .holder("holder")
                .expiryDate("09/26")
                .build();
        Card card2 = Card.builder()
                .number("2234123412341234")
                .user(user)
                .holder("holder")
                .expiryDate("09/26")
                .build();
        cardRepository.save(card1);
        assertThrows(DataIntegrityViolationException.class, () -> cardRepository.save(card2));
    }
}
