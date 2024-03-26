package com.rose.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("serial")
@Data
@Entity 
@Table(name = "Order_Details")
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double totalPrice;

	private Integer discountPercent;

	private Double finalPrice;
	private Integer quantity;

	@ManyToOne
	@JoinColumn(name = "size_id")
	private Size size;
	@ManyToOne
	@JoinColumn(name = "product_id")
	private ProductEntry product;
	@ManyToOne
	@JoinColumn(name = "order_id")
	@JsonIgnore
	private Order order;

	public OrderDetail(Order order){
		this.order = order;
	}
}
