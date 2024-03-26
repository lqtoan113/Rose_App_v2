package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "Colors")
@NoArgsConstructor
@AllArgsConstructor
public class Color implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(50)", unique = true)
    private String colorName;
    @Column(columnDefinition = "VARCHAR(10)", unique = true)
    private String colorValue;
    private boolean available;

    @OneToMany(mappedBy = "color")
    @JsonIgnore
    private List<ProductEntry> productEntries;
    @Temporal(TemporalType.DATE)
    private Date createDate = new Date();


    public Color(String colorName, String colorValue, boolean available, Date createDate) {
        this.colorName = colorName;
        this.colorValue = colorValue;
        this.available = available;
        this.createDate = createDate;
    }
}
