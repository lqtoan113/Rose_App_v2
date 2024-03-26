package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "Comments")
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String content;
    private boolean available;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createDate = new Date();
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "username")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "product_id")
    private Product product;

    private Long commentParentId;

    private Integer rate;
    public Comment( String content,Integer rate, Account account, Boolean available,  Product product) {
        this.content = content;
        this.rate = rate == null ? 5 : rate;
        this.account = account;
        this.available = available;
        this.product = product;
    }
}
