package by.timaz.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    @Pattern(regexp = "^[\\p{L}]+(?:[ \\p{Pd}’']?[\\p{L}]+)*$",
            flags = Pattern.Flag.UNICODE_CASE,
            message = "Name cannot contain digits and other special characters")
    private String name;
    @Pattern(regexp = "^[\\p{L}]+(?:[ \\p{Pd}’']?[\\p{L}]+)*$",
            flags = Pattern.Flag.UNICODE_CASE,
            message = "Surname cannot contain digits and other special characters")
    private String surname;
    @Email(message = "incorrect email")
    private String email;
    @Past(message = "birthday should be in the past")
    private LocalDate birthday;
}
