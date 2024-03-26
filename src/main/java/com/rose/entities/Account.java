package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rose.entities.enums.EAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Accounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {
    @Id
    @Column(columnDefinition = "VARCHAR(50)")
    private String username;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String password;

    private String refreshToken;
    @Column(columnDefinition = "NVARCHAR(50) NOT NULL")
    private String email;

    @Column(columnDefinition = "VARCHAR(12)")
    private String phone;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String fullName;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String photo;

    private Boolean gender;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String address;

    private Boolean active;

    private Double balance;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private EAuthProvider provider;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createDate = new Date();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(  name = "account_roles",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> commentList;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orderList;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Payment> payments;

    public Account( String fullName, String username, String password,String phone,  String email, Set<Role> roles, EAuthProvider provider) {
        this.fullName= fullName;
        this.email= email;
        this.username= username;
        this.password= password;
        this.active= true;
        this.phone = phone;
        this.balance = 0.00;
        this.roles = roles;
        this.provider = provider;
    }

    public Account(String username, String password, String email, String phone, String fullName, String photo, Boolean gender, String address, Boolean active, Double balance, EAuthProvider provider) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.photo = photo;
        this.gender = gender;
        this.address = address;
        this.active = active;
        this.balance = balance;
        this.provider = provider;
    }
}
