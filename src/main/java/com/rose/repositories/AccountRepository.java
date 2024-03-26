package com.rose.repositories;

import com.rose.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("SELECT o FROM Account o WHERE o.username = :keyword or o.email = :keyword ")
    Optional<Account> findAccountByUsernameOrEmail(@Param("keyword") String keyword);
    @Modifying
    @Transactional
    @Query("UPDATE Account o SET o.refreshToken = :refreshToken WHERE o.username = :username")
    void saveRefreshToken(@Param("username") String username, @Param("refreshToken") String refreshToken);

    Boolean existsByUsername(String username);

    Optional<Account> findAccountByRefreshToken(String refreshToken);

    Boolean existsByEmail(String email);

    @Query(value="Select a.username, a.full_name, a.email, a.photo, \n" +
            "\t\t\tsum(odt.total_price) as totalPayment \n" +
            "\t\t\tFrom accounts a inner join orders o on a.username = o.username \n" +
            "\t\t\tinner join order_details odt on o.id = odt.order_id \n" +
            "\t\t\tGroup by a.username, a.full_name, a.email, a.photo \n" +
            "\t\t\torder by totalPayment desc", nativeQuery = true)
    List<Object[]> top10Customer();

    @Query(value=" Select count(a.username) as 'Total Account' from accounts a ", nativeQuery = true)
    Long getToTalAccount();

   @Query(value = "select count(a.username) as 'Total User' from accounts a where a.active = 1", nativeQuery = true)
    Long getToTalAccountActive();

    @Query(value = "Select count(a.username) as 'Male User' from accounts a where a.gender = 1", nativeQuery = true)
    Long getTotalMaleUser();

    @Query(value = "Select count(a.username) as 'Male User' from accounts a where a.gender = 0", nativeQuery = true)
    Long getTotalFemaleUser();
}