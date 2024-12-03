package com.petalaura.library.Repository;

import com.petalaura.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where  p.is_deleted= false and p.is_activated= true")
    List<Product> getAllProduct();

    @Query("select p from Product p where p.name like %?1% or p.description like %?1%")
    List<Product> searchProductsList(String keyword);

    @Query("select  p from Product p")
    Page<Product> pageProducts(Pageable pageable);

    List<Product> findAllByCategoryId(long id);


    @Query("select p from Product p inner join Category c ON c.id = p.category.id" +
            " where p.category.name = ?1 and p.is_activated = true and p.is_deleted = false")
    List<Product> findAllByCategory(String category);




    @Query(value = "SELECT * FROM products WHERE is_deleted = false ORDER BY sale_price DESC", nativeQuery = true)
    Page<Product> filterHighProducts(Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE is_deleted = false ORDER BY sale_price ASC", nativeQuery = true)
    Page<Product> filterLowerProducts(Pageable pageable);



    @Query(value = "SELECT * FROM products where is_deleted = false and is_activated = true limit 4", nativeQuery = true)
    List<Product> listViewProduct();

    @Query(value = "SELECT p.* " +
            "FROM products p " +
            "JOIN (" +
            "  SELECT od.product_id, SUM(od.quantity) as total_sold " +
            "  FROM order_details od " +
            "  GROUP BY od.product_id " +
            "  ORDER BY total_sold DESC " +
            "  LIMIT 10" +
            ") top_selling ON p.product_id = top_selling.product_id " +
            "ORDER BY top_selling.total_sold DESC", nativeQuery = true)
    List<Product> findTopSellingProducts();

    @Query(value = "SELECT p.product_id, p.name, c.name, " +
            "SUM(od.quantity) AS total_quantity_ordered, SUM(od.quantity * p.cost_price) AS total_revenue " +
            "FROM products p " +
            "JOIN order_details od ON p.product_id = od.product_id " +
            "JOIN orders o ON od.order_id = o.order_id " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "WHERE o.order_status = 'Delivered' " +
            "AND o.order_date BETWEEN :startDate AND :endDate " +
            "GROUP BY p.product_id, p.name, c.name " +
            "ORDER BY total_revenue DESC", nativeQuery = true)
    List<Object[]> getProductsStatsForConfirmedOrdersBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT p.product_id, p.name, c.name, " +
            "SUM(od.quantity) AS total_quantity_ordered, SUM(od.quantity * p.cost_price) AS total_revenue " +
            "FROM products p " +
            "JOIN order_details od ON p.product_id = od.product_id " +
            "JOIN orders o ON od.order_id = o.order_id " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "WHERE o.order_status = 'Delivered' " +
            "GROUP BY p.product_id, p.name, c.name " +
            "ORDER BY total_revenue DESC", nativeQuery = true)
    List<Object[]> getProductStatsForConfirmedOrders();

    @Query("SELECT p FROM Product p WHERE p.is_activated = true AND p.is_deleted = false ORDER BY p.costPrice DESC")
    List<Product> filterHighPrice();

    @Query("SELECT p FROM Product p WHERE p.is_activated = true AND p.is_deleted = false ORDER BY p.costPrice DESC")
    List<Product> filterLowPrice();

    @Query(value = "SELECT * FROM products WHERE is_deleted = false ORDER BY name ASC", nativeQuery = true)
    Page<Product> filterByNameAscending(Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE is_deleted = false ORDER BY name DESC", nativeQuery = true)
    Page<Product> filterByNameDescending(Pageable pageable);

    List<Product>  findAllByCategoryName(String name);

    List<Product> findByNameStartingWithIgnoreCase(String prefix);

    @Query("SELECT p.id, p.name, c.name, SUM(od.quantity), SUM(od.totalPrice) FROM Product p JOIN p.category c JOIN OrderDetails od ON p.id = od.product.id GROUP BY p.id, p.name, c.name ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);


}


