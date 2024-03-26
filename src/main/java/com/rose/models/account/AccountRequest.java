package com.rose.models.account;

import com.rose.entities.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 6)
    private String password;

    @NotBlank
    @Email
    private String email;
    @Pattern(regexp = "^(84|0[3|5|7|8|9])+([0-9]{8})$", message = "Your number phone invalid!")
    private String phone;
    @NotBlank
    @Size(min = 3, max = 50)
    private String fullName;

    @URL
    private String photo;
    private Boolean gender;
    private String address;
    private Boolean active;
    @PositiveOrZero
    private Double balance;
    private Set<ERole> roles;
}
