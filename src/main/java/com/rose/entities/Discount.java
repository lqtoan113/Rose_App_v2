package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "Discounts")
@NoArgsConstructor
@AllArgsConstructor
public class Discount implements Serializable {
    @Id
    private String discountCode;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;

    private Boolean active;

    private Integer totalDiscountUsed;

    private Double totalExpense;
    private Integer discountPercent;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date startTime;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date endTime;

    @OneToMany(mappedBy = "discount",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Product> products;

    public Discount(String discountCode, String description, Integer discountPercent, Boolean active, Date startTime, Date endTime, Integer totalDiscountUsed, Double totalExpense) {
        this.discountCode = discountCode;
        this.description = description;
        this.discountPercent = discountPercent;
        this.active = active;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalDiscountUsed = totalDiscountUsed;
        this.totalExpense = totalExpense;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Discount discount = (Discount) o;
        return discountCode != null && Objects.equals(discountCode, discount.discountCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
