package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.repository.CardRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public void createCard(Card card) {
        cardRepository.save(card);
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElse(null);
    }

    public List<Card> getCardsById(List<Long> ids) {
        return cardRepository.findAllById(ids);
    }
    @Transactional
    public void updateCard(Card card) {
        cardRepository.save(card);
    }
    @Transactional
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }
}
