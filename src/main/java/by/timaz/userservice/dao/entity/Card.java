package by.timaz.userservice.dao.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@Table(name = "card_info")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "number",nullable = false, unique=true, length = 16)
    private String number;
    @Column(name = "holder", nullable = false, length = 26)
    private String holder;
    @Column(name = "expiration_date",nullable = false, length = 5)
    private String expiryDate;

    @ManyToMany(mappedBy = "cards")
    private Set<User> users;
}
