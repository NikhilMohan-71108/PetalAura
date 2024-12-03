package com.petalaura.library.Service;

import com.petalaura.library.dto.CategoryDto;
import com.petalaura.library.model.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface  CategoryService {
    List<Category> findAll();
    Category save(Category category);
    Category update(Category category);
    void deleteById(Long id);
    void enableById(Long id);
    Category findById(Long id);
    List<Category> findAllByActivatedTrue();
    List<CategoryDto> getCategoriesAndSize();
    long countTotalProducts();
    List<Object[]> findTopSellingCategories(Pageable pageable);

    long countTotalCategories();
}
