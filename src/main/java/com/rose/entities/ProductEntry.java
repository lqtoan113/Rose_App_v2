package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rose.models.product.ProductEntryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "Products_Entry")
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntry implements Serializable {

    @Id
    @Column(name = "SKU", columnDefinition = "VARCHAR(20)")
    private String SKU;

    private Integer quantity;

    private Integer sold;

    private Double revenue;
    @Column(columnDefinition = "MONEY")
    private Double productPrice;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    private Boolean available;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(  name = "prod_entry_size",
            joinColumns = @JoinColumn(name = "SKU"),
            inverseJoinColumns = @JoinColumn(name = "size_id"))
    private Set<Size> sizes = new HashSet<>();

	@OneToMany(mappedBy = "product")
    @JsonIgnore
	private List<OrderDetail> orderDetails;

    public ProductEntry(ProductEntryDto entryDto, Integer sold, Double revenue){
        this.SKU = entryDto.getSku();
        this.quantity = entryDto.getQuantity();
        this.productPrice = entryDto.getProductPrice();
        this.available = entryDto.getAvailable();
        this.sold = sold;
        this.revenue = revenue;
    }
}
