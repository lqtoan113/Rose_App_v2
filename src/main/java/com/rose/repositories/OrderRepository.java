package com.rose.repositories;

import com.rose.entities.Order;
import com.rose.entities.enums.EOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.account.username =:username")
    List<Order> getOrdersByUsername(String username);

    @Query("SELECT o FROM Order o WHERE o.account.username =:username and o.id=:id")
    Optional<Order> getOrderByUsernameAndId(String username, Long id);

    @Query(value = "Select count(*) from Orders o where o.create_date = CAST( GETDATE() AS Date)",nativeQuery= true)
    Long getTodayOrder();

    List<Order> getOrdersByStatus(EOrder order);

    @Query(value = "Select t.last7Days as 'date', ISNULL(sum(total_price),0) as ' totalPayment' \n" +
            "          From (Select cast(Getdate()as Date) last7Days\n" +
            "           Union all\n" +
            "          Select DateAdd(day,-1,cast(getdate()as date))\n" +
            "           Union all \n" +
            "           Select DateAdd(day,-2,cast(getdate()as date))\n" +
            "           Union all \n" +
            "           Select DateAdd(day,-3,cast(getdate()as date))\n" +
            "            Union all\n" +
            "           Select DateAdd(day,-4,cast(getdate()as date))\n" +
            "            Union all \n" +
            "           Select DateAdd(day,-5,cast(getdate()as date)) \n" +
            "            Union all \n" +
            "           Select DateAdd(day,-6,cast(getdate()as date)) \n" +
            "           Union all \n" +
            "            Select DateAdd(day,-7,cast(getdate()as date))) t Left Join Orders t1 on cast(t.last7Days as date) = Cast(t1.create_date as date) \n" +
            "            left join order_details dt on  t1.Id = dt.order_id \n" +
            "     Group by cast(t.last7Days as Date)",nativeQuery= true)
    List<Object[]> getRevenueLast7Days();

    @Query(value="select count(*) as 'Today accept order', sum(odt.total_price) as 'Total Price' from order_details odt inner join orders o on o.id = odt.order_id\n" +
            "Where o.status = 'ACCEPTED' and CONVERT(DATE,o.create_date) =  CONVERT(DATE, GETDATE())", nativeQuery=true)
    Long TodayAcceptedOrder();

    @Query(value="select count(*) as 'Today pending accept order', sum(odt.total_price) as 'Total Price' from order_details odt inner join orders o on o.id = odt.order_id\n" +
            "Where o.status = 'PENDING_ACCEPT' and CONVERT(DATE,o.create_date) =  CONVERT(DATE, GETDATE())", nativeQuery=true)
    Long TodayPendingAcceptOrder();

    @Query(value="select count(*) as 'Today shipping order', sum(odt.total_price) as 'Total Price' from order_details odt inner join orders o on o.id = odt.order_id\n" +
            "Where o.status = 'SHIPPING' and CONVERT(DATE,o.create_date) =  CONVERT(DATE, GETDATE())", nativeQuery=true)
    Long TodayShippingOrder();

    @Query(value="select count(*) as 'Today complete order', sum(odt.total_price) as 'Total Price' from order_details odt inner join orders o on o.id = odt.order_id\n" +
            "Where o.status = 'COMPLETED' and o.create_date = CAST( GETDATE() AS Date)", nativeQuery=true)
    Long TodayCompleteOrder();

    @Query(value="select count(*) as 'Today cancel order', sum(odt.total_price) as 'Total Price' from order_details odt inner join orders o on o.id = odt.order_id\n" +
            "Where o.status = 'CANCELLED' and CONVERT(DATE,o.create_date) =  CONVERT(DATE, GETDATE())", nativeQuery=true)
    Long TodayCancelOrder();
}