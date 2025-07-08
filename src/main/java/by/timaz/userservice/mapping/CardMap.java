package by.timaz.userservice.mapping;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dto.CardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMap {
    CardDto toCardDto(Card card);
    @Mapping(target = "id", ignore = true)
    Card toCard(CardDto cardDto);
}
