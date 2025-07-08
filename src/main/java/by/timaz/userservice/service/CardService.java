package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.repository.CardRepository;
import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.mapping.CardMap;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardMap cardMap;

    public void createCard(CardDto card) {
        cardRepository.save(cardMap.toCard(card));
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateCard(CardDto card) {
        cardRepository.save(cardMap.toCard(card));
    }
    @Transactional
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }
}
