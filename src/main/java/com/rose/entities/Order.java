package com.rose.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rose.entities.enums.EOrder;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.utils.XDateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Data
@Entity 
@Table(name = "Orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double totalPriceOrder;

	private Double finalPriceOrder;

	@Column(columnDefinition = "VARCHAR(12)")
	private String phone;

	@Column(columnDefinition = "NVARCHAR(255)")
	private String address;

	@Enumerated(EnumType.STRING)
	private EOrder status;

	@Column(columnDefinition = "NVARCHAR(255)")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/YYYY HH:mm:ss")
	private Date createDate = new Date();
	@ManyToOne
	@JoinColumn(name = "username")
	@JsonIgnore
	private Account account;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
	private List<OrderDetail> orderDetailList;
	public Order(Account account){
		this.account = account;
		this.setStatus(EOrder.PENDING_ACCEPT);
	}
}