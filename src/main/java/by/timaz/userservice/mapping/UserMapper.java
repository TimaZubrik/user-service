package by.timaz.userservice.mapping;


import by.timaz.userservice.dto.CardDto;
import by.timaz.userservice.dto.UserDto;
import by.timaz.userservice.dao.entity.Card;
import by.timaz.userservice.dao.entity.User;

import java.util.List;

public class UserMapper  {
    private final CardMapper cardMapper = new CardMapper();

   public UserDto map(User user){
       List<CardDto> cardDtos =
               user.getCards().stream().map(cardMapper::map ).toList();
       return UserDto.builder()
               .id(user.getId())
               .email(user.getEmail())
               .name(user.getName())
               .surname(user.getSurname())
               .birthday(user.getBirthday())
               .cards(cardDtos)
               .build();
   }

   public User map(UserDto userDto){
       List<Card> cards = userDto.getCards().stream().map( cardMapper::map ).toList();
       return User.builder()
               .email(userDto.getEmail())
               .birthday(userDto.getBirthday())
               .name(userDto.getName())
               .surname(userDto.getSurname())
               .cards(cards)
               .build();
   }
}
