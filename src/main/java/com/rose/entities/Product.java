package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rose.models.product.ProductRequest;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("serial")
@Getter
@Setter
@Entity
@EqualsAndHashCode(exclude = "productEntries")
@Table(name = "Products")
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable{
	@Id
	@Column( columnDefinition = "VARCHAR(10)")
	private String productCode;

	@Column(columnDefinition = "NVARCHAR(50)")
	private String productName;

	private Boolean available;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date createDate = new Date();

	@Column(columnDefinition = "NVARCHAR(255)")
	private String description;

	@Column(columnDefinition = "NVARCHAR(255)")
	private String imageUrl;

	private Integer sold;

	private Double revenue;

	private Double avgRate;
	@Column(columnDefinition = "MONEY")
	private Double commonPrice;

	@OneToMany(mappedBy = "product",fetch = FetchType.EAGER)
	private Set<ProductEntry> productEntries;

	@OneToMany(mappedBy = "product",fetch = FetchType.EAGER)
	private List<Comment> commentList;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "discount_code")
	private Discount discount;
	public Product (ProductRequest productRequest, Integer sold, Double revenue, double avgRate){
		this.productCode = productRequest.getProductCode();
		this.productName = productRequest.getProductName();
		this.available = productRequest.getAvailable();
		this.description = productRequest.getDescription();
		this.imageUrl = productRequest.getImageUrl();
		this.commonPrice = productRequest.getCommonPrice();
		this.sold = sold;
		this.revenue= revenue;
		this.avgRate = avgRate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Product product = (Product) o;
		return productCode != null && Objects.equals(productCode, product.productCode);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
