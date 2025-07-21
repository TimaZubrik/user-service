package by.timaz.userservice.dao.repository;

import by.timaz.userservice.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID uuid);
    void deleteById(UUID uuid);
    Optional<User> findByBirthday(Date birthday);

    @Query(nativeQuery = true, value = """
        select * from users where email=:email
        """)
    Optional<User> findUserByEmail(String email);
}
