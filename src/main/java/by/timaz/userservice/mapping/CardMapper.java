package by.timaz.userservice.mapping;

import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.dao.entity.Card;

public class CardMapper {
    public CardDto map(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .number(card.getNumber())
                .exp(card.getExpiryDate())
                .holder(card.getHolder())
                .build();
    }

    public Card map(CardDto cardDto) {
        return Card.builder()
                .number(cardDto.getNumber())
                .expiryDate(cardDto.getExp())
                .holder(cardDto.getHolder())
                .build();
    }
}
