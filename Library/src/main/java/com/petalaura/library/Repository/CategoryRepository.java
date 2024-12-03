package com.petalaura.library.Repository;

import com.petalaura.library.dto.CategoryDto;
import com.petalaura.library.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository  extends JpaRepository<Category, Long> {
    Category findByNameIgnoreCase(String name);
    List<Category> findAllByActivatedTrue();
    @Query(value =  "select new com.petalaura.library.dto.CategoryDto(c.id, c.name) " +
            "from Category c left join Product p on c.id = p.category.id " +
            "where c.activated = true and c.deleted = false " +
            "group by c.id ")
    List<CategoryDto> getCategoriesAndSize();
    @Query("SELECT c.id, c.name, SUM(od.quantity) FROM Category c JOIN Product p ON c.id = p.category.id JOIN OrderDetails od ON p.id = od.product.id GROUP BY c.id, c.name ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findTopSellingCategories(Pageable pageable);

}
