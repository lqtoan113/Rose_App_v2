package com.rose.models.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^(84|0[3|5|7|8|9])+([0-9]{8})$", message = "Your number phone invalid!")
    private String phone;

    @NotBlank
    private String fullName;
    private Boolean gender;
    private String address;
}
