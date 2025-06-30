package by.timaz.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {
    private long id;
    @NotBlank
    @Pattern(regexp = "\\d{16}")
    private String number;
    @Pattern(regexp = "^[A-Z][A-Z]+(?: [A-Z]+)*$")
    private String holder;
    @NotBlank
    @Pattern(regexp = "\\d{2}/\\d{2}")
    private String exp;
}
