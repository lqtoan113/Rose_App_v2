package com.rose.repositories;

import com.rose.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query(value = " Select sum(odt.total_price) as 'Today Income' from order_details odt \n" +
            " inner join orders o on o.id = odt.order_id  \n" +
            " where o.create_date = CAST( GETDATE() AS Date) AND o.status = 'ACCEPTED'", nativeQuery = true)
    Double getTodayIncome();
    @Query(value = " Select sum(odt.total_price) as 'Total Income' from order_details odt ", nativeQuery = true)
    Double getTotalIncome();
}