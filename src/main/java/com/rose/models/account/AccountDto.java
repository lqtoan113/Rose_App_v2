package com.rose.models.account;

import com.rose.entities.Account;
import com.rose.entities.enums.EAuthProvider;
import com.rose.models.order.OrderResponse;
import com.rose.models.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * A DTO for the {@link Account} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto implements Serializable {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    @Size(min = 6)
    private String password;
    @NotBlank
    @Email
    private String email;
    @Pattern(regexp = "^(84|0[3|5|7|8|9])+([0-9]{8})$", message = "Your number phone invalid!")
    private String phone;
    private String fullName;
    @URL
    private String photo;
    private Boolean gender;
    private String address;
    private Boolean active;
    @Min(value = 0)
    private Long balance;
    private Date createDate;
    private Set<RoleDto> roles;
    private List<OrderResponse> orderList;
    private EAuthProvider provider;
}