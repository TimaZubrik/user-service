package by.timaz.userservice.mapping;

import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dto.CardDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {
    CardDto toCardDto(Card card);
    @Mapping(target = "id", ignore = true)
    Card toCard(CardDto cardDto);
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Card toCard(CardDto cardDto, @MappingTarget Card card);
}
