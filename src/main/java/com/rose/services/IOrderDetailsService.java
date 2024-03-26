package com.rose.services;

import com.rose.entities.Order;
import com.rose.entities.OrderDetail;
import com.rose.models.order.ProductEntryOrder;

import java.util.stream.DoubleStream;

public interface IOrderDetailsService {
    OrderDetail createOrderDetail(ProductEntryOrder product, Order order);

    Double getTodayIncome();

    Double getTotalIncome();
}
