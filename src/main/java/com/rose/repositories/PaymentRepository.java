package com.rose.repositories;

import com.rose.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    @Query(value = "select p.* from payments p left join orders o on p.rose_ref = o.id\n" +
            "where p.account_username = :username or o.username = :username \n" +
            "order by p.create_date desc", nativeQuery = true)
    List<Payment> getHistoryPaymentByAccount(@Param("username")String username);
}