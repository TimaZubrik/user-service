package by.timaz.userservice.dao.repository;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByNumber(String number);
    Optional<Card> findById(UUID uuid);
}
