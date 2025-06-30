package by.timaz.userservice.dao.repository;

import by.timaz.userservice.dao.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
