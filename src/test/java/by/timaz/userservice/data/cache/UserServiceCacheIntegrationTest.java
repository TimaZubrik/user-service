package by.timaz.userservice.data.cache;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dto.UserUpdateDto;
import by.timaz.userservice.mapping.UserMapper;
import by.timaz.userservice.service.UserService;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceCacheIntegrationTest {

    @Container
    private static final RedisContainer redisContainer =
            new RedisContainer("redis:alpine3.21")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    private static UUID userId;

    @BeforeAll
    static void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void testCreateUserCache() {
        UserDto inputDto = UserDto.builder()
                .id(null)
                .name("Alice")
                .surname("Smith")
                .email("alice@domain.com")
                .birthday(LocalDate.of(1995, 7, 15))
                .build();

        User entity =User.builder()
                .id(userId)
                .email(inputDto.getEmail())
                .name(inputDto.getName())
                .surname(inputDto.getSurname())
                .birthday(inputDto.getBirthday())
                .build();

        UserDto outputDto = UserDto.builder()
                .id(userId)
                .name("Alice")
                .surname("Smith")
                .email("alice@domain.com")
                .birthday(LocalDate.of(1995, 7, 15))
                .build();

        when(userMapper.toUser(inputDto)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toUserDto(entity)).thenReturn(outputDto);

        UserDto result = userService.createUser(inputDto);
        assertThat(result).isEqualTo(outputDto);

        Cache cacheById = cacheManager.getCache("UserService::getUserById");
        assertThat(cacheById).isNotNull();
        Cache.ValueWrapper wrapperById = cacheById.get(userId);
        assertThat(wrapperById).isNotNull();
        assertThat(wrapperById.get()).isEqualTo(outputDto);

        Cache cacheByEmail = cacheManager.getCache("UserService::getUserByEmail");
        assertThat(cacheByEmail).isNotNull();
        Cache.ValueWrapper wrapperByEmail = cacheByEmail.get(outputDto.getEmail());
        assertThat(wrapperByEmail).isNotNull();
        assertThat(wrapperByEmail.get()).isEqualTo(outputDto);

        verify(userRepository, times(1)).save(entity);
    }

    @Test
    void testGetUserByIdCache() {
        UUID newId = UUID.randomUUID();
        UserDto outputDto = UserDto.builder()
                .id(newId)
                .name("Alice")
                .surname("Smith")
                .email("alice@domain.com")
                .birthday(LocalDate.of(1995, 7, 15))
                .build();
        User entity =User.builder()
                .id(newId)
                .email(outputDto.getEmail())
                .name(outputDto.getName())
                .surname(outputDto.getSurname())
                .birthday(outputDto.getBirthday())
                .build();

        when(userRepository.findById(newId)).thenReturn(Optional.of(entity));
        when(userMapper.toUserDto(entity)).thenReturn(outputDto);

        UserDto result = userService.getUserById(newId);
        assertThat(result).isEqualTo(outputDto);

        Cache cacheById = cacheManager.getCache("UserService::getUserById");
        assertThat(cacheById).isNotNull();
        Cache.ValueWrapper wrapperById = cacheById.get(newId);
        assertThat(wrapperById).isNotNull();
        assertThat(wrapperById.get()).isEqualTo(outputDto);
    }

    @Test
    void testGetUserByEmailCache() {
        UUID newId = UUID.randomUUID();
        String email = "alice2@domain.com";
        UserDto outputDto = UserDto.builder()
                .id(newId)
                .name("Alice")
                .surname("Smith")
                .email(email)
                .birthday(LocalDate.of(1995, 7, 15))
                .build();
        User entity =User.builder()
                .id(newId)
                .email(email)
                .name(outputDto.getName())
                .surname(outputDto.getSurname())
                .birthday(outputDto.getBirthday())
                .build();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(entity));
        when(userMapper.toUserDto(entity)).thenReturn(outputDto);

        UserDto result = userService.getUserByEmail(email);
        assertThat(result).isEqualTo(outputDto);
        Cache cacheById = cacheManager.getCache("UserService::getUserByEmail");
        assertThat(cacheById).isNotNull();
        Cache.ValueWrapper wrapperById = cacheById.get(email);
        assertThat(wrapperById).isNotNull();
        assertThat(wrapperById.get()).isEqualTo(outputDto);
    }

    @Test
    void testUpdateUserCache() {
        UUID newId = UUID.randomUUID();
        String email = "alice@domain.com";
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .email(email)
                .name("Alice")
                .build();
        UserDto userDto = UserDto.builder()
                .id(newId)
                .name("Alice")
                .surname("Smith")
                .email(email)
                .birthday(LocalDate.of(1995, 7, 15))
                .build();
        User entity =User.builder()
                .id(newId)
                .email(email)
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .birthday(userDto.getBirthday())
                .build();

        when(userRepository.findById(newId)).thenReturn(Optional.of(entity));
        when(userMapper.toUser(updateDto, entity)).thenReturn(entity);
        when(userMapper.toUserDto(entity)).thenReturn(userDto);

        UserDto result = userService.updateUser(newId, updateDto);
        assertThat(result).isEqualTo(userDto);
        Cache cacheById = cacheManager.getCache("UserService::getUserById");
        assertThat(cacheById).isNotNull();
        Cache.ValueWrapper wrapperById = cacheById.get(newId);
        assertThat(wrapperById).isNotNull();
        assertThat(wrapperById.get()).isEqualTo(userDto);
    }

    @Test
    void testDeleteUserCache() {
        testCreateUserCache();
        userService.deleteUser(userId);

        Cache cacheById = cacheManager.getCache("UserService::getUserById");
        assertThat(cacheById).isNotNull();
        Cache.ValueWrapper wrapperById = cacheById.get(userId);
        assertThat(wrapperById).isNull();
    }
}
