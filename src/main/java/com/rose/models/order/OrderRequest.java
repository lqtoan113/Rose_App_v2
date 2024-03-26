package com.rose.models.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String address;
    private String numberPhone;
    private String paymentMethod;
    private List<ProductEntryOrder> orderDetails;
}
