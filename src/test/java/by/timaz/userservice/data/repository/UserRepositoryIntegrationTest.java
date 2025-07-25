package by.timaz.userservice.data.repository;

import by.timaz.userservice.dao.entity.User;
import by.timaz.userservice.dao.repository.UserRepository;
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
public class UserRepositoryIntegrationTest {

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
    private UserRepository userRepository;

    @Test
    void testSaveAndFoundUser() {
       String email = "test@examle.com";
        User user = User.builder()
                .email(email)
                .name("Gregory")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userRepository.save(user);
        Optional<User> userFound = userRepository.findUserByEmail(email);
        assertTrue(userFound.isPresent());
        assertEquals("Gregory", userFound.get().getName());
    }

    @Test
    void testUpdateUser() {
        testSaveAndFoundUser();

        User user = userRepository.findUserByEmail("test@examle.com").get();
        user.setName("Updated Name");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        user.setEmail("new@email.ru");

        UUID userId = user.getId();

        userRepository.saveAndFlush(user);
        User updatedUser =  userRepository.findById(userId).get();

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("new@email.ru", updatedUser.getEmail());
        assertEquals(LocalDate.of(1999, 1, 1), updatedUser.getBirthday());
        assertEquals(Optional.empty(), userRepository.findUserByEmail("test@examle.com"));
    }

    @Test
    void testDeleteUser() {
        UUID id = userRepository.findUserByEmail("test@examle.com").get().getId();
        userRepository.deleteById(id);
        assertEquals(Optional.empty(),userRepository.findById(id));
    }

    @Test
    void testUniqueEmailCheck() {
        String email = "test2@examle.com";
        User user1 = User.builder()
                .email(email)
                .name("Gregory")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .email(email)
                .name("Alice")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userRepository.save(user1);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
    }

    @Test
    void testNotNullValidation() {
        String email = "test@examle.com";
        User user1 = User.builder()
                .name("Gregory")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .email(email)
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user3 = User.builder()
                .email(email)
                .name("Alice")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user1));
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user3));
    }

}
