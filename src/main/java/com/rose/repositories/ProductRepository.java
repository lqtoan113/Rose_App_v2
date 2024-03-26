package com.rose.repositories;

import com.rose.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);
    Boolean existsProductByProductCode(String productCode);

    @Query(value = "select i.* from products i where i.product_code in(" +
            "select top 10 p.product_id from order_details o inner join products_entry p" +
            " on o.product_id = p.sku" +
            " group by p.product_id " +
            " order by sum(o.quantity) desc)", nativeQuery = true)
    List<Product> getTop10ProductBestSale();

    @Query("Select count(p) from Product p where p.available = true")
    Long getAvailable();


    @Query(value = "select  SUM(od.quantity) as N'Tổng sổ lượng bán ra', c.category_name from orders o \n" +
            "inner join order_details od on o.id=od.order_id\n" +
            "inner join products_entry pe on pe.sku = od.product_id\n" +
            "inner join category_products cp on cp.product_code = pe.product_id\n" +
            "inner join categories c on cp.category_code = c.category_code\n" +
            "where o.status != N'COMPLETED'\n" +
            "group by c.category_name"
            , nativeQuery = true)
    List<Object[]> numberOfProductSoldByType();


    @Query(value = "Select c.category_name, ISNULL(sum(odt.quantity),0) as 'Sold' from categories c  \n" +
            "\t\t\tinner join category_products cp on cp.category_code = c.category_code\n" +
            "\t\t\tinner join products p on p.product_code = cp.product_code \n" +
            "\t\t\tinner join products_entry pe on pe.product_id = p.product_code\n" +
            "\t\t\tinner join order_details odt on pe.sku = odt.product_id \n" +
            "\t\t\tinner join orders o on odt.order_id = o.Id \n" +
            "\t\t\tgroup by c.category_name", nativeQuery = true)
    List<Object[]> getPercentByCate();

    @Query(value = "with table1 as ( \n" +
            "\t\t\tSelect c.category_name as catename,  \n" +
            "\t\t\t\tcount(c.active) as active \n" +
            "\t\t\tfrom categories c \n" +
            "\t\t\t\tinner join category_products cp on c.category_code = cp.category_code\n" +
            "\t\t\t\tinner join products p on p.product_code = cp.product_code\n" +
            "\t\t\t\twhere p.available = 1 \n" +
            "\t\t\t\tgroup by c.category_name \n" +
            "\t\t\t), \n" +
            "\t\t\ttable2 as (\n" +
            "\t\t\t\tSelect c.category_name as catename,  \n" +
            "\t\t\t\tcount(c.active) as unactive \n" +
            "\t\t\t\tfrom categories c \n" +
            "\t\t\t\tinner join category_products cp on c.category_code = cp.category_code\n" +
            "\t\t\t\tinner join products p on p.product_code = cp.product_code\n" +
            "\t\t\t\twhere p.available = 0\n" +
            "\t\t\tgroup by c.category_name \n" +
            "\t\t\t) \n" +
            "\t\t\tSelect t1.catename,t1.active,t2.unactive  \n" +
            "\t\t\tfrom table1 t1 inner join table2 t2 on t1.catename = t2.catename", nativeQuery = true)
    List<Object[]> availableRate();

    @Query(value="Select top 10 p.product_name, count(odt.product_id) as mostSold \n" +
            "           From order_details odt inner join products_entry pe on pe.sku = odt.product_id\n" +
            "\t\t   inner join products p on p.product_code = pe.product_id\n" +
            "            group by p.product_name \n" +
            "            Order by mostSold desc",nativeQuery = true)
    List<Object> getTop10();
}