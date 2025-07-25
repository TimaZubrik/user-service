package by.timaz.userservice.data.cache;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.CardRepository;
import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.mapping.CardMapper;
import by.timaz.userservice.service.CardService;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CardServiceCacheIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.5-alpine3.22"));

    @Container
    private static final RedisContainer redisContainer =
            new RedisContainer("redis:alpine3.21").withExposedPorts(6379);

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @MockitoBean
    private CardRepository cardRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CardMapper cardMapper;

    @Autowired
    private CardService cardService;

    @Autowired
    private CacheManager cacheManager;

    private static UUID cardId;
    private static UUID userId;

    @BeforeAll
    static void init() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void testCreateCardEvictsUserCache() {
        CardDto cardDto = CardDto.builder()
                .number("1234")
                .holder("TOM MOT")
                .build();

        Card cardEntity = Card.builder()
                .id(cardId)
                .number("1234")
                .holder("TOM MOT")
                .build();

        User user = User.builder()
                .id(userId)
                .name("John")
                .email("john@domain.com")
                .build();

        when(cardMapper.toCard(cardDto)).thenReturn(cardEntity);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardMapper.toCardDto(cardEntity)).thenReturn(cardDto);

        cardService.createCard(cardDto, userId);

        Cache userCache = cacheManager.getCache("UserService::getUserById");
        assertThat(userCache).isNotNull();
        assertThat(userCache.get(userId)).isNull(); // должно быть очищено
    }

    @Test
    void testGetCardByIdCachesResult() {
        Card cardEntity = Card.builder()
                .id(cardId)
                .number("1234")
                .holder("TOM MOT")
                .build();

        CardDto cardDto = CardDto.builder()
                .number("1234")
                .holder("TOM MOT")
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toCardDto(cardEntity)).thenReturn(cardDto);

        CardDto result = cardService.getCardById(cardId);
        assertThat(result).isEqualTo(cardDto);

        Cache cache = cacheManager.getCache("CardService::getCardById");
        assertThat(cache).isNotNull();
        assertThat(cache.get(cardId).get()).isEqualTo(cardDto);
    }

    @Test
    void testGetCardByNumberCachesResult() {
        String number = "1234";

        Card cardEntity = Card.builder()
                .id(cardId)
                .number(number)
                .holder("TOM MOT")
                .build();

        CardDto cardDto = CardDto.builder()
                .number(number)
                .holder("TOM MOT")
                .build();

        when(cardRepository.findByNumber(number)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toCardDto(cardEntity)).thenReturn(cardDto);

        CardDto result = cardService.getCardByNumber(number);
        assertThat(result).isEqualTo(cardDto);

        Cache cache = cacheManager.getCache("CardService::getCardByNumber");
        assertThat(cache).isNotNull();
        assertThat(cache.get(number).get()).isEqualTo(cardDto);
    }

    @Test
    void testUpdateCardCachePut() {
        String number = "1234123412341234";

        CardDto cardDto = CardDto.builder()
                .number(number)
                .holder("TOM MOT")
                .build();

        Card cardEntity = Card.builder()
                .id(cardId)
                .number(number)
                .holder("TOM MOT")
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toCard(cardDto, cardEntity)).thenReturn(cardEntity);
        when(cardMapper.toCardDto(cardEntity)).thenReturn(cardDto);

        CardDto result = cardService.updateCard(cardId,cardDto);

        Cache cache = cacheManager.getCache("CardService::getCardByNumber");
        assertThat(cache).isNotNull();
        assertThat(cache.get(number).get()).isEqualTo(result);
        cache = cacheManager.getCache("CardService::getCardById");
        assertThat(cache).isNotNull();
        assertThat(cache.get(cardId).get()).isEqualTo(result);

    }

    @Test
    void testDeleteCardEvictsCache() {
        testCreateCardEvictsUserCache();
        cardService.deleteCard(cardId);

        Cache cache = cacheManager.getCache("CardService::getCardById");
        assertThat(cache).isNotNull();
        assertThat(cache.get(cardId)).isNull();
    }
}
