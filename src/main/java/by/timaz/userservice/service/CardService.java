package by.timaz.userservice.service;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.repository.CardRepository;
import by.timaz.userservice.dao.repository.UserRepository;
import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.exception.ResourceNotFoundException;
import by.timaz.userservice.mapping.CardMapper;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @CacheEvict(
            cacheNames = "UserService::getUserById",
            key        = "#root.args[1]"
    )
    public CardDto createCard(CardDto cardDto, UUID userId) {
            Card card =  cardMapper.toCard(cardDto);
            card.setUser(userRepository
                    .findById(userId)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("User","id",userId.toString())
                    ));
           return cardMapper.toCardDto(cardRepository.save(card));
    }
    @Cacheable(value = "CardService::getCardById", key = "#id")
    public CardDto getCardById(UUID id) {
        return cardMapper.toCardDto(cardRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Card", "id", id.toString())
        ));
    }

    @Cacheable(value = "CardService::getCardByNumber", key = "#number")
    public CardDto getCardByNumber(String number) {
        return cardMapper.toCardDto(cardRepository.findByNumber(number).orElseThrow(
                () -> new ResourceNotFoundException("Card", "number", number)
        ));
    }


    @Transactional
    @CachePut(value =  "CardService::getCardByNumber", key = "#card.number")
    public void updateCard(CardDto card) {
        cardRepository.save(cardMapper.toCard(card));
    }
    @Transactional
    @CacheEvict(value = "CardService::getCardById", key = "#id")
    public void deleteCard(UUID id) {
        cardRepository.deleteById(id);
    }
}
