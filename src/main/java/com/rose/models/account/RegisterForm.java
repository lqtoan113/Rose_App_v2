package com.rose.models.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A form for the {@link com.rose.entities.Account} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForm {
    @NotBlank
    private String fullName;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(84|0[3|5|7|8|9])+([0-9]{8})$", message = "Your number phone invalid!")
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}
